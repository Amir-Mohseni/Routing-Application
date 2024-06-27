package dbTables;
import java.util.*;

public class AStarWithTime {
    private static final int TRANSFER_PENALTY = 300; // 5 minutes penalty for transfer
    private static final double AVERAGE_SPEED_KM_PER_HOUR = 20.0; //  Lower bound for Bus average speed
    private static final double MAX_TRAVEL_TIME_SECONDS = 2 * 60 * 60; // Maximum travel time of 2 hours converted to seconds
    private static final double EARTH_RADIUS_KM = 6378; // Earth radius in kilometers

    public static List<PathNode> findShortestPath(BusGraph graph, List<String> startStopIds, String endStopId, Map<String, String> startTimes, Map<String, Stop> addressMap, Map<String, Map<String, Double>> travelTimeMap) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Map<String, Double> gScore = new HashMap<>();
        Map<String, Double> fScore = new HashMap<>();
        Map<String, PathNode> cameFrom = new HashMap<>();
        Map<String, String> arrivalTimes = new HashMap<>();
        Map<String, String> tripIds = new HashMap<>();
        Map<String, String> routeIds = new HashMap<>();  // Track route IDs

        // Initialize start nodes
        for (String startStopId : startStopIds) {
            String startTime = startTimes.get(startStopId);
            gScore.put(startStopId, 0.0);
            fScore.put(startStopId, heuristic(startStopId, endStopId, addressMap, travelTimeMap));
            arrivalTimes.put(startStopId, startTime);
            tripIds.put(startStopId, null); // No trip ID at the start
            routeIds.put(startStopId, null); // No route ID at the start

            openSet.add(new Node(startStopId, fScore.get(startStopId), startTime));
        }

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            // Check if we've reached the goal
            if (current.stopId.equals(endStopId)) {
                return reconstructPath(cameFrom, current.stopId);
            }

            for (BusGraph.Edge neighbor : graph.getEdges(current.stopId)) {
                if (neighbor.toStopId == null) {
                    System.err.println("Encountered null neighbor toStopId for current stop: " + current.stopId);
                    continue;
                }

                String currentArrivalTime = arrivalTimes.get(current.stopId);

                // Handle null departure and arrival times
                String neighborDepartureTime = neighbor.departureTime != null ? neighbor.departureTime : currentArrivalTime;
                String neighborArrivalTime = neighbor.arrivalTime != null ? neighbor.arrivalTime : currentArrivalTime;

                // Handle walking between stops within the same parent station
                if (graph.getParentStop(current.stopId).equals(graph.getParentStop(neighbor.toStopId))) {
                    neighborArrivalTime = current.arrivalTime;
                    neighborDepartureTime = current.arrivalTime;

                    // Apply a smaller penalty for walking
                    double tentativeGScore = gScore.getOrDefault(current.stopId, Double.POSITIVE_INFINITY) + 60; // Assume 60 seconds penalty for walking
                    if (tentativeGScore < gScore.getOrDefault(neighbor.toStopId, Double.POSITIVE_INFINITY)) {
                        if (arrivalTimes.containsKey(neighbor.toStopId) && arrivalTimes.get(neighbor.toStopId).compareTo(neighborArrivalTime) < 0) {
                            continue;   // Skip if we already have a faster arrival time (earlier)
                        }

                        cameFrom.put(neighbor.toStopId, new PathNode(current.stopId, null, neighborDepartureTime, neighborArrivalTime, null));
                        gScore.put(neighbor.toStopId, tentativeGScore);
                        fScore.put(neighbor.toStopId, tentativeGScore + heuristic(neighbor.toStopId, endStopId, addressMap, travelTimeMap));
                        arrivalTimes.put(neighbor.toStopId, neighborArrivalTime);
                        tripIds.put(neighbor.toStopId, null);
                        routeIds.put(neighbor.toStopId, null);
                        openSet.add(new Node(neighbor.toStopId, fScore.get(neighbor.toStopId), neighborArrivalTime));
                    }
                    continue; // Skip the remaining part of the loop to process the next neighbor
                }

                // Skip if we arrive after the neighbor's departure time
                if (currentArrivalTime != null && timeToSeconds(currentArrivalTime) > timeToSeconds(neighborDepartureTime)) {
                    continue;
                }

                double tentativeGScore = gScore.getOrDefault(current.stopId, Double.POSITIVE_INFINITY) + neighbor.weight;

                // Apply transfer penalty if the trip ID or route ID changes and is not null
                boolean tripIdChanged = tripIds.get(current.stopId) != null && !tripIds.get(current.stopId).equals(neighbor.tripId) && neighbor.tripId != null;
                boolean routeIdChanged = routeIds.get(current.stopId) != null && !routeIds.get(current.stopId).equals(neighbor.routeId) && neighbor.routeId != null;
                if (tripIdChanged || routeIdChanged) {
                    tentativeGScore += TRANSFER_PENALTY;
                }

                // Skip if the tentative travel time exceeds 2 hours
                if (tentativeGScore > MAX_TRAVEL_TIME_SECONDS) {
                    continue;
                }

                if (tentativeGScore < gScore.getOrDefault(neighbor.toStopId, Double.POSITIVE_INFINITY)) {
                    if (arrivalTimes.containsKey(neighbor.toStopId) && arrivalTimes.get(neighbor.toStopId).compareTo(neighborArrivalTime) < 0) {
//                        System.out.println("already have a faster arrival time (earlier)"); // Skip if we already have a faster arrival time (earlier)
                        continue;
                    }
                    cameFrom.put(neighbor.toStopId, new PathNode(current.stopId, neighbor.tripId, neighborDepartureTime, neighborArrivalTime, neighbor.routeId));
                    gScore.put(neighbor.toStopId, tentativeGScore);
                    fScore.put(neighbor.toStopId, tentativeGScore + heuristic(neighbor.toStopId, endStopId, addressMap, travelTimeMap));
                    arrivalTimes.put(neighbor.toStopId, neighborArrivalTime);
                    tripIds.put(neighbor.toStopId, neighbor.tripId);
                    routeIds.put(neighbor.toStopId, neighbor.routeId); // Track the route ID
                    openSet.add(new Node(neighbor.toStopId, fScore.get(neighbor.toStopId), neighborArrivalTime));
                }
            }
        }

        return Collections.emptyList(); // Return an empty path if no path is found
    }

    private static double heuristic(String fromStopId, String toStopId, Map<String, Stop> addressMap, Map<String, Map<String, Double>> travelTimeMap) {
        Stop fromAddress = addressMap.get(fromStopId);
        Stop toAddress = addressMap.get(toStopId);

        if (fromAddress == null || toAddress == null) {
            System.err.println("Missing address data for stop. From: " + fromStopId + ", To: " + toStopId);
            return Double.POSITIVE_INFINITY;
        }

        // Use direct travel time data if available
        if (travelTimeMap.containsKey(fromStopId) && travelTimeMap.get(fromStopId).containsKey(toStopId)) {
            return travelTimeMap.get(fromStopId).get(toStopId);
        }

        // Fallback to geographic heuristic if no direct data available
        double distance = calculateDistance(fromAddress, toAddress);
        return estimateTravelTime(distance);
    }

    private static double calculateDistance(Stop start, Stop end) {
        double lat1Rad = degToRad(start.stopLat());
        double lon1Rad = degToRad(start.stopLon());
        double lat2Rad = degToRad(end.stopLat());
        double lon2Rad = degToRad(end.stopLon());

        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    private static double estimateTravelTime(double distance) {
        return (distance / AVERAGE_SPEED_KM_PER_HOUR) * 3600;
    }

    private static List<PathNode> reconstructPath(Map<String, PathNode> cameFrom, String current) {
        List<PathNode> path = new LinkedList<>();
        while (cameFrom.containsKey(current)) {
            PathNode node = cameFrom.get(current);
            path.add(node);
            current = node.previousStopId;
        }
        Collections.reverse(path);
        return path;
    }

    public static int timeToSeconds(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }

    public static double degToRad(double deg) {
        return deg * (Math.PI / 180);
    }

    public static class Node {
        String stopId;
        double fScore;
        String arrivalTime;

        Node(String stopId, double fScore, String arrivalTime) {
            this.stopId = stopId;
            this.fScore = fScore;
            this.arrivalTime = arrivalTime;
        }
    }

    public static class PathNode {
        public String previousStopId;
        public String tripId;
        public String departureTime;
        public String arrivalTime;
        public String routeId;

        public PathNode(String previousStopId, String tripId, String departureTime, String arrivalTime, String routeId) {
            this.previousStopId = previousStopId;
            this.tripId = tripId;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.routeId = routeId;
        }

        @Override
        public String toString() {
            return "From " + previousStopId + " to " + tripId + " (Route: " + routeId + ", Departs at: " + departureTime + ", Arrives at: " + arrivalTime + ")";
        }
    }
}