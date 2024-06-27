package dbTables;
import java.util.List;

public class DirectRoute extends BusRoute {
    private List<BusStop> busStops;

    public DirectRoute(List<BusStop> busStops) {
        super(busStops.isEmpty() ? null : busStops.get(0).getDepartureTime(),
                busStops.isEmpty() ? null : busStops.get(busStops.size() - 1).getArrivalTime());

        if (busStops == null || busStops.isEmpty()) {
            throw new IllegalArgumentException("Route is empty or null");
        }
        this.busStops = busStops;
    }

    public DirectRoute(List<BusStop> busStops, String startTime) {
        super(startTime, busStops.isEmpty() ? null : busStops.get(busStops.size() - 1).getArrivalTime());

        if (busStops == null || busStops.isEmpty()) {
            throw new IllegalArgumentException("Route is empty or null");
        }
        this.busStops = busStops;
    }

    public List<BusStop> getBusStops() {
        return busStops;
    }

    public void setBusStops(List<BusStop> busStops) {
        this.busStops = busStops;
    }
}