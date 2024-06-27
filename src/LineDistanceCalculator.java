import dbTables.PostAddress;

public class LineDistanceCalculator {
    public static final double EARTH_RADIUS_KM = 6378;

    // This function converts decimal degrees to radians
    public static double degToRad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /***
     * A method for finding the birds flight distance between two points
     *
     * @param start has the latitude and longitude of the starting point
     * @param end has the latitude and longitude of the ending point
     * @return The kilometers between those points
     */
    public static double basicDistances(PostAddress start, PostAddress end) {
        // Convert the latitudes and longitudes from decimal degrees to radians
        double lat1Rad = degToRad(start.getLat());
        double lon1Rad = degToRad(start.getLon());

        double lat2Rad = degToRad(end.getLat());
        double lon2Rad = degToRad(end.getLon());

        // Haversine formula
        double dLat = lat2Rad - lat1Rad;
        double dLon = lon2Rad - lon1Rad;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
