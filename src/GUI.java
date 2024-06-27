import dbTables.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class GUI extends JFrame {


    // used to store the input field data
    public static String FromCode = "FROM";
    public static String ToCode = "TO";
    public static VehicleType currentVehicle = VehicleType.FOOT;

    // Singleton instance
    private static GUI instance;

    // Private constructor to prevent instantiation
    private GUI() {
        // create Main window
        setSize(900, 600);
        setResizable(false);
        setTitle("Maastricht Route Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        PanelElements controller = PanelElements.getInstance();
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        // create map panel
        JPanel controlPanel = controller.createControlPanel();
        JPanel mapPanel = controller.createMapPanel();

        // create utility panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controlPanel, mapPanel);
        splitPane.setDividerLocation(200);
        splitPane.setEnabled(false);
        splitPane.setOneTouchExpandable(false);

        // assemble Main Frame
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);

        // Setup Action Listeners
        setupActionListeners();
        setVisible(true);
    }

    // Public method to return the single instance of the class
    public static synchronized GUI getInstance() {
        if (instance == null) {
            instance = new GUI();

        }
        return instance;
    }

    // Main method that creates GUI object
    public static void main(String[] args) {
        try {
            CompletableFuture<Void> guiFuture = CompletableFuture.runAsync(() -> {
                SwingUtilities.invokeLater(() -> {
                    GUI frame = GUI.getInstance();
                    frame.setVisible(true);
                });
            });

            CompletableFuture<Void> gtfsLoaderFuture = CompletableFuture.runAsync(GTFSLoader::loadGraph);

            CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(guiFuture, gtfsLoaderFuture);
            combinedFuture.join();  // Wait for both tasks to complete
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while initializing the GUI or loading the graph");
        }
    }


    private double getDistance() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return LineDistanceCalculator.basicDistances(getAddressFromDataManager(FromCode), getAddressFromDataManager(ToCode));
    }


    //process the data from the showBusRouteOptionsDialog using the boolean transfer value and the time that was given through the spinner objects
    private void processBusRouteOptions(JCheckBox transferCheckBox, JRadioButton currentTimeButton, JSpinner hourSpinner, JSpinner minuteSpinner, JSpinner secondSpinner) throws Exception {
        boolean allowTransfers = transferCheckBox.isSelected();
        String preferredTime;

        PanelElements controller = PanelElements.getInstance();
        if (currentTimeButton.isSelected()) {
            preferredTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        } else {
            int hours = (int) hourSpinner.getValue();
            int minutes = (int) minuteSpinner.getValue();
            int seconds = (int) secondSpinner.getValue();
            preferredTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }

        boolean accept = buttonClickSharedOperations(controller.getPostCodeFromField(), controller.getPostCodeToField(), false);
        if (!accept) return;

        PostAddress first = getAddressFromDataManager(FromCode);
        PostAddress last = getAddressFromDataManager(ToCode);

        if (!allowTransfers) {
            handleDirectBusRoute(first, last, preferredTime);
        } else {
            handleTransfers(first, last, preferredTime);
        }
        controller.untoggleAllButtons();
    }

    //get the arrival times for each stop in the transfer method
    public List<String> getTimes(List<AStarWithTime.PathNode> path, String endStopId) {
        String finalArrivalTime = null;
        List<String> times = new ArrayList<>();
        for (int i = 0; i < path.size(); i++) {
            AStarWithTime.PathNode node = path.get(i);
            String nextStopId = (i + 1 < path.size()) ? path.get(i + 1).previousStopId : endStopId;
            List<BusStop> segmentStops = GTFSLoader.getBusStopsForTrip(node.tripId, node.previousStopId, nextStopId);
            for (BusStop segmentStop : segmentStops) {
                times.add(segmentStop.getArrivalTime());
            }
            finalArrivalTime = node.arrivalTime;
        }
        Stop stop = GTFSLoader.getStopDetails(endStopId);
        times.add(finalArrivalTime);
        return times;

    }

    private void handleTransfers(PostAddress first, PostAddress last, String preferredTime) throws Exception {
        JourneyRouteResult result = RoutingApplication.findBestRoute(first.getPostalCode(), last.getPostalCode(), preferredTime);

        DrawingUtils utils = new DrawingUtils();


        if (result == null) {
            noBusError();
            return;
        }

        List<AStarWithTime.PathNode> path = result.path;
        String endStopId = result.route.endStopId;

        if (path == null) {
            noBusError();
            return;
        }

        List<Stop> totalStops = new ArrayList<>();
        List<Integer> transferIndices = new ArrayList<>();
        RoutingApplication.printPathDetails(path, first.getPostalCode(), last.getPostalCode(), preferredTime);
        Stop startStop = createStopFromPostalCode(first);
        totalStops.add(startStop);
        String previousTripId = null;
        int index = 1;

        for (int i = 0; i < path.size(); i++) {
            AStarWithTime.PathNode node = path.get(i);
            Stop stop = GTFSLoader.getStopDetails(node.previousStopId);

            if (stop != null) {
                if (previousTripId != null && !previousTripId.equals(node.tripId)) {
                    transferIndices.add(totalStops.size() - 1);
                }
                totalStops.add(stop);
                previousTripId = node.tripId;
                index++;
            }
            String nextStopId = (i + 1 < path.size()) ? path.get(i + 1).previousStopId : endStopId;
            if (nextStopId != null) {
                List<BusStop> segmentStops = GTFSLoader.getBusStopsForTrip(node.tripId, node.previousStopId, nextStopId);
                for (BusStop segmentStop : segmentStops) {
                    Stop intermediateStop = new Stop(segmentStop.getStopName(), segmentStop.getStopName(), segmentStop.getStopLat(), segmentStop.getStopLon());
                    totalStops.add(intermediateStop);
                }
            }
        }


        Stop endStop = createStopFromPostalCode(last);
        totalStops.add(endStop);
        utils.drawShortestPathOnMapBusRouteTransfer(totalStops, transferIndices);
        Windows popupController = Windows.getInstance();
        popupController.showBusStopsPopupTransfer(totalStops, transferIndices, result.travelTime, result.path, result.route.endStopId);
    }


    //translate PostAddress objects to Stop objects
    private Stop createStopFromPostalCode(PostAddress address) {
        return new Stop(address.getPostalCode(), address.getPostalCode(), address.getLat(), address.getLon());
    }

    //translates PostAddress object to BusStop Object
    private BusStop createBusStopFromPostalCode(PostAddress postalCode) {
        return new BusStop("0", 0, postalCode.getPostalCode(), null, null, (float) postalCode.getLat(), (float) postalCode.getLon(), null);
    }

    //Creates Bus Route and Calls the Drawing method for non transfer routes
    private void handleDirectBusRoute(PostAddress first, PostAddress last, String preferredTime) {
        DrawingUtils util = new DrawingUtils();
        BusRouteFinder finder = new BusRouteFinder(first, last);
        DirectRoute directRoute = finder.findShortestDirectBusRouteWithTime(preferredTime);
        if (directRoute == null) {
            noBusError();
            return;
        }
        directRoute.getBusStops().add(0, createBusStopFromPostalCode(first));
        directRoute.getBusStops().add(createBusStopFromPostalCode(last));
        util.drawShortestPathOnMapBusRoute(directRoute);
        showBusStopsPopup(directRoute);
    }


    private void noBusError() {
        JOptionPane.showMessageDialog(null, "There is no bus route that connects these two postal codes");
        PanelElements controller = PanelElements.getInstance();
        controller.untoggleAllButtons();
    }


    //summarizes the Bus Route without transfer by showing the line, bus stops that are passed though, duration and line number
    private void showBusStopsPopup(DirectRoute route) {
        String routeName = route.getBusStops().get(1).getRouteName();
        StringBuilder stopNames = new StringBuilder("Bus Stops for line (" + routeName + "):\n");
        for (BusStop busStop : route.getBusStops()) {
            if (busStop.getArrivalTime() == null) {
                stopNames.append(busStop.getStopName()).append("\n");
            } else {
                stopNames.append(busStop.getStopName()).append(" at : ").append(busStop.getArrivalTime()).append("\n");
            }
        }
        String firstArrivalTime = route.getBusStops().get(1).getArrivalTime();
        String lastDepartureTime = route.getBusStops().get(route.getBusStops().size() - 2).getDepartureTime();
        long totalTimeMinutes = calculateTimeDifference(firstArrivalTime, lastDepartureTime);

        stopNames.append("\nTotal Travel Time: ").append(totalTimeMinutes + 5).append(" minutes");

        JOptionPane.showMessageDialog(null, stopNames.toString(), "Bus Route", JOptionPane.INFORMATION_MESSAGE);
    }

    //calculates the duration of the bus Route
    private long calculateTimeDifference(String startTime, String endTime) {
        if (startTime == null || endTime == null) {
            return 0;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime start = LocalTime.parse(startTime, formatter);
        LocalTime end = LocalTime.parse(endTime, formatter);
        return ChronoUnit.MINUTES.between(start, end);
    }


    //Controls the Input, which is the same operation for all buttons since we care about validity
    private boolean buttonClickSharedOperations(JFormattedTextField postCodeFromField, JFormattedTextField postCodeToField, boolean careForVehicle) {
        DrawingUtils util = new DrawingUtils();
        PanelElements controller = PanelElements.getInstance();
        util.DrawBaseImage(util.getMapGraphics());
        boolean accept = true;
        repaint();

        String codeToString = postCodeToField.getText().replace(" ", "");
        String codeFromString = postCodeFromField.getText().replace(" ", "");


        accept = buttonClickConditionals(codeFromString, codeToString);
        boolean vehicleFlag = true;
        if (careForVehicle && controller.getSelectedVehicle() == null) {
            vehicleFlag = false;
            JOptionPane.showMessageDialog(null, "Foreign vehicle or none selected. \nPlease select another and try again");
        }
        accept = accept && vehicleFlag;
        if (accept) {
            ToCode = codeToString;
            FromCode = codeFromString;
        }
        return accept;
    }


    //Checks for illegal postal codes and gives the proper error messages
    private boolean buttonClickConditionals(String codeFrom, String codeTo) {
        if (!acceptCode(codeTo)) {
            JOptionPane.showMessageDialog(null, "The \"TO\" PostCode is Not in the proper format\nFormat: 1234AB or 1234 AB");
            return false;
        }
        if (!acceptCode(codeFrom)) {
            JOptionPane.showMessageDialog(null, "The \"FROM\" PostCode is Not in the proper format\nFormat: 1234AB or 1234 AB");
            return false;
        }
        if (codeTo.equals(codeFrom)) {
            JOptionPane.showMessageDialog(null, "The Post Codes are the same\n No distance between them");
            return false;
        }
        if (getAddressFromDataManager(codeFrom) == null) {
            JOptionPane.showMessageDialog(null, "The \"FROM\" PostCode is not valid");
            return false;
        }
        if (getAddressFromDataManager(codeTo) == null) {
            JOptionPane.showMessageDialog(null, "The \"TO\" PostCode is not valid");
            return false;
        }
        return true;
    }


    private void runPathFindingAlgorithm(PostAddress from, PostAddress to) {
        GraphHopperUtil graphHopperUtil = new GraphHopperUtil();
        PanelElements controller = PanelElements.getInstance();
        QueryResponse queryResponse = graphHopperUtil.calculateRoute(from.getPostalCode(), to.getPostalCode(), controller.getSelectedVehicle().toLowerCase());
        System.out.println(controller.getSelectedVehicle().toLowerCase());

        ArrayList<PostAddress> shortestPath = queryResponse.path();
        DrawingUtils util = new DrawingUtils();
        util.drawShortestPathOnMap(shortestPath);

        double distance = queryResponse.distance() / 1000;
        long time = queryResponse.time();

        distance = Double.parseDouble(new DecimalFormat("##.##").format(distance));
        Windows popupController = Windows.getInstance();
        popupController.showAlgorithmDistanceMessage(time, distance);
    }


    //sets up action Listeners for the buttons in the main frame
    void setupActionListeners() {

        PanelElements controller = PanelElements.getInstance();

        controller.getGoButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean accept = buttonClickSharedOperations(controller.getPostCodeFromField(), controller.getPostCodeToField(), true);
                if (!accept) return;
                PostAddress fromAddress = getAddressFromDataManager(FromCode);
                PostAddress toAddress = getAddressFromDataManager(ToCode);
                DrawingUtils utils = new DrawingUtils();
                utils.drawStraightLine(fromAddress, toAddress);
                double distance = getDistance();
                Windows popupController = Windows.getInstance();

                popupController.showAlgorithmDistanceMessage(0, distance);
            }
        });


        controller.getAlgorithmButton().addActionListener(e -> {
            boolean accept = buttonClickSharedOperations(controller.getPostCodeFromField(), controller.getPostCodeToField(), true);
            if (!accept) return;
            try {
                runPathFindingAlgorithm(getAddressFromDataManager(FromCode), getAddressFromDataManager(ToCode));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });


        controller.getBusRouteButton().addActionListener(e -> {
            controller.untoggleAllButtons();
            Windows popupController = Windows.getInstance();

            Object[] options = popupController.showBusRouteOptionsDialog();
            if (options != null) {
                try {
                    processBusRouteOptions((JCheckBox) options[0], (JRadioButton) options[1], (JSpinner) options[2], (JSpinner) options[3], (JSpinner) options[4]);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        controller.getAccessibilityButton().addActionListener(e -> {
            Windows popupController = Windows.getInstance();
            popupController.accessibilityScoreDialog();
        });


    }


    //Takes a String and returns the corresponding PostAddress
    public static PostAddress getAddressFromDataManager(String postalCode) {
        try {
            return AddressFinder.getAddress(postalCode);
        } catch (Exception e) {
            System.err.println("Error getting address from postal code: " + postalCode);
            return null;
        }
    }

    //decline postal code if not 6 characters
    public boolean acceptCode(String code) {
        return code.length() == 6;
    }


}