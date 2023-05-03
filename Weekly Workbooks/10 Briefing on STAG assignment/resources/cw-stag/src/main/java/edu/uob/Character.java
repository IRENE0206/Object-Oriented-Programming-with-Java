package edu.uob;

// The various creatures or people involved in game
public class Character extends GameEntity {
    public Character(String name, String description) {
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
        if (entityVisitor == null) {
            return;
        }
        entityVisitor.actOnEntity(this);
    }
}
