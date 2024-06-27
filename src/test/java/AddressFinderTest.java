import dbTables.PostAddress;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AddressFinderTest {
    @Test
    public void testValidAddress() {
        String postalCode = "6216EG";
        PostAddress address = AddressFinder.getAddress("6216EG");
        assert address != null;
        assertEquals(address.getPostalCode(), postalCode);
        assertEquals(address.getLat(), 50.8463396901629, 0.00000001);
        assertEquals(address.getLon(), 5.6695838972445, 0.00000001);
    }

    @Test
    public void testInvalidAddress() {
        PostAddress address = AddressFinder.getAddress("021000AA");
        assert address == null;

        // Test if the address is not found
        address = AddressFinder.getAddress("7000AA");
        assert address == null;

        // Test if the address is not found
        address = AddressFinder.getAddress("-7000AA");
        assert address == null;
    }
}
