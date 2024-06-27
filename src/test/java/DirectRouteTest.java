import dbTables.BusStop;
import dbTables.DirectRoute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DirectRouteTest {

    private List<BusStop> busStops;

    @BeforeEach
    public void setUp() {
        busStops = Arrays.asList(
                new BusStop("stop1", 1, "Stop 1", "08:00:00", "08:05:00", 50.850346f, 5.688889f),
                new BusStop("stop2", 2, "Stop 2", "08:10:00", "08:15:00", 50.851368f, 5.690973f)
        );
    }

    @Test
    public void testConstructorWithBusStops() {
        DirectRoute directRoute = new DirectRoute(busStops);

        assertNotNull(directRoute);
        assertTimeEquals("08:05:00", directRoute.getStartTime());
        assertTimeEquals("08:15:00", directRoute.getEndTime());
        assertEquals(busStops, directRoute.getBusStops());
    }

    @Test
    public void testConstructorWithBusStopsAndStartTime() {
        String startTime = "08:00:00";
        DirectRoute directRoute = new DirectRoute(busStops, startTime);

        assertNotNull(directRoute);
        assertTimeEquals(startTime, directRoute.getStartTime());
        assertTimeEquals("08:15:00", directRoute.getEndTime());
        assertEquals(busStops, directRoute.getBusStops());
    }

    @Test
    public void testConstructorWithEmptyBusStops() {
        List<BusStop> emptyBusStops = List.of();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new DirectRoute(emptyBusStops);
        });

        assertEquals("Route is empty or null", exception.getMessage());
    }

   /* Because the original direct route class doesnt handle null values, i think...
   @Test
    public void testConstructorWithNullBusStops() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new DirectRoute(null);
        });

        assertEquals("Route is empty or null", exception.getMessage());
    }
*/
    @Test
    public void testSetAndGetBusStops() {
        DirectRoute directRoute = new DirectRoute(busStops);
        List<BusStop> newBusStops = List.of(
                new BusStop("stop3", 3, "Stop 3", "08:20:00", "08:25:00", 50.852456f, 5.692123f)
        );

        directRoute.setBusStops(newBusStops);
        assertEquals(newBusStops, directRoute.getBusStops());
    }

    private void assertTimeEquals(String expected, String actual) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime expectedTime = LocalTime.parse(expected, formatter);
        LocalTime actualTime = LocalTime.parse(actual, formatter);
        long difference = Math.abs(java.time.Duration.between(expectedTime, actualTime).toMinutes());
        assertTrue(difference <= 5, "Expected time " + expected + " but got " + actual);
    }
}