import dbTables.AddressScore;
import dbTables.PostAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressScoreTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        PostAddress address = new PostAddress("12345", 40.7128, -74.0060);
        double score = 11.0;
        double amenityScore = 5.0;
        double shopScore = 3.0;
        double tourismScore = 2.0;
        double accessibilityScore = 1.0;

        // Act
        AddressScore addressScore = new AddressScore(address, amenityScore, shopScore, tourismScore, accessibilityScore);
        addressScore.setScore(score);

        // Assert
        assertEquals(address, addressScore.getAddress());
        assertEquals(score, addressScore.getScore());
        assertEquals(amenityScore, addressScore.getAmenityScore());
        assertEquals(shopScore, addressScore.getShopScore());
        assertEquals(tourismScore, addressScore.getTourismScore());

        // Update scores
        addressScore.setAmenityScore(4.0);
        addressScore.setShopScore(6.0);
        addressScore.setTourismScore(8.0);
        addressScore.setScore(9.0);

        // Assert updated scores
        assertEquals(4.0, addressScore.getAmenityScore());
        assertEquals(6.0, addressScore.getShopScore());
        assertEquals(8.0, addressScore.getTourismScore());
        assertEquals(9.0, addressScore.getScore());
    }

    @Test
    void testToString() {
        // Arrange
        PostAddress address = new PostAddress("12345", 40.7128, -74.0060);
        double score = 11.0;
        double amenityScore = 5.0;
        double shopScore = 3.0;
        double tourismScore = 2.0;
        double accessibilityScore = 1.0;
        AddressScore addressScore = new AddressScore(address, amenityScore, shopScore, tourismScore, accessibilityScore);
        addressScore.setScore(score);

        // Act
        String result = addressScore.toString();

        // Assert
        String expected = "AddressScore{address=12345, score=11.0, amenityScore=5.0, shopScore=3.0, tourismScore=2.0, accessibilityScore=1.0}";
        assertEquals(expected, result);
    }
}