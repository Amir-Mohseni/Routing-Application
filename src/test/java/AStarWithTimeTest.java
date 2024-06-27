import dbTables.AStarWithTime;
import dbTables.AStarWithTime.PathNode;
import dbTables.BusGraph;
import dbTables.Stop;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AStarWithTimeTest {

    @Test
    public void testFindShortestPathSimple() {
        BusGraph graph = new BusGraph();
        graph.addEdge("A", "B", 600, "1", "08:00:00", "08:10:00", "R1");
        graph.addEdge("B", "C", 600, "1", "08:15:00", "08:25:00", "R1");

        graph.setParentStop("A", "PS1");
        graph.setParentStop("B", "PS2");
        graph.setParentStop("C", "PS3");

        Map<String, String> startTimes = new HashMap<>();
        startTimes.put("A", "08:00:00");

        Map<String, Stop> addressMap = new HashMap<>();
        addressMap.put("A", new Stop("A", "PS1", 0.0, 0.0));
        addressMap.put("B", new Stop("B", "PS2", 0.1, 0.1));
        addressMap.put("C", new Stop("C", "PS3", 0.2, 0.2));

        Map<String, Map<String, Double>> travelTimeMap = new HashMap<>();

        List<PathNode> path = AStarWithTime.findShortestPath(graph, List.of("A"), "C", startTimes, addressMap, travelTimeMap);

        assertEquals(2, path.size());
        assertEquals("A", path.get(0).previousStopId);
        assertEquals("B", path.get(1).previousStopId);
    }

    @Test
    public void testFindShortestPathWithTransfer() {
        BusGraph graph = new BusGraph();
        graph.addEdge("A", "B", 600, "1", "08:00:00", "08:10:00", "R1");
        graph.addEdge("B", "C", 600, "2", "08:20:00", "08:30:00", "R2");

        graph.setParentStop("A", "PS1");
        graph.setParentStop("B", "PS2");
        graph.setParentStop("C", "PS3");

        Map<String, String> startTimes = new HashMap<>();
        startTimes.put("A", "08:00:00");

        Map<String, Stop> addressMap = new HashMap<>();
        addressMap.put("A", new Stop("A", "PS1", 0.0, 0.0));
        addressMap.put("B", new Stop("B", "PS2", 0.1, 0.1));
        addressMap.put("C", new Stop("C", "PS3", 0.2, 0.2));

        Map<String, Map<String, Double>> travelTimeMap = new HashMap<>();

        List<PathNode> path = AStarWithTime.findShortestPath(graph, List.of("A"), "C", startTimes, addressMap, travelTimeMap);

        assertEquals(2, path.size());
        assertEquals("A", path.get(0).previousStopId);
        assertEquals("B", path.get(1).previousStopId);
    }

    @Test
    public void testFindShortestPathNoPath() {
        BusGraph graph = new BusGraph();
        graph.addEdge("A", "B", 600, "1", "08:00:00", "08:10:00", "R1");

        graph.setParentStop("A", "PS1");
        graph.setParentStop("B", "PS2");
        graph.setParentStop("C", "PS3");

        Map<String, String> startTimes = new HashMap<>();
        startTimes.put("A", "08:00:00");

        Map<String, Stop> addressMap = new HashMap<>();
        addressMap.put("A", new Stop("A", "PS1", 0.0, 0.0));
        addressMap.put("B", new Stop("B", "PS2", 0.1, 0.1));
        addressMap.put("C", new Stop("C", "PS3", 0.2, 0.2));

        Map<String, Map<String, Double>> travelTimeMap = new HashMap<>();

        List<PathNode> path = AStarWithTime.findShortestPath(graph, List.of("A"), "C", startTimes, addressMap, travelTimeMap);

        assertEquals(0, path.size());
    }

    @Test
    public void testFindShortestPathWithWalking() {
        BusGraph graph = new BusGraph();
        graph.addEdge("A", "B", 600, "1", "08:00:00", "08:10:00", "R1");
        graph.addEdge("B", "C", 60, "1", "08:15:00", "08:16:00", "R1"); // Walking between stops in the same parent station

        graph.setParentStop("A", "PS1");
        graph.setParentStop("B", "PS1");
        graph.setParentStop("C", "PS1");

        Map<String, String> startTimes = new HashMap<>();
        startTimes.put("A", "08:00:00");

        Map<String, Stop> addressMap = new HashMap<>();
        addressMap.put("A", new Stop("A", "PS1", 0.0, 0.0));
        addressMap.put("B", new Stop("B", "PS1", 0.1, 0.1));
        addressMap.put("C", new Stop("C", "PS1", 0.2, 0.2));

        Map<String, Map<String, Double>> travelTimeMap = new HashMap<>();

        List<PathNode> path = AStarWithTime.findShortestPath(graph, List.of("A"), "C", startTimes, addressMap, travelTimeMap);

        assertEquals(2, path.size());
        assertEquals("A", path.get(0).previousStopId);
        assertEquals("B", path.get(1).previousStopId);
    }

    @Test
    public void testFindShortestPathWithInvalidData() {
        BusGraph graph = new BusGraph();
        graph.addEdge("A", "B", 600, "1", "08:00:00", "08:10:00", "R1");
        graph.addEdge("B", "C", 600, "2", "08:20:00", "08:30:00", "R2");
        graph.addEdge("B", "D", 600, "3", "08:25:00", "08:35:00", "R3"); // Corrected edge with a valid toStopId

        graph.setParentStop("A", "PS1");
        graph.setParentStop("B", "PS2");
        graph.setParentStop("C", "PS3");
        graph.setParentStop("D", "PS4");

        Map<String, String> startTimes = new HashMap<>();
        startTimes.put("A", "08:00:00");

        Map<String, Stop> addressMap = new HashMap<>();
        addressMap.put("A", new Stop("A", "PS1", 0.0, 0.0));
        addressMap.put("B", new Stop("B", "PS2", 0.1, 0.1));
        addressMap.put("C", new Stop("C", "PS3", 0.2, 0.2));
        addressMap.put("D", new Stop("D", "PS4", 0.3, 0.3));

        Map<String, Map<String, Double>> travelTimeMap = new HashMap<>();

        List<PathNode> path = AStarWithTime.findShortestPath(graph, List.of("A"), "C", startTimes, addressMap, travelTimeMap);

        assertEquals(2, path.size());
        assertEquals("A", path.get(0).previousStopId);
        assertEquals("B", path.get(1).previousStopId);
    }
}
