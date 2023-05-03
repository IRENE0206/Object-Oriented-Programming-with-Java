package edu.uob;

public abstract class EntityVisitor {
    private final Location triggeredLocation;
    private final Location storeroom;

    public EntityVisitor(Location triggeredLocation, Location storeroom) {
        this.triggeredLocation = triggeredLocation;
        this.storeroom = storeroom;
    }

    public Location getStoreroom() {
        return this.storeroom;
    }

    public Location getTriggeredLocation() {
        return this.triggeredLocation;
    }

    public abstract void actOnEntity(GameEntity gameEntity);
    public abstract void actOnEntity(Artefact artefact);
    public abstract void actOnEntity(Furniture furniture);
    public abstract void actOnEntity(Character character);
    public abstract void actOnEntity(Location location);
    public abstract void actOnEntity(Player player);
}
