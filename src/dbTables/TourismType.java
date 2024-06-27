package dbTables;

public class TourismType extends LocationType {
    private static final double HIGH_WEIGHT = 0.5;
    private static final double MODERATE_WEIGHT = 0.3;
    private static final double LOW_WEIGHT = 0.2;

    private static final double HIGH_DECAY = 0.3;
    private static final double MODERATE_DECAY = 0.5;
    private static final double LOW_DECAY = 0.7;

    @Override
    public boolean isEssential(String type) {
        return false;
    }

    @Override
    public boolean isSpecialty(String type) {
        return false;
    }

    @Override
    public boolean isCommunity(String type) {
        return false;
    }

    @Override
    public boolean isHighTourism(String type) {
        return type.equals("museum") || type.equals("gallery") || type.equals("attraction") ||
                type.equals("artwork") || type.equals("zoo");
    }

    @Override
    public boolean isModerateTourism(String type) {
        return type.equals("hotel") || type.equals("hostel") || type.equals("guest_house") ||
                type.equals("caravan_site") || type.equals("viewpoint") || type.equals("apartment");
    }

    @Override
    public double getDecayConstant(String type) {
        if (isHighTourism(type)) {
            return HIGH_DECAY;
        } else if (isModerateTourism(type)) {
            return MODERATE_DECAY;
        } else {
            return LOW_DECAY;
        }
    }

    @Override
    public double getWeight(String type) {
        if (isHighTourism(type)) {
            return HIGH_WEIGHT;
        } else if (isModerateTourism(type)) {
            return MODERATE_WEIGHT;
        } else {
            return LOW_WEIGHT;
        }
    }
}
