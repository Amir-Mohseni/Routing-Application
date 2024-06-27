package dbTables;

public class ShopType extends LocationType {
    private static final double ESSENTIAL_WEIGHT = 0.5;
    private static final double SPECIALTY_WEIGHT = 0.3;
    private static final double MISC_WEIGHT = 0.2;

    private static final double ESSENTIAL_DECAY = 0.3;
    private static final double SPECIALTY_DECAY = 0.5;
    private static final double MISC_DECAY = 0.7;

    @Override
    public boolean isEssential(String type) {
        return type.equals("supermarket") || type.equals("mall") || type.equals("pharmacy") ||
                type.equals("medical_supply") || type.equals("convenience") || type.equals("bakery") ||
                type.equals("butcher") || type.equals("greengrocer");
    }

    @Override
    public boolean isSpecialty(String type) {
        return type.equals("clothing") || type.equals("shoes") || type.equals("books") ||
                type.equals("electronics") || type.equals("sports") || type.equals("bicycle") ||
                type.equals("toys") || type.equals("music") || type.equals("art") ||
                type.equals("furniture") || type.equals("jewelry");
    }

    @Override
    public boolean isCommunity(String type) {
        return false;
    }

    @Override
    public boolean isHighTourism(String type) {
        return false;
    }

    @Override
    public boolean isModerateTourism(String type) {
        return false;
    }

    @Override
    public double getDecayConstant(String type) {
        if (isEssential(type)) {
            return ESSENTIAL_DECAY;
        } else if (isSpecialty(type)) {
            return SPECIALTY_DECAY;
        } else {
            return MISC_DECAY;
        }
    }

    @Override
    public double getWeight(String type) {
        if (isEssential(type)) {
            return ESSENTIAL_WEIGHT;
        } else if (isSpecialty(type)) {
            return SPECIALTY_WEIGHT;
        } else {
            return MISC_WEIGHT;
        }
    }
}