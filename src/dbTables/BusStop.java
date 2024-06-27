package dbTables;

public class BusStop {
    private final String stopId;
    private final int stopSequence;
    private final String stopName;
    private final String arrivalTime;
    private final String departureTime;
    private final float stopLat;
    private final float stopLon;
    private String routeName;

    public BusStop(String stopId, int stopSequence, String stopName, String arrivalTime, String departureTime, float stopLat, float stopLon) {
        this.stopName = stopName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
        this.stopId = stopId;
        this.stopSequence = stopSequence;
    }

    public BusStop(String stopId, int stopSequence, String stopName, String arrivalTime, String departureTime, float stopLat, float stopLon, String routeName) {
        this.stopId = stopId;
        this.stopSequence = stopSequence;
        this.stopName = stopName;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
        this.routeName = routeName;
    }

    public String getStopId() {
        return stopId;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public String getStopName() {
        return stopName;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public float getStopLat() {
        return stopLat;
    }

    public float getStopLon() {
        return stopLon;
    }

    public String getRouteName() {
        return routeName;
    }
}
