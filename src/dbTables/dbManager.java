package dbTables;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

import static dbTables.DirectRoute.getDuration;

public class dbManager {
    static Connection getSqlConnection() {
        String USERNAME = dbCredentials.USERNAME;
        String PORT = dbCredentials.PORT;
        String HOST = dbCredentials.HOST;
        String DATABASE_NAME = dbCredentials.databaseName;
        String PASSWORD = dbCredentials.PASSWORD;
        String DATABASE_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE_NAME
                + "?autoReconnect=true&useSSL=true&requireSSL=true&connectTimeout=50000&socketTimeout=500000";

        Connection connection = null;
        int retryCount = 3;
        while (retryCount > 0) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
                if (connection != null) {
                    break;
                }
            } catch (ClassNotFoundException e) {
                System.err.println("JDBC Driver not found: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Error establishing database connection: " + e.getMessage());
                retryCount--;
                try {
                    Thread.sleep(2000); // Wait for 2 seconds before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (connection == null) {
            System.err.println("Failed to establish database connection after multiple attempts.");
        }
        return connection;
    }

    public static Route findBestTransferRouteWithoutStartTime(List<Stop> startIds, List<Stop> endIds) {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Route>> futures = new ArrayList<>();

        for (Stop startId : startIds) {
            for (Stop endId : endIds) {
                Callable<Route> task = () -> findFastestTransferRouteWithoutStartTime(startId.stopId(), endId.stopId());
                futures.add(executorService.submit(task));
            }
        }

        Route bestTransferRoute = null;

        for (Future<Route> future : futures) {
            try {
                Route currentRoute = future.get();
                if (currentRoute != null) {
                    if (bestTransferRoute == null ||
                            getDuration(currentRoute.getDepartureTime(), currentRoute.getArrivalTime()).toMinutes() <
                                    getDuration(bestTransferRoute.getDepartureTime(), bestTransferRoute.getArrivalTime()).toMinutes()) {
                        bestTransferRoute = currentRoute;
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error processing future: " + e.getMessage());
            }
        }

        executorService.shutdown();

        return bestTransferRoute;
    }

    public static Route findFastestTransferRouteWithoutStartTime(String startStopId, String endStopId) {
        Connection conn = getSqlConnection();
        if (conn == null) {
            return null;
        }

        String sql = "SELECT " +
                "st1.trip_id AS first_trip_id, t1.route_id AS first_route_id, r1.route_short_name AS first_route_short_name, r1.route_long_name AS first_route_long_name, " +
                "st1.departure_time AS first_departure_time, st2.arrival_time AS first_arrival_time, st1.stop_id AS first_start_stop_id, st2.stop_id AS first_end_stop_id, " +
                "st3.trip_id AS second_trip_id, t2.route_id AS second_route_id, r2.route_short_name AS second_route_short_name, r2.route_long_name AS second_route_long_name, " +
                "st3.departure_time AS second_departure_time, st4.arrival_time AS second_arrival_time, st3.stop_id AS second_start_stop_id, st4.stop_id AS second_end_stop_id " +
                "FROM stop_times st1 " +
                "JOIN trips t1 ON st1.trip_id = t1.trip_id " +
                "JOIN routes r1 ON t1.route_id = r1.route_id " +
                "JOIN stop_times st2 ON st1.trip_id = st2.trip_id AND st1.stop_id = ? AND st2.stop_id != st1.stop_id AND st1.stop_sequence < st2.stop_sequence " +
                "JOIN stops s2 ON st2.stop_id = s2.stop_id " +
                "JOIN stop_times st3 ON (s2.parent_station IS NOT NULL AND s2.parent_station = st3.stop_id OR s2.parent_station IS NULL AND st2.stop_id = st3.stop_id) " +
                "JOIN trips t2 ON st3.trip_id = t2.trip_id " +
                "JOIN routes r2 ON t2.route_id = r2.route_id " +
                "JOIN stop_times st4 ON st3.trip_id = st4.trip_id AND st3.stop_id != st4.stop_id AND st4.stop_id = ? " +
                "WHERE st2.arrival_time < st3.departure_time AND TIMESTAMPDIFF(MINUTE, st2.arrival_time, st3.departure_time) >= 3 " +
                "ORDER BY TIMESTAMPDIFF(MINUTE, st1.departure_time, st4.arrival_time) ASC " + // Order by the overall travel time
                "LIMIT 1;";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, startStopId);
            pstmt.setString(2, endStopId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Route firstRoute = new Route(
                        rs.getString("first_trip_id"),
                        rs.getString("first_route_id"),
                        rs.getString("first_route_short_name"),
                        rs.getString("first_route_long_name"),
                        rs.getString("first_departure_time"),
                        rs.getString("first_arrival_time"),
                        rs.getString("first_start_stop_id"),
                        rs.getString("first_end_stop_id")
                );
                Route secondRoute = new Route(
                        rs.getString("second_trip_id"),
                        rs.getString("second_route_id"),
                        rs.getString("second_route_short_name"),
                        rs.getString("second_route_long_name"),
                        rs.getString("second_departure_time"),
                        rs.getString("second_arrival_time"),
                        rs.getString("second_start_stop_id"),
                        rs.getString("second_end_stop_id")
                );

                rs.close();
                pstmt.close();
                conn.close();

                return new Route(firstRoute, secondRoute);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
        }
        return null;
    }

    public static Route findShortestDirectRoute(List<Stop> startStops, List<Stop> endStops, String startTime) {
        Connection conn = getSqlConnection();
        if (conn == null) {
            return null;
        }

        String startStopsBuilder = getStopsListAsString(startStops);
        String endStopsBuilder = getStopsListAsString(endStops);

        String sql = "SELECT " +
                "st1.trip_id, r1.route_id, r1.route_short_name, r1.route_long_name, st1.departure_time, st2.arrival_time, st1.stop_id AS start_stop_id, st2.stop_id AS end_stop_id " +
                "FROM stop_times st1 " +
                "JOIN stop_times st2 ON st1.trip_id = st2.trip_id AND st1.stop_sequence < st2.stop_sequence " +
                "JOIN trips t1 ON st1.trip_id = t1.trip_id " +
                "JOIN routes r1 ON t1.route_id = r1.route_id " +
                "WHERE st1.stop_id IN (" + startStopsBuilder + ") AND st2.stop_id IN (" + endStopsBuilder + ") " +
                "AND st1.departure_time >= ? " +
                "ORDER BY st2.arrival_time ASC " +
                "LIMIT 1;";

        Route shortestRoute = null;
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, startTime);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                shortestRoute = new Route(
                        rs.getString("trip_id"),
                        rs.getString("route_id"),
                        rs.getString("route_short_name"),
                        rs.getString("route_long_name"),
                        rs.getString("departure_time"),
                        rs.getString("arrival_time"),
                        rs.getString("start_stop_id"),
                        rs.getString("end_stop_id")
                );
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving direct routes: " + e.getMessage());
        }
        return shortestRoute;
    }

    public static String getStopsListAsString(List<Stop> stops) {
        StringBuilder stopsList = new StringBuilder();
        for (int i = 0; i < stops.size(); i++) {
            stopsList.append(stops.get(i).stopId());
            if (i < stops.size() - 1) {
                stopsList.append(", ");
            }
        }
        return stopsList.toString();
    }

    public static DirectRoute getStopsFromRoute(Route route) {
        Connection conn = getSqlConnection();
        if (conn == null) {
            return null;
        }

        List<BusStop> busStops = new ArrayList<>();
        String query = "SELECT " +
                "st.stop_id, st.stop_sequence, s.stop_name, st.arrival_time, st.departure_time, " +
                "s.stop_lat, s.stop_lon, r.route_short_name " +
                "FROM stop_times st " +
                "JOIN stops s ON st.stop_id = s.stop_id " +
                "JOIN trips t ON st.trip_id = t.trip_id " +
                "JOIN routes r ON t.route_id = r.route_id " +
                "WHERE st.trip_id = ? AND " +
                "st.stop_sequence BETWEEN (SELECT stop_sequence FROM stop_times WHERE stop_id = ? AND trip_id = ? LIMIT 1) " +
                "AND (SELECT stop_sequence FROM stop_times WHERE stop_id = ? AND trip_id = ? LIMIT 1) " +
                "ORDER BY st.stop_sequence";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, route.getTripId());
            stmt.setString(2, route.getStartStopId());
            stmt.setString(3, route.getTripId());
            stmt.setString(4, route.getEndStopId());
            stmt.setString(5, route.getTripId());
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                BusStop busStop = new BusStop(
                        resultSet.getString("stop_id"),
                        resultSet.getInt("stop_sequence"),
                        resultSet.getString("stop_name"),
                        resultSet.getString("arrival_time"),
                        resultSet.getString("departure_time"),
                        resultSet.getFloat("stop_lat"),
                        resultSet.getFloat("stop_lon"),
                        resultSet.getString("route_short_name"));
                busStops.add(busStop);
            }

            resultSet.close();
            stmt.close();
            conn.close();
            if (!busStops.isEmpty()) {
                return new DirectRoute(busStops);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving stops: " + e.getMessage());
        }
        return null;
    }


    public static DirectRoute getAllStopsFromTripId(String tripID, int startSequence, int endSequence) {
        Connection conn = getSqlConnection();
        if (conn == null)
            return null;

        List<BusStop> busStops = new ArrayList<>();

        String query = "SELECT " +
                "    st.stop_id, " +
                "    st.stop_sequence, " +
                "    s.stop_name, " +
                "    st.arrival_time, " +
                "    st.departure_time, " +
                "    s.stop_lat, " +
                "    s.stop_lon, " +
                "    r.route_short_name " +
                "FROM " +
                "    stop_times st " +
                "JOIN " +
                "    stops s ON st.stop_id = s.stop_id " +
                "JOIN " +
                "    trips t ON st.trip_id = t.trip_id " +
                "JOIN " +
                "    routes r ON t.route_id = r.route_id " +
                "WHERE " +
                "    st.trip_id = ? AND " +
                "    st.stop_sequence BETWEEN ? AND ? " +
                "ORDER BY " +
                "    st.stop_sequence;";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, tripID);
            stmt.setInt(2, startSequence);
            stmt.setInt(3, endSequence);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                BusStop busStop = new BusStop(
                        resultSet.getString("stop_id"),
                        resultSet.getInt("stop_sequence"),
                        resultSet.getString("stop_name"),
                        resultSet.getString("arrival_time"),
                        resultSet.getString("departure_time"),
                        resultSet.getFloat("stop_lat"),
                        resultSet.getFloat("stop_lon"),
                        resultSet.getString("route_short_name"));
                busStops.add(busStop);
            }

            resultSet.close();
            stmt.close();
            conn.close();

            return new DirectRoute(busStops);
        } catch (SQLException e) {
            System.err.println("Error retrieving stops: " + e.getMessage());
            return null;
        }
    }

    public static class RouteDetails {
        private final String tripId;
        private final int startStopSequence;
        private final int endStopSequence;

        public RouteDetails(String tripId, int startStopSequence, int endStopSequence) {
            this.tripId = tripId;
            this.startStopSequence = startStopSequence;
            this.endStopSequence = endStopSequence;
        }

        public String getTripId() {
            return tripId;
        }

        public int getStartStopSequence() {
            return startStopSequence;
        }

        public int getEndStopSequence() {
            return endStopSequence;
        }
    }

    public static RouteDetails findShortestRoute(List<Stop> startStopIds, List<Stop> endStopIds) {
        Connection conn = getSqlConnection();
        if (conn == null)
            return null;

        String startStops = getStopsListAsString(startStopIds);
        String endStops = getStopsListAsString(endStopIds);

        String query = "SELECT " +
                "st1.trip_id, st1.stop_sequence AS start_stop_sequence, st2.stop_sequence AS end_stop_sequence, " +
                "TIMEDIFF(st2.arrival_time, st1.departure_time) AS travel_time " +
                "FROM stop_times st1 " +
                "JOIN stop_times st2 ON st1.trip_id = st2.trip_id AND st1.stop_sequence < st2.stop_sequence " +
                "WHERE st1.stop_id IN (" + startStops + ") " +
                "AND st2.stop_id IN (" + endStops + ") " +
                "ORDER BY travel_time ASC;";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery();
            RouteDetails routeDetails = null;
            if (resultSet.next()) {
                System.out.println(resultSet.getString("travel_time"));
                String tripId = resultSet.getString("trip_id");
                int startStopSequence = resultSet.getInt("start_stop_sequence");
                int endStopSequence = resultSet.getInt("end_stop_sequence");
                routeDetails = new RouteDetails(tripId, startStopSequence, endStopSequence);
            }
            resultSet.close();
            stmt.close();
            conn.close();
            return routeDetails;
        } catch (SQLException e) {
            System.err.println("Error retrieving trip id: " + e.getMessage());
            return null;
        }
    }
    
    public static List<Stop> fetchStopsByCoords(double lat, double lon) {
        Connection conn = getSqlConnection();
        if (conn == null)
            return null;

        List<Stop> stopsList = new ArrayList<>();

        String query = "SELECT *, " +
                "(6378 * acos(cos(radians(?)) * cos(radians(stop_lat)) * cos(radians(stop_lon) - radians(?)) + sin(radians(?)) * sin(radians(stop_lat)))) AS distance "
                +
                "FROM stops " +
                "ORDER BY distance";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDouble(1, lat);
            stmt.setDouble(2, lon);
            stmt.setDouble(3, lat);

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                Stop stop = new Stop(
                        resultSet.getString("stop_id"),
                        resultSet.getString("stop_name"),
                        resultSet.getDouble("stop_lat"),
                        resultSet.getDouble("stop_lon")
                );

                float distance = resultSet.getFloat("distance");
                if(!stopsList.isEmpty() && distance > 0.35)
                    continue;
                stopsList.add(stop);
            }
            resultSet.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return stopsList;
    }

    public static PostAddress fetchAddress(String postalCode) {
        Connection conn = getSqlConnection();
        if (conn == null)
            return null;

        String query = "SELECT * FROM postal_codes WHERE postal_code = ?";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, postalCode);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                PostAddress address = new PostAddress(resultSet.getString("postal_code"), resultSet.getDouble("latitude"), resultSet.getDouble("longitude"));
                resultSet.close();
                stmt.close();
                conn.close();
                return address;
            } else {
                resultSet.close();
                stmt.close();
                conn.close();
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving postal code: " + e.getMessage());
            return null;
        }
    }

    public static List<PostAddress> fetchAllAddresses() {
        Connection conn = getSqlConnection();
        if (conn == null)
            return null;

        List<PostAddress> addresses = new ArrayList<>();

        String query = "SELECT * FROM postal_codes";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                PostAddress address = new PostAddress(resultSet.getString("postal_code"), resultSet.getDouble("latitude"), resultSet.getDouble("longitude"));
                addresses.add(address);
            }
            resultSet.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving postal codes: " + e.getMessage());
        }
        return addresses;
    }
}
