package edu.uob;

public abstract class EntityVisitor {
    private final Location triggeredLocation;
    private final GameState gameState;

    public EntityVisitor(Location triggeredLocation, GameState gameState) {
        this.triggeredLocation = triggeredLocation;
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return this.gameState;
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
