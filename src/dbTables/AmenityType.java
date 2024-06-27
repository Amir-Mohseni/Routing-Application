package dbTables;

public class AmenityType extends LocationType {
    private static final double ESSENTIAL_WEIGHT = 0.5;
    private static final double COMMUNITY_WEIGHT = 0.3;
    private static final double REC_WEIGHT = 0.2;

    private static final double ESSENTIAL_DECAY = 0.3;
    private static final double COMMUNITY_DECAY = 0.5;
    private static final double REC_DECAY = 0.7;

    @Override
    public boolean isEssential(String type) {
        return type.equals("hospital") || type.equals("clinic") || type.equals("fire_station") ||
                type.equals("police") || type.equals("school") || type.equals("university") ||
                type.equals("childcare") || type.equals("nursing_home") || type.equals("doctors");
    }

    @Override
    public boolean isSpecialty(String type) {
        return false;
    }

    @Override
    public boolean isCommunity(String type) {
        return type.equals("place_of_worship") || type.equals("community_centre") || type.equals("library") ||
                type.equals("arts_centre") || type.equals("theatre") || type.equals("social_facility") ||
                type.equals("townhall");
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
        } else if (isCommunity(type)) {
            return COMMUNITY_DECAY;
        } else {
            return REC_DECAY;
        }
    }

    @Override
    public double getWeight(String type) {
        if (isEssential(type)) {
            return ESSENTIAL_WEIGHT;
        } else if (isCommunity(type)) {
            return COMMUNITY_WEIGHT;
        } else {
            return REC_WEIGHT;
        }
    }
}

