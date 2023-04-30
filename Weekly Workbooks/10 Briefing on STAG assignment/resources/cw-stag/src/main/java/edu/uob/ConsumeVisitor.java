package edu.uob;

public class ConsumeVisitor extends EntityVisitor {
    // if an entity (artefact/furniture/character) is in the player's current location,
    // it is removed and moved to the storeroom
    // if an entity (artefact/furniture/character) is NOT in the player's current location,
    // it is still removed and moved to the storeroom

    public ConsumeVisitor(Location triggeredLocation, GameState gameState) {
        super(triggeredLocation, gameState);
    }

    @Override
    public void actOnEntity(GameEntity gameEntity) {
        gameEntity.getActedUpon(this);
    }

    @Override
    public void actOnEntity(Artefact artefact) {
        if (artefact == null) {
            return;
        }
        artefact.removeFromCurrentLocation();
        // if an artefact is in the player's inventory,
        // it is removed and moved to the storeroom
        artefact.setCurrentOwner(null);
        artefact.addToLocation(this.getGameState().getStoreroom());
    }

    @Override
    public void actOnEntity(Furniture furniture) {
        if (furniture == null) {
            return;
        }
        furniture.removeFromCurrentLocation();
        furniture.addToLocation(this.getGameState().getStoreroom());
    }

    @Override
    public void actOnEntity(Character character) {
        if (character == null) {
            return;
        }
        character.removeFromCurrentLocation();
        character.addToLocation(this.getGameState().getStoreroom());
    }

    @Override
    public void actOnEntity(Location location) {
        Location triggeredLocation = this.getTriggeredLocation();
        if (triggeredLocation != null) {
            triggeredLocation.removeEntity(location);
        }
    }

    @Override
    public void actOnEntity(Player player) {
        if (player != null) {
            player.decreaseHealth();
        }
    }
}
