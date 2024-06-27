import dbTables.PostAddress;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LineDistanceCalculatorTest {

    @Test
    public void testDegToRad() {
        double degrees = 180.0;
        double radians = LineDistanceCalculator.degToRad(degrees);
        assertEquals("180 degrees should be π radians", Math.PI, radians, 0.00001);
    }

    @Test
    public void testBasicDistancesWithinMaastricht() {

        PostAddress start = new PostAddress(50.851368, 5.690973); // Maastricht University
        PostAddress end = new PostAddress(50.844205, 5.701240);   // Maastricht Central Station

        double distance = LineDistanceCalculator.basicDistances(start, end);
        double expectedDistance = 1.2; // Roughly the distance in km between these two points
        double delta = 0.2; // Acceptable margin of error
        assertEquals("Distance between Maastricht University and Maastricht Central Station should be approximately 1.2 km", expectedDistance, distance, delta);
    }

    @Test
    public void testBasicDistancesWithinMaastrichtSameLocation() {
        PostAddress start = new PostAddress(50.851368, 5.690973); // Maastricht University twice
        PostAddress end = new PostAddress(50.851368, 5.690973);

        double distance = LineDistanceCalculator.basicDistances(start, end);
        assertEquals("Distance between the same locations should be 0 km", 0.0, distance, 0.00001);
    }

    @Test
    public void testBasicDistancesWithinMaastrichtCloseLocations() {
        // Coordinates for very close locations within Maastricht
        PostAddress start = new PostAddress(50.851368, 5.690973); // Maastricht University
        PostAddress end = new PostAddress(50.850368, 5.690973);   // Very close location to Maastricht University

        double distance = LineDistanceCalculator.basicDistances(start, end);
        double expectedDistance = 0.111; // Very small distance in km
        double delta = 0.01; // Acceptable margin of error
        assertEquals("Distance between very close locations in Maastricht should be very small", expectedDistance, distance, delta);
    }

    @Test
    public void testBasicDistancesWithinMaastrichtFarLocations() {
        // Coordinates for two far locations within Maastricht
        PostAddress start = new PostAddress(50.851368, 5.690973); // Maastricht University
        PostAddress end = new PostAddress(50.861368, 5.710973);   // Distant location within Maastricht

        double distance = LineDistanceCalculator.basicDistances(start, end);
        double expectedDistance = 2.3; // Roughly the distance in km between these two points
        double delta = 0.6; // Acceptable margin of error
        assertEquals("Distance between Maastricht University and a distant location in Maastricht should be approximately 2.3 km", expectedDistance, distance, delta);
    }
}