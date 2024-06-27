package dbTables;

import java.util.List;

public class TransferRoute extends BusRoute {
    private DirectRoute startRoute;
    private DirectRoute endRoute;

    public TransferRoute(DirectRoute startRoute, DirectRoute endRoute) {
        super(startRoute == null || startRoute.getBusStops().isEmpty() ? null : startRoute.getBusStops().get(0).getDepartureTime(),
                endRoute == null || endRoute.getBusStops().isEmpty() ? null : endRoute.getBusStops().get(endRoute.getBusStops().size() - 1).getArrivalTime());

        if (startRoute == null || endRoute == null || startRoute.getBusStops().isEmpty() || endRoute.getBusStops().isEmpty()) {
            throw new IllegalArgumentException("One of the routes is null or empty");
        }
        this.startRoute = startRoute;
        this.endRoute = endRoute;
    }

    public TransferRoute(DirectRoute startRoute, DirectRoute endRoute, String startTime) {
        super(startTime, endRoute == null || endRoute.getBusStops().isEmpty() ? null : endRoute.getBusStops().get(endRoute.getBusStops().size() - 1).getArrivalTime());

        if (startRoute == null || endRoute == null || startRoute.getBusStops().isEmpty() || endRoute.getBusStops().isEmpty()) {
            throw new IllegalArgumentException("One of the routes is null or empty");
        }
        this.startRoute = startRoute;
        this.endRoute = endRoute;
    }

    public List<BusStop> getStartBusStops() {
        return startRoute.getBusStops();
    }

    public List<BusStop> getEndBusStops() {
        return endRoute.getBusStops();
    }

    public String getTransferLine() {
        return getStartBusStops().get(0).getRouteName() + " -> " + getEndBusStops().get(0).getRouteName();
    }

    public DirectRoute getStartRoute() {
        return startRoute;
    }

    public void setStartRoute(DirectRoute startRoute) {
        this.startRoute = startRoute;
    }

    public DirectRoute getEndRoute() {
        return endRoute;
    }

    public void setEndRoute(DirectRoute endRoute) {
        this.endRoute = endRoute;
    }
}