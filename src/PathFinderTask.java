import dbTables.*;

import java.util.*;
import java.util.concurrent.Callable;

public class PathFinderTask implements Callable<JourneyRouteResult> {
    private final BusGraph graph;
    private final Map<String, Stop> addressMap;
    private final Map<String, String> routeNames;
    private final Map<String, Map<String, Double>> travelTimeMap;
    private final List<Stop> startStops;
    private final Stop end;
    private final String startTime;

    public PathFinderTask(BusGraph graph, Map<String, Stop> addressMap, Map<String, String> routeNames, Map<String, Map<String, Double>> travelTimeMap, List<Stop> startStops, Stop end, String startTime) {
        this.graph = graph;
        this.addressMap = addressMap;
        this.routeNames = routeNames;
        this.travelTimeMap = travelTimeMap;
        this.startStops = startStops;
        this.end = end;
        this.startTime = startTime;
    }

    @Override
    public JourneyRouteResult call() throws Exception {
        if (!addressMap.containsKey(end.stopId())) {
            System.err.println("Missing address data for end stop: " + end.stopId());
            return null;
        }

        Map<String, String> startTimes = new HashMap<>();
        List<String> startStopIds = new ArrayList<>();
        for (Stop start : startStops) {
            if (!addressMap.containsKey(start.stopId())) {
                System.err.println("Missing address data for start stop: " + start.stopId());
                continue;
            }
            startStopIds.add(start.stopId());
            startTimes.put(start.stopId(), startTime);
        }

        if (startStopIds.isEmpty()) {
            return null;
        }

        List<AStarWithTime.PathNode> path = AStarWithTime.findShortestPath(
                graph, startStopIds, end.stopId(), startTimes, addressMap, travelTimeMap
        );

        if (path != null && !path.isEmpty()) {
            for (AStarWithTime.PathNode node : path) {
                Stop stop = GTFSLoader.getStopDetails(node.previousStopId);
                if (stop == null) {
                    System.err.println("Stop details not found for stopId: " + node.previousStopId);
                    return null;
                }
                if (node.routeId != null && !routeNames.containsKey(node.routeId)) {
                    System.err.println("Route details not found for routeId: " + node.routeId);
                    return null;
                }
            }
            int travelTime = getTotalTravelTime(path, startTime);
            return new JourneyRouteResult(new JourneyRoute(path.get(0).previousStopId, end.stopId()), path, travelTime, startTime);
        }

        return null;
    }

    private static int getTotalTravelTime(List<AStarWithTime.PathNode> path, String startTime) {
        String finalArrivalTime = path.get(path.size() - 1).arrivalTime;
        return AStarWithTime.timeToSeconds(finalArrivalTime) - AStarWithTime.timeToSeconds(startTime);
    }
}
