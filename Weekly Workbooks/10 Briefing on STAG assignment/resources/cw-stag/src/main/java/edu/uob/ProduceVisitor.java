package edu.uob;

public class ProduceVisitor extends EntityVisitor {

    public ProduceVisitor(Location triggeredLocation, GameState gameState) {
        super(triggeredLocation, gameState);
    }

    @Override
    public void actOnEntity(GameEntity gameEntity) {
        if (gameEntity == null) {
            return;
        }
        gameEntity.getActedUpon(this);
    }

    @Override
    public void actOnEntity(Artefact artefact) {
        removeFromCurrentLocationToTriggeredLocation(artefact);
    }

    @Override
    public void actOnEntity(Furniture furniture) {
        removeFromCurrentLocationToTriggeredLocation(furniture);
    }

    @Override
    public void actOnEntity(Character character) {
        removeFromCurrentLocationToTriggeredLocation(character);
    }

    @Override
    public void actOnEntity(Location location) {
        Location triggeredLocation = this.getTriggeredLocation();
        if (triggeredLocation == null) {
            return;
        }
        triggeredLocation.addEntity(location);
    }

    @Override
    public void actOnEntity(Player player) {
        if (player != null) {
            player.increaseHealth();
        }
    }

    private void removeFromCurrentLocationToTriggeredLocation(GameEntity gameEntity) {
        if (gameEntity == null) {
            return;
        }
        gameEntity.removeFromCurrentLocation();
        gameEntity.addToLocation(this.getTriggeredLocation());
    }


}
