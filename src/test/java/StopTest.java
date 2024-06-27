import dbTables.Stop;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StopTest {

    @Test
    public void testConstructorAndGetters() {
        String stopId = "stop1";
        String stopName = "Stop 1";
        double stopLat = 50.850346;
        double stopLon = 5.688889;

        Stop stop = new Stop(stopId, stopName, stopLat, stopLon);

        assertEquals(stopId, stop.stopId());
        assertEquals(stopName, stop.stopName());
        assertEquals(stopLat, stop.stopLat());
        assertEquals(stopLon, stop.stopLon());
    }

    @Test
    public void testToString() {
        Stop stop = new Stop("stop1", "Stop 1", 50.850346, 5.688889);
        String expectedString = "Stop{stopId='stop1', stopName='Stop 1', stopLat=50.850346, stopLon=5.688889}";

        assertEquals(expectedString, stop.toString());
    }
}