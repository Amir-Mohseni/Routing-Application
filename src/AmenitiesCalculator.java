import dbTables.*;

import static dbTables.AmenityManager.*;
import static dbTables.dbManager.fetchAllAddresses;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.ToDoubleFunction;

public class AmenitiesCalculator {

    private static final LocationType shopType = new ShopType();
    private static final LocationType amenityType = new AmenityType();
    private static final LocationType tourismType = new TourismType();

    public static void main(String[] args) {
        List<AddressScore> scores = new ArrayList<>();
        AmenitiesCalculator calculator = new AmenitiesCalculator();
        calculateAllScores(scores);
        calculator.printBestAndWorst(scores);
        System.out.println(getAddressScore("6221CR", scores));
    }

    private static double calculateCategoryScore(PostAddress postAddress, List<? extends Location> locations, LocationType typeChecker) {
        double score = 0;
        for (Location location : locations) {
            double decayConstant = typeChecker.getDecayConstant(location.getType());
            double decayScore = exponentialDecayScore(LineDistanceCalculator.basicDistances(postAddress, new PostAddress(location.getLat(), location.getLon())), decayConstant);
            score += decayScore * typeChecker.getWeight(location.getType());
        }
        return score;
    }

    private static double calculateShopScores(PostAddress postAddress, List<Shop> shops) {
        return calculateCategoryScore(postAddress, shops, shopType);
    }

    private static double calculateAmenityScores(PostAddress postAddress, List<Amenity> amenities) {
        return calculateCategoryScore(postAddress, amenities, amenityType);
    }

    private static double calculateTourismScores(PostAddress postAddress, List<Tourism> attractions) {
        return calculateCategoryScore(postAddress, attractions, tourismType);
    }

    private static double calculateAccessibilityScore(PostAddress postAddress, List<Stop> busStops) {
        double score = 0;
        for (Stop busStop : busStops) {
            double distance = LineDistanceCalculator.basicDistances(postAddress, new PostAddress(busStop.getStopLat(), busStop.getStopLon()));
            score += 1 / (1 + distance);
        }
        return score;
    }

    private static double exponentialDecayScore(double distance, double decayConstant) {
        return Math.exp(-decayConstant * distance);
    }

    public static void calculateAllScores(List<AddressScore> scores) {
        List<PostAddress> allAddresses = fetchAllAddresses();
        if (allAddresses == null || allAddresses.isEmpty()) {
            System.out.println("No addresses found");
            return;
        }

        List<Shop> shops = fetchShopsByCoords();
        List<Amenity> amenities = fetchAmenitiesByCords();
        List<Tourism> attractions = fetchAttractionsByCoords();
        List<Stop> busStops = fetchBusStopsByCoords();

        if (shops == null || shops.isEmpty() || amenities == null || amenities.isEmpty() || attractions == null || attractions.isEmpty() || busStops == null || busStops.isEmpty()) {
            System.out.println("No locations found");
            return;
        }

        for (PostAddress address : allAddresses) {
            double shopScore = calculateShopScores(address, shops);
            double amenityScore = calculateAmenityScores(address, amenities);
            double tourismScore = calculateTourismScores(address, attractions);
            double accessibilityScore = calculateAccessibilityScore(address, busStops);

            scores.add(new AddressScore(address, amenityScore, shopScore, tourismScore, accessibilityScore));
        }

        normalizeScores(scores);
        sortScores(scores);
    }

    public void printBestAndWorst(List<AddressScore> scores) {
        System.out.println("Most desirable addresses:");
        System.out.println(scores.get(0));

        System.out.println("Least desirable addresses:");
        System.out.println(scores.get(scores.size() - 1));
    }

    private static void sortScores(List<AddressScore> scores) {
        scores.sort((score1, score2) -> Double.compare(score2.getScore(), score1.getScore()));
    }

    public static AddressScore getAddressScore(String postalCode, List<AddressScore> scores) {
        return scores.stream()
                .filter(score -> score.getPostalCode().equals(postalCode))
                .findFirst()
                .orElse(null);
    }

    private static void normalizeScores(List<AddressScore> scores) {
        if (scores.isEmpty()) {
            System.out.println("No scores to normalize");
            return;
        }

        normalizeCategoryScores(scores, AddressScore::getAmenityScore, AddressScore::setAmenityScore);
        normalizeCategoryScores(scores, AddressScore::getShopScore, AddressScore::setShopScore);
        normalizeCategoryScores(scores, AddressScore::getTourismScore, AddressScore::setTourismScore);
        normalizeCategoryScores(scores, AddressScore::getAccessibilityScore, AddressScore::setAccessibilityScore);

        setTotalScores(scores);
    }

    private static void normalizeCategoryScores(List<AddressScore> scores, ToDoubleFunction<AddressScore> getter, BiConsumer<AddressScore, Double> setter) {
        double maxScore = scores.stream().mapToDouble(getter).max().orElse(0);
        double minScore = scores.stream().mapToDouble(getter).min().orElse(0);

        if (maxScore == minScore) {
            scores.forEach(score -> setter.accept(score, 0.0));
        } else {
            scores.forEach(score -> {
                double normalizedScore = 100 * (getter.applyAsDouble(score) - minScore) / (maxScore - minScore);
                setter.accept(score, normalizedScore);
            });
        }
    }

    private static void setTotalScores(List<AddressScore> scores) {
        for (AddressScore score : scores) {
            double totalScore = (score.getAmenityScore() * Constants.AMENITY_CATEGORY_WEIGHT) +
                    (score.getShopScore() * Constants.SHOP_CATEGORY_WEIGHT) +
                    (score.getTourismScore() * Constants.TOURISM_CATEGORY_WEIGHT) +
                    (score.getAccessibilityScore() * Constants.ACCESSIBILITY_CATEGORY_WEIGHT);
            score.setScore(totalScore);
        }
    }
}

// Constants class to hold weight values
class Constants {
    public static final double AMENITY_CATEGORY_WEIGHT = 0.4;
    public static final double SHOP_CATEGORY_WEIGHT = 0.3;
    public static final double TOURISM_CATEGORY_WEIGHT = 0.1;
    public static final double ACCESSIBILITY_CATEGORY_WEIGHT = 0.2;
}
