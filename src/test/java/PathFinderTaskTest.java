import dbTables.AStarWithTime;
import dbTables.BusGraph;
import dbTables.Stop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNull;

public class PathFinderTaskTest {

    private BusGraph mockGraph;
    private Map<String, Stop> mockAddressMap;
    private Map<String, String> mockRouteNames;
    private Map<String, Map<String, Double>> mockTravelTimeMap;
    private List<Stop> mockStartStops;
    private Stop mockEndStop;
    private String mockStartTime;

    @BeforeEach
    public void setUp() {
        mockGraph = new MockBusGraph();
        mockAddressMap = new HashMap<>();
        mockRouteNames = new HashMap<>();
        mockTravelTimeMap = new HashMap<>();
        mockStartStops = new ArrayList<>();
        mockEndStop = new Stop("endStop", "End Stop", 50.851368, 5.690973); // Realistic coordinates for Maastricht
        mockStartTime = "08:00:00";
    }

    @Test
    public void testMissingAddressDataForEndStop() throws Exception {
        mockEndStop = new Stop("missingEndStop", "Missing End Stop", 50.851368, 5.690973);

        PathFinderTask task = new PathFinderTask(mockGraph, mockAddressMap, mockRouteNames, mockTravelTimeMap, mockStartStops, mockEndStop, mockStartTime);
        JourneyRouteResult result = task.call();

        System.out.println("Result: " + result);

        assertNull(result);
    }

    @Test
    public void testMissingAddressDataForStartStop() throws Exception {
        mockAddressMap.put("endStop", mockEndStop);

        Stop missingStartStop = new Stop("missingStartStop", "Missing Start Stop", 50.850346, 5.688889);
        mockStartStops.add(missingStartStop);

        PathFinderTask task = new PathFinderTask(mockGraph, mockAddressMap, mockRouteNames, mockTravelTimeMap, mockStartStops, mockEndStop, mockStartTime);
        JourneyRouteResult result = task.call();

        System.out.println("Result: " + result);

        assertNull(result);
    }

    @Test
    public void testRouteNotFound() throws Exception {
        mockAddressMap.put("endStop", mockEndStop);
        mockAddressMap.put("startStop1", new Stop("startStop1", "Start Stop 1", 50.850346, 5.688889));
        mockStartStops.add(new Stop("startStop1", "Start Stop 1", 50.850346, 5.688889));

        MockAStarWithTime.setMockPath(Collections.emptyList());

        PathFinderTask task = new PathFinderTask(mockGraph, mockAddressMap, mockRouteNames, mockTravelTimeMap, mockStartStops, mockEndStop, mockStartTime);
        JourneyRouteResult result = task.call();

        System.out.println("Result: " + result);

        assertNull(result);
    }

    @Test
    public void testRouteDetailsNotFound() throws Exception {
        mockAddressMap.put("endStop", mockEndStop);
        mockAddressMap.put("startStop1", new Stop("startStop1", "Start Stop 1", 50.850346, 5.688889));
        mockStartStops.add(new Stop("startStop1", "Start Stop 1", 50.850346, 5.688889));

        List<AStarWithTime.PathNode> mockPath = new ArrayList<>();
        mockPath.add(new AStarWithTime.PathNode("startStop1", null, "08:00:00", "08:10:00", "route1"));
        mockPath.add(new AStarWithTime.PathNode("endStop", "trip1", "08:10:00", "08:20:00", "route1"));

        MockAStarWithTime.setMockPath(mockPath);
        GTFSLoader.setMockStopDetails("startStop1", null);

        PathFinderTask task = new PathFinderTask(mockGraph, mockAddressMap, mockRouteNames, mockTravelTimeMap, mockStartStops, mockEndStop, mockStartTime);
        JourneyRouteResult result = task.call();

        System.out.println("Result: " + result);

        assertNull(result);
    }

    private static class MockBusGraph extends BusGraph {
        @Override
        public List<Edge> getEdges(String stopId) {
            List<Edge> edges = new ArrayList<>();
            if (stopId.equals("startStop1")) {
                edges.add(new Edge("startStop1", 1200, "08:00:00", "08:20:00", "route1", "trip1"));
            }
            return edges;
        }

        @Override
        public String getParentStop(String stopId) {
            return stopId;
        }
    }

    private static class MockAStarWithTime extends AStarWithTime {
        private static List<PathNode> mockPath;

        public static void setMockPath(List<PathNode> mockPath) {
            MockAStarWithTime.mockPath = mockPath;
        }

        public static List<PathNode> findShortestPath(BusGraph graph, List<String> startStopIds, String endStopId, Map<String, String> startTimes, Map<String, Stop> addressMap, Map<String, Map<String, Double>> travelTimeMap) {
            return mockPath;
        }
    }

    private static class GTFSLoader {
        private static final Map<String, Stop> mockStopDetails = new HashMap<>();

        public static void setMockStopDetails(String stopId, Stop stop) {
            mockStopDetails.put(stopId, stop);
        }

        public static Stop getStopDetails(String stopId) {
            return mockStopDetails.get(stopId);
        }
    }
}