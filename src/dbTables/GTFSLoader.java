package dbTables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GTFSLoader {

    private static final BusGraph graph;

    static {
        graph = loadGraph();
    }

    public static BusGraph getGraph() {
        return graph;
    }

    public static BusGraph loadGraph() {
        if(graph != null) {
            return graph;
        }

        Connection conn = getSqlConnection();

        if (conn == null) {
            return null;
        }

        BusGraph graph = new BusGraph();

        String sql = "SELECT st1.stop_id, st1.trip_id, t.route_id, " +
                "TIME_FORMAT(st1.departure_time, '%H:%i:%s') AS departure_time_str, " +
                "TIME_FORMAT(st2.arrival_time, '%H:%i:%s') AS arrival_time_str, st2.stop_id AS next_stop_id " +
                "FROM stop_times st1 " +
                "JOIN stop_times st2 ON st1.trip_id = st2.trip_id AND st1.stop_sequence < st2.stop_sequence " +
                "JOIN trips t ON st1.trip_id = t.trip_id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String fromStopId = rs.getString("stop_id");
                String toStopId = rs.getString("next_stop_id");
                String tripId = rs.getString("trip_id");
                String routeId = rs.getString("route_id");
                String departureTime = rs.getString("departure_time_str");
                String arrivalTime = rs.getString("arrival_time_str");
                int weight = computeTimeDifferenceInSeconds(departureTime, arrivalTime);

                graph.addEdge(fromStopId, toStopId, weight, tripId, departureTime, arrivalTime, routeId);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }

        populateParentStops(graph, conn);
        addParentStopEdges(graph);

        return graph;
    }

    private static void populateParentStops(BusGraph graph, Connection conn) {
        String sql = "SELECT stop_id, COALESCE(parent_station, stop_id) AS parent_station FROM stops";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String stopId = rs.getString("stop_id");
                String parentStation = rs.getString("parent_station");
                graph.setParentStop(stopId, parentStation);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
    }

    private static void addParentStopEdges(BusGraph graph) {
        Map<String, List<String>> parentToStopsMap = new HashMap<>();

        for (Map.Entry<String, String> entry : graph.parentStopMap.entrySet()) {
            String stopId = entry.getKey();
            String parentStation = entry.getValue();

            parentToStopsMap.computeIfAbsent(parentStation, k -> new ArrayList<>()).add(stopId);
        }

        for (List<String> stops : parentToStopsMap.values()) {
            for (int i = 0; i < stops.size(); i++) {
                for (int j = i + 1; j < stops.size(); j++) {
                    String stop1 = stops.get(i);
                    String stop2 = stops.get(j);
                    // Add edges in both directions with a small penalty (e.g., 60 seconds)
                    graph.addEdge(stop1, stop2, 60, null, null, null, null);
                    graph.addEdge(stop2, stop1, 60, null, null, null, null);
                }
            }
        }
    }

    public static Map<String, Map<String, Double>> fetchTravelTimes() {
        Connection conn = getSqlConnection();
        if (conn == null) {
            return Collections.emptyMap();
        }

        Map<String, Map<String, Double>> travelTimeMap = new HashMap<>();
        String sql = "SELECT from_stop_id, to_stop_id, MIN(travel_time) as min_travel_time FROM ("
                + "SELECT st1.stop_id as from_stop_id, st2.stop_id as to_stop_id, "
                + "(TIME_TO_SEC(st2.arrival_time) - TIME_TO_SEC(st1.departure_time)) as travel_time "
                + "FROM stop_times st1 JOIN stop_times st2 ON st1.trip_id = st2.trip_id AND st1.stop_sequence < st2.stop_sequence) as temp_table "
                + "GROUP BY from_stop_id, to_stop_id";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String fromStopId = rs.getString("from_stop_id");
                String toStopId = rs.getString("to_stop_id");
                double avgTravelTime = rs.getDouble("min_travel_time");
                travelTimeMap.putIfAbsent(fromStopId, new HashMap<>());
                travelTimeMap.get(fromStopId).put(toStopId, avgTravelTime);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
        return travelTimeMap;
    }

    private static int computeTimeDifferenceInSeconds(String departureTime, String arrivalTime) {
        return AStarWithTime.timeToSeconds(arrivalTime) - AStarWithTime.timeToSeconds(departureTime);
    }

    public static Map<String, Stop> fetchAllAddresses() {
        Map<String, Stop> addressMap = new HashMap<>();
        Connection conn = getSqlConnection();
        if (conn == null) {
            return Collections.emptyMap();
        }

        String sql = "SELECT stop_id, stop_name, stop_lat, stop_lon FROM stops";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String stopId = rs.getString("stop_id");
                String stopName = rs.getString("stop_name");
                double lat = rs.getDouble("stop_lat");
                double lon = rs.getDouble("stop_lon");
                addressMap.put(stopId, new Stop(stopId, stopName, lat, lon));
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }

        return addressMap;
    }

    public static Map<String, String> fetchAllRouteNames() {
        Map<String, String> routeNames = new HashMap<>();
        Connection conn = getSqlConnection();
        if (conn == null) {
            return Collections.emptyMap();
        }

        String sql = "SELECT route_id, route_short_name, route_long_name FROM routes";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String routeId = rs.getString("route_id");
                String routeName = rs.getString("route_short_name") + " - " + rs.getString("route_long_name");
                routeNames.put(routeId, routeName);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }

        return routeNames;
    }

    public static List<BusStop> getBusStopsForTrip(String tripId, String startStopId, String endStopId) {
        List<BusStop> stops = new ArrayList<>();
        String sql = "SELECT s.stop_name, st.departure_time, st.arrival_time, s.stop_lat, s.stop_lon, s.stop_id, st.stop_sequence  " +
                "FROM stop_times st " +
                "JOIN stops s ON st.stop_id = s.stop_id " +
                "WHERE st.trip_id = ? AND st.stop_sequence >= " +
                "(SELECT stop_sequence FROM stop_times WHERE trip_id = ? AND stop_id = ?) " +
                "AND st.stop_sequence <= " +
                "(SELECT stop_sequence FROM stop_times WHERE trip_id = ? AND stop_id = ?) " +
                "ORDER BY st.stop_sequence";

        try (Connection conn = getSqlConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tripId);
            pstmt.setString(2, tripId);
            pstmt.setString(3, startStopId);
            pstmt.setString(4, tripId);
            pstmt.setString(5, endStopId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String stopName = rs.getString("stop_name");
                    String departureTime = rs.getString("departure_time");
                    String arrivalTime = rs.getString("arrival_time");
                    float lat = rs.getFloat("stop_lat");
                    float lon = rs.getFloat("stop_lon");
                    String stopId = rs.getString("stop_id");
                    int stopSequence = rs.getInt("stop_sequence");
                    stops.add(new BusStop(stopId, stopSequence, stopName, arrivalTime, departureTime, lat, lon));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }

        return stops;
    }

    public static Stop getStopDetails(String stopId) {
        Connection conn = getSqlConnection();
        if (conn == null) {
            return null;
        }

        String sql = "SELECT stop_id, stop_name, stop_lat, stop_lon FROM stops WHERE stop_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stopId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String stopName = rs.getString("stop_name");
                    double lat = rs.getDouble("stop_lat");
                    double lon = rs.getDouble("stop_lon");
                    return new Stop(stopId, stopName, lat, lon);
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }

        return null;
    }

    public static boolean isValidTimeInDatabase(String stopId, String departureTime, String arrivalTime) {
        String sql = "SELECT COUNT(*) FROM stop_times WHERE stop_id = ? AND departure_time = ? AND arrival_time = ?";
        try (Connection conn = getSqlConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, stopId);
            pstmt.setString(2, departureTime);
            pstmt.setString(3, arrivalTime);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        return true;
                    } else {
                        System.err.println("No matching records found for stopId: " + stopId + ", departure: " + departureTime + ", arrival: " + arrivalTime);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
        return false;
    }

    static Connection getSqlConnection() {
        return dbManager.getSqlConnection();
    }
}
