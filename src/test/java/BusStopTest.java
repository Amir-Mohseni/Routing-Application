import dbTables.BusStop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BusStopTest {

    private BusStop busStopWithRouteName;
    private BusStop busStopWithoutRouteName;

    @BeforeEach
    public void setUp() {
        // Initialize instances of BusStop
        busStopWithRouteName = new BusStop("stop1", 1, "Stop 1", "08:00:00", "08:05:00", 50.850346f, 5.688889f, "Route 1");
        busStopWithoutRouteName = new BusStop("stop2", 2, "Stop 2", "08:10:00", "08:15:00", 50.851368f, 5.690973f);
    }

    @Test
    public void testGetStopId() {
        assertEquals("stop1", busStopWithRouteName.getStopId());
        assertEquals("stop2", busStopWithoutRouteName.getStopId());
    }

    @Test
    public void testGetStopSequence() {
        assertEquals(1, busStopWithRouteName.getStopSequence());
        assertEquals(2, busStopWithoutRouteName.getStopSequence());
    }

    @Test
    public void testGetStopName() {
        assertEquals("Stop 1", busStopWithRouteName.getStopName());
        assertEquals("Stop 2", busStopWithoutRouteName.getStopName());
    }

    @Test
    public void testGetArrivalTime() {
        assertEquals("08:00:00", busStopWithRouteName.getArrivalTime());
        assertEquals("08:10:00", busStopWithoutRouteName.getArrivalTime());
    }

    @Test
    public void testGetDepartureTime() {
        assertEquals("08:05:00", busStopWithRouteName.getDepartureTime());
        assertEquals("08:15:00", busStopWithoutRouteName.getDepartureTime());
    }

    @Test
    public void testGetStopLat() {
        assertEquals(50.850346f, busStopWithRouteName.getStopLat());
        assertEquals(50.851368f, busStopWithoutRouteName.getStopLat());
    }

    @Test
    public void testGetStopLon() {
        assertEquals(5.688889f, busStopWithRouteName.getStopLon());
        assertEquals(5.690973f, busStopWithoutRouteName.getStopLon());
    }

    @Test
    public void testGetRouteName() {
        assertEquals("Route 1", busStopWithRouteName.getRouteName());
        assertNull(busStopWithoutRouteName.getRouteName());
    }
}