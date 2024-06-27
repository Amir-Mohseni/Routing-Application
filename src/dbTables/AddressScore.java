package dbTables;

public class AddressScore {
    private final PostAddress address;
    private double score;
    private double amenityScore;
    private double shopScore;
    private double tourismScore;
    private double accessibilityScore;

    public AddressScore(PostAddress address, double amenityScore, double shopScore, double tourismScore, double accessibilityScore) {
        this.address = address;
        this.amenityScore = amenityScore;
        this.shopScore = shopScore;
        this.tourismScore = tourismScore;
        this.accessibilityScore = accessibilityScore;
    }

    public PostAddress getAddress() {
        return address;
    }

    public String getPostalCode() {
        return address.getPostalCode();
    }

    public double getScore() {
        return score;
    }

    public double getAmenityScore() {
        return amenityScore;
    }

    public double getShopScore() {
        return shopScore;
    }

    public double getTourismScore() {
        return tourismScore;
    }

    public double getAccessibilityScore() {
        return this.accessibilityScore;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setAmenityScore(double amenityScore) {
        this.amenityScore = amenityScore;
    }

    public void setShopScore(double shopScore) {
        this.shopScore = shopScore;
    }

    public void setTourismScore(double tourismScore) {
        this.tourismScore = tourismScore;
    }

    public void setAccessibilityScore(double accessibilityScore) {
        this.accessibilityScore = accessibilityScore;
    }

    @Override
    public String toString() {
        return "AddressScore{" +
                "address=" + getPostalCode() +
                ", score=" + getScore() +
                ", amenityScore=" + getAmenityScore() +
                ", shopScore=" + getShopScore() +
                ", tourismScore=" + getTourismScore() +
                ", accessibilityScore=" + getAccessibilityScore() +
                '}';
    }
}
