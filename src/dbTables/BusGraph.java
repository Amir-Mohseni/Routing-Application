package dbTables;

import java.util.*;

public class BusGraph {
    final Map<String, List<Edge>> adjList = new HashMap<>();
    final Map<String, String> parentStopMap = new HashMap<>(); // New map to store parent stops

    // Method to add an edge to the graph
    public void addEdge(String fromStopId, String toStopId, int weight, String tripId, String departureTime, String arrivalTime, String routeId) {
        adjList.computeIfAbsent(fromStopId, k -> new ArrayList<>()).add(new Edge(toStopId, weight, tripId, departureTime, arrivalTime, routeId));
    }

    // Method to get edges from a given stop
    public List<Edge> getEdges(String stopId) {
        return adjList.getOrDefault(stopId, new ArrayList<>());
    }

    // New method to set parent stops
    public void setParentStop(String stopId, String parentStopId) {
        parentStopMap.put(stopId, parentStopId);
    }

    // New method to get the parent stop
    public String getParentStop(String stopId) {
        return parentStopMap.get(stopId);
    }

    // Edge class representing the connection between stops
    public static class Edge {
        public String toStopId;
        public int weight; // Travel time in seconds
        public String tripId;
        public String departureTime;
        public String arrivalTime;
        public String routeId;

        public Edge(String toStopId, int weight, String tripId, String departureTime, String arrivalTime, String routeId) {
            this.toStopId = toStopId;
            this.weight = weight;
            this.tripId = tripId;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.routeId = routeId;
        }
    }
}