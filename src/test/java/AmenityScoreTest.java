import dbTables.AddressScore;
import dbTables.PostAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AmenityScoreTest {

    private List<AddressScore> scores;

    @BeforeEach
    public void setUp() {
        scores = new ArrayList<>();

        // Mock data for testing
        PostAddress postAddress1 = AddressFinder.getAddress("6211GP");
        PostAddress postAddress2 = AddressFinder.getAddress("6223HV");
        scores.add(new AddressScore(postAddress1, 99.92559402137016, 99.70940303671595, 98.74976614636529, 94.89442578055045));
        scores.add(new AddressScore(postAddress2, 0.0, 0.0, 0.0, 0.0));
        // Add other necessary mock data if required
    }

    @Test
    public void testCalculateAllScores() {
        // Calculate scores using the method from AmenitiesCalculator
        AmenitiesCalculator.calculateAllScores(scores);

        // Verify if the first address in the sorted list is the best address
        AddressScore bestAddress = scores.get(0);
        assertEquals("6211GP", bestAddress.getPostalCode());

        // Verify if the last address in the sorted list is the worst address
        AddressScore worstAddress = scores.get(scores.size() - 1);
        assertEquals("6223HV", worstAddress.getPostalCode());
    }
}
