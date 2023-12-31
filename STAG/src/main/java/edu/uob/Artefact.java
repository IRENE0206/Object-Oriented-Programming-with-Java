package edu.uob;

// Physical things within the game that can be collected by the player
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
        if (this.currentOwner != null) {
            this.currentOwner.removeArtefact(this.getName());
            this.setCurrentOwner(null);
        }
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
        if (entityVisitor != null) {
            entityVisitor.actOnEntity(this);
        }
    }

    public void setCurrentOwner(Player player) {
        this.currentOwner = player;
    }

}
