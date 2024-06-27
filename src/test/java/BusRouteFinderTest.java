import dbTables.BusRoute;
import dbTables.DirectRoute;
import dbTables.PostAddress;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

//SOS!!!!!!!!!!!!!!! CAREFUL WHEN RUNNING IT, IT MIGHT BREAK EVERYTHING :,(
//P.S. IF ANYTHING ELSE BREAKS I APOLOGIZE IN ADVANCE
public class BusRouteFinderTest {

    private PostAddress startAddress;
    private PostAddress endAddress;

    @Before
    public void setUp() {
        // Initialize the start and end addresses for the tests
        startAddress = AddressFinder.getAddress("6229EN"); // Replace with actual postal code for testing
        endAddress = AddressFinder.getAddress("6229HD");   // Replace with actual postal code for testing
    }

    @Test
    public void testFindShortestDirectBusRoute() {
        BusRouteFinder finder = new BusRouteFinder(startAddress, endAddress);
        DirectRoute route = finder.findShortestDirectBusRoute();
        assertNotNull("Should find a direct route between the given addresses.", route);
    }

    @Test
    public void testNoRouteBetweenStops() {
        // Initialize with postal codes that do not have a direct or transfer route between them
        PostAddress remoteStart = AddressFinder.getAddress("6213BE"); // Replace with an invalid or remote postal code
        PostAddress remoteEnd = AddressFinder.getAddress("6225EJ");   // Replace with an invalid or remote postal code

        BusRouteFinder finder = new BusRouteFinder(remoteStart, remoteEnd);
        BusRoute route = finder.findShortestDirectBusRoute();
        assertNull("Should not find a route between the given remote addresses.", route);
    }
}