import dbTables.PostAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PostAddressTest {

    @Test
    public void testConstructorWithPostalCode() {
        String postalCode = "6211AA"; // A postal code in Maastricht
        double lat = 50.8484; // Latitude for Maastricht
        double lon = 5.6880; // Longitude for Maastricht
        PostAddress address = new PostAddress(postalCode, lat, lon);

        assertEquals(postalCode, address.getPostalCode());
        assertEquals(lat, address.getLat(), 0.0001);
        assertEquals(lon, address.getLon(), 0.0001);
    }

    @Test
    public void testConstructorWithoutPostalCode() {
        double lat = 50.8484;
        double lon = 5.6880;
        PostAddress address = new PostAddress(lat, lon);

        assertNull(address.getPostalCode());
        assertEquals(lat, address.getLat(), 0.0001);
        assertEquals(lon, address.getLon(), 0.0001);
    }

    @Test
    public void testEqualsAndHashCode() {
        PostAddress address1 = new PostAddress("6211AA", 50.8484, 5.6880);
        PostAddress address2 = new PostAddress("6211AA", 50.8485, 5.6881); // Slightly different coordinates, same postal code
        PostAddress address3 = new PostAddress("6221BB", 50.8500, 5.6900); // Different postal code

        assertEquals(address1, address2);
        assertNotEquals(address1, address3);
        assertEquals(address1.hashCode(), address2.hashCode());
        assertNotEquals(address1.hashCode(), address3.hashCode());
    }

    @Test
    public void testNotEqualsWithNull() {
        PostAddress address = new PostAddress("6211AA", 50.8484, 5.6880);
        assertNotEquals(null, address);
    }

    @Test
    public void testNotEqualsWithDifferentClass() {
        PostAddress address = new PostAddress("6211AA", 50.8484, 5.6880);
        String differentClassObject = "I am a string";
        assertNotEquals(differentClassObject, address);
    }

    @Test
    public void testEqualsWithSameObject() {
        PostAddress address = new PostAddress("6211AA", 50.8484, 5.6880);
        assertEquals(address, address);
    }
}