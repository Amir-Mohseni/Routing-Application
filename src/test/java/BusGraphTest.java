import dbTables.BusGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BusGraphTest {

    private BusGraph busGraph;

    @BeforeEach
    public void setUp() {
        busGraph = new BusGraph();
    }

    @Test
    public void testAddAndGetEdges() {
        // Add an edge to the graph
        busGraph.addEdge("stop1", "stop2", 600, "trip1", "08:00:00", "08:10:00", "route1");

        // Get edges from the graph
        List<BusGraph.Edge> edges = busGraph.getEdges("stop1");

        // Validate the edge
        assertNotNull(edges);
        assertEquals(1, edges.size());

        BusGraph.Edge edge = edges.get(0);
        assertEquals("stop2", edge.toStopId);
        assertEquals(600, edge.weight);
        assertEquals("trip1", edge.tripId);
        assertEquals("08:00:00", edge.departureTime);
        assertEquals("08:10:00", edge.arrivalTime);
        assertEquals("route1", edge.routeId);
    }

    @Test
    public void testSetAndGetParentStop() {
        // Set parent stops
        busGraph.setParentStop("stop1", "parent1");
        busGraph.setParentStop("stop2", "parent2");

        // Get parent stops
        String parentStop1 = busGraph.getParentStop("stop1");
        String parentStop2 = busGraph.getParentStop("stop2");

        // Validate the parent stops
        assertEquals("parent1", parentStop1);
        assertEquals("parent2", parentStop2);
    }

    @Test
    public void testGetEdgesForNonExistentStop() {
        // Get edges from a stop that doesn't exist
        List<BusGraph.Edge> edges = busGraph.getEdges("nonExistentStop");

        // Validate that the edges list is empty
        assertNotNull(edges);
        assertTrue(edges.isEmpty());
    }

    @Test
    public void testGetParentStopForNonExistentStop() {
        // Get parent stop for a stop that doesn't exist
        String parentStop = busGraph.getParentStop("nonExistentStop");

        // Validate that the parent stop is null
        assertNull(parentStop);
    }
}