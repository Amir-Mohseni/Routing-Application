import dbTables.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RoutingApplication {

    public static void main(String[] args) {
        // Test with and without start time
        testWithTime();
    }

    public static void testWithTime() {
        try {
            // Test postal codes
            String startPostalCode = "6213BE";
            String endPostalCode = "6225EJ";
            String startTime = "23:50:00";
            JourneyRouteResult result = findBestRoute(startPostalCode, endPostalCode, startTime);
            if (result != null) {
                printPathDetails(result.path, result.route.startStopId, result.route.endStopId, startTime);
            } else {
                System.out.println("No path found between the given stops.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static JourneyRouteResult findBestRoute(String startPostalCode, String endPostalCode, String startTime) throws Exception {
        BusGraph graph = GTFSLoader.getGraph(); // Use getGraph() to get the preloaded graph
        if (graph == null) {
            throw new Exception("Failed to load graph");
        }

        Map<String, Stop> addressMap = GTFSLoader.fetchAllAddresses();
        if (addressMap.isEmpty()) {
            throw new Exception("Failed to load addresses");
        }

        Map<String, String> routeNames = GTFSLoader.fetchAllRouteNames();
        if (routeNames.isEmpty()) {
            throw new Exception("Failed to load route names");
        }

        Map<String, Map<String, Double>> travelTimeMap = GTFSLoader.fetchTravelTimes();
        if (travelTimeMap.isEmpty()) {
            throw new Exception("Failed to load travel times");
        }

        List<Stop> startStops = getStopsByPostalCode(startPostalCode);
        List<Stop> endStops = getStopsByPostalCode(endPostalCode);

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<JourneyRouteResult>> results = new ArrayList<>();

        for (Stop end : endStops) {
            results.add(executor.submit(new PathFinderTask(graph, addressMap, routeNames, travelTimeMap, startStops, end, startTime)));
        }

        JourneyRoute bestRoute = null;
        List<AStarWithTime.PathNode> bestPath = null;
        int bestTime = Integer.MAX_VALUE;

        for (Future<JourneyRouteResult> result : results) {
            try {
                JourneyRouteResult routeResult = result.get();
                if (routeResult != null && routeResult.travelTime < bestTime) {
                    bestRoute = routeResult.route;
                    bestPath = routeResult.path;
                    bestTime = routeResult.travelTime;
                }
            } catch (Exception e) {
                System.err.println("Error processing routing result: " + e.getMessage());
            }
        }

        executor.shutdown();

        if (bestPath != null) {
            return new JourneyRouteResult(bestRoute, bestPath, bestTime, startTime);
        } else {
            return null;
        }
    }

    private static List<Stop> getStopsByPostalCode(String postalCode) {
        PostAddress address = AddressFinder.getAddress(postalCode);
        if (address == null) {
            System.err.println("No address found for postal code: " + postalCode);
            return Collections.emptyList();
        }
        return BusRouteFinder.findNearestBusStop(address);
    }

    public static void printPathDetails(List<AStarWithTime.PathNode> path, String startStopId, String endStopId, String startTime) {
        System.out.println("Shortest path from " + startStopId + " to " + endStopId + " starting at " + startTime + ":");

        String previousTripId = null;
        String finalArrivalTime = null;
        int totalTravelTimeInSeconds;

        for (int i = 0; i < path.size(); i++) {
            AStarWithTime.PathNode node = path.get(i);
            Stop stop = GTFSLoader.getStopDetails(node.previousStopId);

            if (previousTripId == null || !previousTripId.equals(node.tripId)) {
                if (previousTripId != null) {
                    if (node.tripId == null) {
                        System.out.println("Walk to another stop within the same parent station: " + (stop != null ? stop.stopName() : node.previousStopId));
                    } else {
                        System.out.println("Transfer to Route " + node.routeId + " at Stop: " + (stop != null ? stop.stopName() : node.previousStopId));
                    }
                }
                previousTripId = node.tripId;
            }

            String nextStopId = (i + 1 < path.size()) ? path.get(i + 1).previousStopId : endStopId;

            // Print all bus stops for this segment using GTFS times
            List<BusStop> segmentStops = GTFSLoader.getBusStopsForTrip(node.tripId, node.previousStopId, nextStopId);
            for (BusStop segmentStop : segmentStops) {
                System.out.println("Stop: " + segmentStop.getStopName() +
                        ", Arrival: " + segmentStop.getArrivalTime() +
                        ", Departure: " + segmentStop.getDepartureTime() +
                        ", Route: " + node.routeId +
                        ", Trip id: " + node.tripId);
            }

            finalArrivalTime = node.arrivalTime;
        }

        // Print final stop name
        Stop stop = GTFSLoader.getStopDetails(endStopId);
        System.out.println("Stop: " + (stop != null ? stop.stopName() : endStopId) + ", Arrival: " + finalArrivalTime);

        // Calculate total travel time from the startTime to the final arrival time
        if (finalArrivalTime != null) {
            totalTravelTimeInSeconds = AStarWithTime.timeToSeconds(finalArrivalTime) - AStarWithTime.timeToSeconds(startTime);

            int hours = totalTravelTimeInSeconds / 3600;
            int minutes = (totalTravelTimeInSeconds % 3600) / 60;
            int seconds = totalTravelTimeInSeconds % 60;

            System.out.println("Total travel time: " + String.format("%02d:%02d:%02d", hours, minutes, seconds));
        } else {
            System.out.println("Total travel time: N/A");
        }
    }
}