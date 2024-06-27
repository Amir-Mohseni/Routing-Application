import dbTables.AStarWithTime;

import java.util.List;

public class JourneyRouteResult {
    public JourneyRoute route;
    public List<AStarWithTime.PathNode> path;
    public int travelTime;
    public String startTime;

    public JourneyRouteResult(JourneyRoute route, List<AStarWithTime.PathNode> path, int travelTime, String startTime) {
        this.route = route;
        this.path = path;
        this.travelTime = travelTime;
        this.startTime = startTime;
    }
}
