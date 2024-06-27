package dbTables;

public abstract class LocationType {
    public abstract boolean isEssential(String type);
    public abstract boolean isSpecialty(String type);
    public abstract boolean isCommunity(String type);
    public abstract boolean isHighTourism(String type);
    public abstract boolean isModerateTourism(String type);

    public abstract double getDecayConstant(String type);
    public abstract double getWeight(String type);
}