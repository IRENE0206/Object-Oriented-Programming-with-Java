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
        }
    }

    @Override
    public void addToLocation(Location location) {
        if (location == null) {
            return;
        }
        location.addEntity(this);
    }

    @Override
    public void getActedUpon(EntityVisitor entityVisitor) {
        if (entityVisitor == null) {
            return;
        }
        entityVisitor.actOnEntity(this);
    }
}
