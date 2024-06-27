import dbTables.BusRoute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BusRouteTest {

    private TestBusRoute busRoute;

    @BeforeEach
    public void setUp() {
        // Initialize a concrete instance of BusRoute
        busRoute = new TestBusRoute("08:00:00", "09:30:00");
    }

    @Test
    public void testGetDuration() {
        // Test the getDuration method
        Duration duration = BusRoute.getDuration("08:00:00", "09:30:00");
        assertEquals(90, duration.toMinutes());
    }

    @Test
    public void testCalculateTripTime() {
        // Test the calculateTripTime method
        int tripTime = busRoute.calculateTripTime();
        assertEquals(90, tripTime);
    }

    @Test
    public void testSetAndGetStartTime() {
        // Test the get and set methods for startTime
        busRoute.setStartTime("10:00:00");
        assertEquals("10:00:00", busRoute.getStartTime());
    }

    @Test
    public void testSetAndGetEndTime() {
        // Test the get and set methods for endTime
        busRoute.setEndTime("11:00:00");
        assertEquals("11:00:00", busRoute.getEndTime());
    }

    // Concrete subclass of BusRoute for testing
    private static class TestBusRoute extends BusRoute {
        public TestBusRoute(String startTime, String endTime) {
            super(startTime, endTime);
        }
    }
}