package dbTables;

public class Route {
    private final String tripId;
    private final String routeId;
    private final String routeShortName;
    private final String routeLongName;
    private final String departureTime;
    private final String arrivalTime;
    private Route fromRoute; // First segment of the journey
    private Route toRoute;   // Second segment of the journey
    private final String startStopId; // ID of the start stop
    private final String endStopId;   // ID of the end stop

    // Constructor for direct routes
    public Route(String tripId, String routeId, String routeShortName, String routeLongName, String departureTime, String arrivalTime, String startStopId, String endStopId) {
        this.tripId = tripId;
        this.routeId = routeId;
        this.routeShortName = routeShortName;
        this.routeLongName = routeLongName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
    }

    // Constructor for routes with transfers
    public Route(Route fromRoute, Route toRoute) {
        // Initialize with first segment details
        this.tripId = fromRoute.getTripId();
        this.routeId = fromRoute.getRouteId();
        this.routeShortName = fromRoute.getRouteShortName();
        this.routeLongName = fromRoute.getRouteLongName();
        this.departureTime = fromRoute.getDepartureTime();
        // Set arrival time from the second segment
        this.arrivalTime = toRoute.getArrivalTime();
        this.fromRoute = fromRoute;
        this.toRoute = toRoute;
        this.startStopId = fromRoute.getStartStopId();
        this.endStopId = toRoute.getEndStopId();
    }

    public String getStartStopId() {
        return startStopId;
    }

    public String getEndStopId() {
        return endStopId;
    }

    public String getTripId() {
        return tripId;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getRouteShortName() {
        return routeShortName;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public Route getFromRoute() {
        return fromRoute;
    }

    public Route getToRoute() {
        return toRoute;
    }

    @Override
    public String toString() {
        if (fromRoute != null && toRoute != null) {
            return "Route with transfer: \n  From: " + fromRoute + "\n  To: " + toRoute;
        }
        return "Direct Route{" +
                "tripId='" + tripId + '\'' +
                ", routeId='" + routeId + '\'' +
                ", routeShortName='" + routeShortName + '\'' +
                ", routeLongName='" + routeLongName + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                '}';
    }
}
