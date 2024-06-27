package dbTables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dbTables.dbManager.getSqlConnection;

public class AmenityManager {
    public static List<Amenity> fetchAmenitiesByCords() {
        Connection conn = getSqlConnection();
        if (conn == null)
            return null;

        List<Amenity> nearbyAmenities = new ArrayList<Amenity>();

        String query = "SELECT lat, lon, amenity " +
                "FROM amenities ";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                Amenity Amenity = new Amenity(
                        resultSet.getDouble("lat"),
                        resultSet.getDouble("lon"),
                        resultSet.getString("amenity")
                );
                nearbyAmenities.add(Amenity);
            }
            resultSet.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return nearbyAmenities;
    }

    public static List<Tourism> fetchAttractionsByCoords() {
        Connection conn = getSqlConnection();
        if (conn == null)
            return null;

        List<Tourism> nearbyAttractions = new ArrayList<Tourism>();

        String query = "SELECT lat, lon, attraction_type FROM tourism ";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                Tourism attraction = new Tourism(resultSet.getDouble("lat"),
                        resultSet.getDouble("lon"), resultSet.getString("attraction_type"));
                nearbyAttractions.add(attraction);
            }
            resultSet.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return nearbyAttractions;
    }

    public static List<Shop> fetchShopsByCoords() {
        Connection conn = getSqlConnection();
        if (conn == null)
            return null;

        List<Shop> nearbyShops = new ArrayList<>();

        String query = "SELECT lat, lon, shop FROM shop ";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                Shop shop = new Shop(resultSet.getDouble("lat"), resultSet.getDouble("lon"), resultSet.getString("shop"));
                nearbyShops.add(shop);
            }
            resultSet.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return nearbyShops;
    }

    public static List<Stop> fetchBusStopsByCoords() {
        Connection conn = getSqlConnection();
        if (conn == null)
            return null;

        List<Stop> stops = new ArrayList<>();

        String query = "SELECT * FROM stops ";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);

            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                Stop stop = new Stop(
                        resultSet.getString("stop_id"),
                        resultSet.getString("stop_name"),
                        resultSet.getDouble("stop_lat"),
                        resultSet.getDouble("stop_lon")
                );
                stops.add(stop);
            }
            resultSet.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return stops;
    }
}
