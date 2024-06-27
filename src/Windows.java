import dbTables.AStarWithTime;
import dbTables.AddressScore;
import dbTables.PostAddress;
import dbTables.Stop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Windows {


    private static Windows instance;

    private Windows() {

    }

    public static Windows getInstance() {
        if (instance == null) {
            instance = new Windows();
        }
        return instance;
    }


    //Opens this dialog when the Accessibility Score button gets pressed, gives option to find the score of a single postal code or all by leaving the input field empty, also choice to open the heat map that is created live
    void accessibilityScoreDialog() {
        JFrame frame = new JFrame("Accessibility Score Calculator");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new FlowLayout());


        JLabel postalCodeLabel = new JLabel("Postal Code:");
        frame.add(postalCodeLabel);


        JTextField postalCodeTextField = new JTextField(20);
        frame.add(postalCodeTextField);


        JButton submitButton = new JButton("Submit");
        frame.add(submitButton);


        JButton heatMapButton = new JButton("HeatMap");
        frame.add(heatMapButton);


        List<AddressScore> scores = new ArrayList<>();


        GUI gui = GUI.getInstance();
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String postalCode = postalCodeTextField.getText();
                PostAddress postAddress = GUI.getAddressFromDataManager(postalCode);
                if (postAddress != null) {

                    AmenitiesCalculator.calculateAllScores(scores);
                    AddressScore addressScore = AmenitiesCalculator.getAddressScore(postAddress.getPostalCode(), scores);

                    if (addressScore != null) {

                        frame.dispose();
                        displayScores(postalCode, addressScore.getShopScore(), addressScore.getAmenityScore(), addressScore.getTourismScore(), addressScore.getScore());
                    } else {
                        JOptionPane.showMessageDialog(frame, "Score not found for the given postal code", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    AmenitiesCalculator.calculateAllScores(scores);
                    displayAllScores(scores);
                }
            }
        });


        heatMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                AmenitiesCalculator.calculateAllScores(scores);
                DrawingUtils utils = new DrawingUtils();
                utils.createHeatMap(scores);
                GUI.getInstance().repaint();

            }
        });


        frame.setVisible(true);
    }


    //Displays all the postal codes as well as their scores, calculated live
    private void displayAllScores(List<AddressScore> scores) {
        DecimalFormat df = new DecimalFormat("#.###");

        JFrame resultFrame = new JFrame("All Accessibility Scores");
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultFrame.setSize(800, 400);
        resultFrame.setLayout(new GridBagLayout());
        resultFrame.setResizable(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        gbc.gridx = 0;
        gbc.gridy = 0;
        resultFrame.add(new JLabel("Postal Code"), gbc);

        gbc.gridx = 1;
        resultFrame.add(new JLabel("Shop Score"), gbc);

        gbc.gridx = 2;
        resultFrame.add(new JLabel("Amenity Score"), gbc);

        gbc.gridx = 3;
        resultFrame.add(new JLabel("Tourism Score"), gbc);

        gbc.gridx = 4;
        resultFrame.add(new JLabel("Total Score"), gbc);


        for (int i = 0; i < scores.size(); i++) {
            AddressScore addressScore = scores.get(i);

            gbc.gridy = i + 1;
            gbc.gridx = 0;
            resultFrame.add(new JLabel(addressScore.getAddress().getPostalCode()), gbc);

            gbc.gridx = 1;
            resultFrame.add(new JLabel(df.format(addressScore.getShopScore())), gbc);

            gbc.gridx = 2;
            resultFrame.add(new JLabel(df.format(addressScore.getAmenityScore())), gbc);

            gbc.gridx = 3;
            resultFrame.add(new JLabel(df.format(addressScore.getTourismScore())), gbc);

            gbc.gridx = 4;
            resultFrame.add(new JLabel(df.format(addressScore.getScore())), gbc);
        }


        JScrollPane scrollPane = new JScrollPane(resultFrame.getContentPane());
        JFrame scrollableFrame = new JFrame("All Accessibility Scores");
        scrollableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        scrollableFrame.setSize(800, 400);
        scrollableFrame.add(scrollPane);
        scrollableFrame.setVisible(true);
    }


    //similar to displayAllScores but for a single one
    private static void displayScores(String postalCode, double shopScore, double amenityScore, double tourismScore, double totalScore) {
        DecimalFormat df = new DecimalFormat("#.###");

        JFrame resultFrame = new JFrame("Accessibility Scores");
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultFrame.setSize(600, 120);
        resultFrame.setLayout(new GridBagLayout());
        resultFrame.setResizable(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        gbc.gridx = 0;
        gbc.gridy = 0;
        resultFrame.add(new JLabel("Postal Code:"), gbc);

        gbc.gridx = 1;
        resultFrame.add(new JLabel("Shop Score:"), gbc);

        gbc.gridx = 2;
        resultFrame.add(new JLabel("Amenity Score:"), gbc);

        gbc.gridx = 3;
        resultFrame.add(new JLabel("Tourism Score:"), gbc);

        gbc.gridx = 4;
        resultFrame.add(new JLabel("Total Score:"), gbc);


        gbc.gridy = 1;
        gbc.gridx = 0;
        resultFrame.add(new JLabel(postalCode), gbc);

        gbc.gridx = 1;
        resultFrame.add(new JLabel(df.format(shopScore)), gbc);

        gbc.gridx = 2;
        resultFrame.add(new JLabel(df.format(amenityScore)), gbc);

        gbc.gridx = 3;
        resultFrame.add(new JLabel(df.format(tourismScore)), gbc);

        gbc.gridx = 4;
        resultFrame.add(new JLabel(df.format(totalScore)), gbc);


        resultFrame.setVisible(true);
    }


    //Allows the user to choose between transfer or non and timed or non bus route between two postal codes
    Object[] showBusRouteOptionsDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JLabel transferLabel = new JLabel("Allow Transfers?");
        JCheckBox transferCheckBox = new JCheckBox();

        JRadioButton currentTimeButton = new JRadioButton("Use Current Time");
        JRadioButton manualTimeButton = new JRadioButton("Enter Manual Time");
        ButtonGroup timeOptionGroup = new ButtonGroup();
        timeOptionGroup.add(currentTimeButton);
        timeOptionGroup.add(manualTimeButton);
        currentTimeButton.setSelected(true);

        JPanel timePanel = new JPanel(new GridLayout(1, 6));
        JLabel hourLabel = new JLabel("HH:");
        JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        JLabel minuteLabel = new JLabel("mm:");
        JSpinner minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        JLabel secondLabel = new JLabel("ss:");
        JSpinner secondSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));

        timePanel.add(hourLabel);
        timePanel.add(hourSpinner);
        timePanel.add(minuteLabel);
        timePanel.add(minuteSpinner);
        timePanel.add(secondLabel);
        timePanel.add(secondSpinner);


        hourSpinner.setEnabled(false);
        minuteSpinner.setEnabled(false);
        secondSpinner.setEnabled(false);


        currentTimeButton.addActionListener(e -> {
            hourSpinner.setEnabled(false);
            minuteSpinner.setEnabled(false);
            secondSpinner.setEnabled(false);
        });

        manualTimeButton.addActionListener(e -> {
            hourSpinner.setEnabled(true);
            minuteSpinner.setEnabled(true);
            secondSpinner.setEnabled(true);
        });

        panel.add(transferLabel);
        panel.add(transferCheckBox);
        panel.add(currentTimeButton);
        panel.add(manualTimeButton);
        panel.add(timePanel);

        int result = JOptionPane.showConfirmDialog(null, panel, "Bus Route Options", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            return new Object[]{transferCheckBox, currentTimeButton, hourSpinner, minuteSpinner, secondSpinner};
        }
        return null;
    }


    // summarizes the data from the Transfer route, by creating a new window that contains stop information, transfer position and the duration of the trip
    void showBusStopsPopupTransfer(List<Stop> totalStops, List<Integer> transferIndices, int travelTime, List<AStarWithTime.PathNode> path, String endStopId) {
        if (totalStops == null || totalStops.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No stops available", "Bus Route", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder stopNames = new StringBuilder("Bus Stops with Transfers:\n");
        Set<String> uniqueNames = new HashSet<>();
        List<String> times = GUI.getInstance().getTimes(path, endStopId);

        int index = 0;
        for (int i = 0; i < totalStops.size(); i++) {
            Stop stop = totalStops.get(i);
            String stopName = stop.stopName();


            boolean isTransfer = transferIndices.contains(i);


            if (!uniqueNames.contains(stopName)) {
                stopNames.append(stopName);
                if (i != 0 && i != totalStops.size() - 1) {
                    stopNames.append(" at: ").append(times.get(index), 0, 5);
                    index++;
                }
                uniqueNames.add(stopName);

                if (isTransfer) {
                    stopNames.append(" (Transfer)");
                }
                stopNames.append("\n");
            }
        }

        stopNames.append("\nTotal Travel Time: ").append(travelTime / 60).append(" minutes");

        JOptionPane.showMessageDialog(null, stopNames.toString(), "Bus Route", JOptionPane.INFORMATION_MESSAGE);
    }

    // Shows simple distance and time for the straight distance and non-bus route pathfinding algorithm
    void showAlgorithmDistanceMessage(long time, double distance) {
        int timeInSeconds = (int) (time / 1000);
        int hours = (timeInSeconds / 3600);
        int minutes = ((timeInSeconds % 3600) / 60);
        int seconds = (timeInSeconds % 60);

        String distanceMessage = String.format("Distance: %.2f km\n", distance);

        String timeMessage;

        if (time != 0) {
            String hoursMessage = hours != 0 ? hours + " hours " : "";
            String minutesMessage = minutes != 0 ? minutes + " minutes " : "";
            String secondsMessage = seconds != 0 ? seconds + " seconds" : "";

            timeMessage = hoursMessage + minutesMessage + secondsMessage;
        } else {
            timeMessage = "Not Applicable";
        }

        // Debug prints to verify the flow
        System.out.println("Distance: " + distance);
        System.out.println("Time Message: " + timeMessage);

        JOptionPane.showMessageDialog(null, distanceMessage + "Time: " + timeMessage);
    }


}
