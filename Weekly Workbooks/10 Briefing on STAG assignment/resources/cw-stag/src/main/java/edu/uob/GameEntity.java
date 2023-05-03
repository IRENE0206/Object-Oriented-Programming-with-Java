package edu.uob;

public abstract class GameEntity {
    /**
     * entity names cannot contain spaces
     * entity names will be unique
     * there should only be a single instance of each entity within the game
     */
    private final String name;
    private final String description;
    private Location currentLocation;

    public GameEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Location getCurrentLocation() {
        return this.currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public abstract void removeFromCurrentLocation();

    public abstract void addToLocation(Location location);

    public abstract void addToGameState(GameState gameState);

    public abstract void getActedUpon(EntityVisitor entityVisitor);
}
