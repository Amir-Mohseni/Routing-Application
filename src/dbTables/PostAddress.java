package dbTables;

import java.util.Objects;

public class PostAddress {
    private String postalCode;
    private final double lat;
    private final double lon;

    public String getPostalCode() {
        return postalCode;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public PostAddress(String postalCode, double lat, double lon) {
        this.postalCode = postalCode;
        this.lat = lat;
        this.lon = lon;
    }

    public PostAddress(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        PostAddress other = (PostAddress) obj;
        return Objects.equals(postalCode, other.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postalCode);
    }

}
