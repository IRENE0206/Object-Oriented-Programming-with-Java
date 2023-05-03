package edu.uob;

// Physical things that are an integral part of a location (these can NOT be collected by the player)
public class Furniture extends GameEntity {
    public Furniture(String name, String description) {
        super(name, description);
    }

    @Override
    public void removeFromCurrentLocation() {
        Location currentLocation = this.getCurrentLocation();
        if (currentLocation != null) {
            currentLocation.removeEntity(this);
            this.setCurrentLocation(null);
        }
    }

    @Override
    public void addToLocation(Location location) {
        this.removeFromCurrentLocation();
        if (location != null) {
            location.addEntity(this);
        }
    }

    @Override
    public void addToGameState(GameState gameState) {
        gameState.addEntity(this);
    }

    @Override
    public void getActedUpon(EntityVisitor entityVisitor) {
        entityVisitor.actOnEntity(this);
    }

}
