package edu.uob;

// can be collected by the player
public class Artefact extends GameEntity {
    private Player currentOwner;

    public Artefact(String name, String description) {
        super(name, description);
        this.currentOwner = null;
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
    public void getActedUpon(EntityVisitor entityVisitor) {
        if (entityVisitor != null) {
            entityVisitor.actOnEntity(this);
        }
    }

    public void setCurrentOwner(Player player) {
        this.currentOwner = player;
    }

    public Player getCurrentOwner() {
        return this.currentOwner;
    }

}
