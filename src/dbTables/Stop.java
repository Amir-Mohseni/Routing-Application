package dbTables;

public record Stop(String stopId, String stopName, double stopLat, double stopLon) {
    public double getStopLat() {
        return stopLat;
    }

    public double getStopLon() {
        return stopLon;
    }

    @Override
    public String toString() {
        return "Stop{" +
                "stopId='" + stopId + '\'' +
                ", stopName='" + stopName + '\'' +
                ", stopLat=" + stopLat +
                ", stopLon=" + stopLon +
                '}';
    }
}
