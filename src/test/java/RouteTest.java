import dbTables.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RouteTest {

    private Route directRoute;
    private Route compositeRoute;

    @BeforeEach
    public void setUp() {
        directRoute = new Route("trip1", "route1", "R1", "Route 1", "08:00:00", "08:30:00", "stop1", "stop2");
        Route fromRoute = new Route("trip2", "route2", "R2", "Route 2", "09:00:00", "09:20:00", "stop3", "stop4");
        Route toRoute = new Route("trip3", "route3", "R3", "Route 3", "09:30:00", "10:00:00", "stop5", "stop6");
        compositeRoute = new Route(fromRoute, toRoute);
    }

    @Test
    @DisplayName("Test Direct Route Construction and Getters")
    public void testDirectRouteConstructorAndGetters() {
        assertEquals("trip1", directRoute.getTripId());
        assertEquals("route1", directRoute.getRouteId());
        assertEquals("R1", directRoute.getRouteShortName());
        assertEquals("Route 1", directRoute.getRouteLongName());
        assertEquals("08:00:00", directRoute.getDepartureTime());
        assertEquals("08:30:00", directRoute.getArrivalTime());
        assertEquals("stop1", directRoute.getStartStopId());
        assertEquals("stop2", directRoute.getEndStopId());
    }

    @Test
    @DisplayName("Test Composite Route Construction and Getters")
    public void testCompositeRouteConstructorAndGetters() {
        assertEquals("trip2", compositeRoute.getTripId());
        assertEquals("route2", compositeRoute.getRouteId());
        assertEquals("R2", compositeRoute.getRouteShortName());
        assertEquals("Route 2", compositeRoute.getRouteLongName());
        assertEquals("09:00:00", compositeRoute.getDepartureTime());
        assertEquals("10:00:00", compositeRoute.getArrivalTime()); // From the second segment
        assertEquals("stop3", compositeRoute.getStartStopId());
        assertEquals("stop6", compositeRoute.getEndStopId());
    }

    @Test
    @DisplayName("Test Total Travel Time Calculation")
    public void testGetTotalTravelTime() {
        LocalTime departureTime = LocalTime.parse(compositeRoute.getDepartureTime());
        LocalTime arrivalTime = LocalTime.parse(compositeRoute.getArrivalTime());
        long travelTime = ChronoUnit.MINUTES.between(departureTime, arrivalTime);
        assertEquals(60, travelTime); // Expects 60 minutes from 09:00:00 to 10:00:00
    }

    @Test
    @DisplayName("Test String Representation")
    public void testToString() {
        String expectedDirectRouteString = "Direct Route{tripId='trip1', routeId='route1', routeShortName='R1', routeLongName='Route 1', departureTime='08:00:00', arrivalTime='08:30:00'}";
        assertEquals(expectedDirectRouteString, directRoute.toString());

        String expectedCompositeRouteString = "Route with transfer: \n  From: Direct Route{tripId='trip2', routeId='route2', routeShortName='R2', routeLongName='Route 2', departureTime='09:00:00', arrivalTime='09:20:00'}\n  To: Direct Route{tripId='trip3', routeId='route3', routeShortName='R3', routeLongName='Route 3', departureTime='09:30:00', arrivalTime='10:00:00'}";
        assertEquals(expectedCompositeRouteString, compositeRoute.toString());
    }
}