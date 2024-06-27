import dbTables.*;

import java.util.List;

import static dbTables.dbManager.*;

public class BusRouteFinder {
    private final PostAddress start;
    private final PostAddress end;

    public BusRouteFinder(PostAddress start, PostAddress end) {
        this.start = start;
        this.end = end;
    }

    public DirectRoute findShortestDirectBusRoute() {
        // Find bus stops near start and end
        List<Stop> closeToStartStops = findNearestBusStop(start);
        List<Stop> closeToEndStops = findNearestBusStop(end);

        if(closeToStartStops.isEmpty() || closeToEndStops.isEmpty()) {
            System.out.println("No bus stops found near start or end");
            return null;
        }

        // Find the shortest route
        RouteDetails routeDetails = findShortestRoute(closeToStartStops, closeToEndStops);
        if (routeDetails != null) {
            DirectRoute route = getAllStopsFromTripId(routeDetails.getTripId(), routeDetails.getStartStopSequence(), routeDetails.getEndStopSequence());

            assert route != null;

            return route;
        }
        else {
            System.out.println("No route found");
            return null;
        }
    }

    public TransferRoute findShortestTransferRoute() {
        // Find bus stops near start and end
        List<Stop> startStops = findNearestBusStop(start);
        List<Stop> endStops = findNearestBusStop(end);

        if(startStops.isEmpty() || endStops.isEmpty()) {
            System.out.println("No bus stops found near start or end");
            return null;
        }

        // Find the shortest route
        Route bestRoute = findBestTransferRouteWithoutStartTime(startStops, endStops);

        if (bestRoute != null) {
            DirectRoute fromRoute = getStopsFromRoute(bestRoute.getFromRoute());
            DirectRoute toRoute = getStopsFromRoute(bestRoute.getToRoute());
            return new TransferRoute(fromRoute, toRoute);
        }
        else {
            System.out.println("No transfer route found");
            return null;
        }
    }

    public static List<Stop> findNearestBusStop(PostAddress address) {
        // Find nearest bus stop
        return fetchStopsByCoords(address.getLat(), address.getLon());
    }

    public DirectRoute findShortestDirectBusRouteWithTime(String startTime) {
        // Find bus stops near start and end
        List<Stop> closeToStartStops = findNearestBusStop(start);
        List<Stop> closeToEndStops = findNearestBusStop(end);

        if(closeToStartStops.isEmpty() || closeToEndStops.isEmpty()) {
            System.out.println("No bus stops found near start or end");
            return null;
        }

        Route shortestDirectRoute = findShortestDirectRoute(closeToStartStops, closeToEndStops, startTime);

        if(shortestDirectRoute != null) {
            DirectRoute directRoute = getStopsFromRoute(shortestDirectRoute);
            assert directRoute != null;
            directRoute.setStartTime(startTime);
            return directRoute;
        }
        else {
            System.out.println("No route found");
            return null;

        }
    }

    public BusRoute findOverallShortestRoute() {
        // Find bus stops near start and end
        List<Stop> startStops = findNearestBusStop(start);
        List<Stop> endStops = findNearestBusStop(end);

        if(startStops.isEmpty() || endStops.isEmpty()) {
            System.out.println("No bus stops found near start or end");
            return null;
        }

        DirectRoute shortestDirectBusRoute = findShortestDirectBusRoute();
        TransferRoute shortestTransferRoute = findShortestTransferRoute();

        if(shortestDirectBusRoute != null && shortestTransferRoute != null) {
            System.out.println("Both direct and transfer route found");

            if(shortestDirectBusRoute.calculateTripTime() < shortestTransferRoute.calculateTripTime())
                return shortestDirectBusRoute;
            else
                return shortestTransferRoute;
        }
        else if (shortestTransferRoute != null) {
            System.out.println("Only transfer route found");
            return shortestTransferRoute;
        }
        else if (shortestDirectBusRoute != null) {
            System.out.println("Only direct route found");
            return shortestDirectBusRoute;
        }
        else {
            System.out.println("No route found");
            return null;
        }
    }

    public static void main(String[] args) {
//        testDirect();
//        testDirectWithTime();
        testOverallWithoutTime();
//        testTransfer();
    }

    public static void testOverallWithoutTime() {
        PostAddress start = AddressFinder.getAddress("6229GT");
        PostAddress end = AddressFinder.getAddress("6216EG");

        BusRouteFinder busRouteFinder = new BusRouteFinder(start, end);
        BusRoute shortestRoute = busRouteFinder.findOverallShortestRoute();

        if (shortestRoute != null) {
            System.out.println("Shortest route found");
            System.out.println("Trip time: " + shortestRoute.calculateTripTime() + " minutes");

            if (shortestRoute instanceof DirectRoute directRoute) {
                System.out.println("Direct route found");
                for (BusStop busStop : directRoute.getBusStops()) {
                    System.out.println(busStop.getStopName() + " " + busStop.getDepartureTime());
                }
            }
            else if (shortestRoute instanceof TransferRoute transferRoute) {
                System.out.println("Transfer route found");
                for (BusStop busStop : transferRoute.getStartBusStops()) {
                    System.out.println(busStop.getStopName() + " " + busStop.getDepartureTime() + " " + busStop.getStopId());
                }
                System.out.println("Transfer line: " + transferRoute.getTransferLine());
                for (BusStop busStop : transferRoute.getEndBusStops()) {
                    System.out.println(busStop.getStopName() + " " + busStop.getDepartureTime() + " " + busStop.getStopId());
                }
            }
        }
    }

    public static void testTransfer() {
        PostAddress start = AddressFinder.getAddress("6218BK");
        PostAddress end = AddressFinder.getAddress("6229EN");

        BusRouteFinder busRouteFinder = new BusRouteFinder(start, end);
        TransferRoute shortestTransferRoute = busRouteFinder.findShortestTransferRoute();

        if (shortestTransferRoute != null) {
            System.out.println("Shortest transfer route found");
            System.out.println("Trip time: " + shortestTransferRoute.calculateTripTime() + " minutes");

            for (BusStop busStop : shortestTransferRoute.getStartRoute().getBusStops()) {
                System.out.println(busStop.getStopName() + " " + busStop.getDepartureTime());
            }
            System.out.println("Transfer line: " + shortestTransferRoute.getTransferLine());
            for (BusStop busStop : shortestTransferRoute.getEndRoute().getBusStops()) {
                System.out.println(busStop.getStopName() + " " + busStop.getDepartureTime());
            }
        }
    }

    public static void testDirect() {
        PostAddress start = AddressFinder.getAddress("6213CR");
        PostAddress end = AddressFinder.getAddress("6215BC");

        BusRouteFinder busRouteFinder = new BusRouteFinder(start, end);
        DirectRoute shortestDirectRoute = busRouteFinder.findShortestDirectBusRoute();

        if (shortestDirectRoute != null) {
            System.out.println("Shortest route found");
            System.out.println("Trip time: " + shortestDirectRoute.calculateTripTime() + " minutes");

            for (BusStop busStop : shortestDirectRoute.getBusStops()) {
                System.out.println(busStop.getStopName() + " " + busStop.getDepartureTime());
            }
        }
    }

    public static void testDirectWithTime() {
        PostAddress start = AddressFinder.getAddress("6218BK");
        PostAddress end = AddressFinder.getAddress("6229EN");

        BusRouteFinder busRouteFinder = new BusRouteFinder(start, end);
        DirectRoute shortestDirectRoute = busRouteFinder.findShortestDirectBusRouteWithTime("08:00:00");

        if (shortestDirectRoute != null) {
            System.out.println("Shortest route found");
            System.out.println("Trip time: " + shortestDirectRoute.calculateTripTime() + " minutes");

            for (BusStop busStop : shortestDirectRoute.getBusStops()) {
                System.out.println(busStop.getStopName() + " " + busStop.getDepartureTime());
            }
        }
    }
}
