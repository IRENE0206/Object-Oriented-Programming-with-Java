package edu.uob;

// When an entity is consumed it should be removed from its current location (which could be any location)
// moved into the storeroom location.
// If the game writer wants to enforce co-location (i.e. the consumed entity must be in the same location as the player)
// then they must include that entity as a subject of the action.
public class ConsumeVisitor extends EntityVisitor {
    // if an entity (artefact/furniture/character) is in the player's current location,
    // it is removed and moved to the storeroom
    // if an entity (artefact/furniture/character) is NOT in the player's current location,
    // it is still removed and moved to the storeroom
    public ConsumeVisitor(Location triggeredLocation, Location storeroom) {
        super(triggeredLocation, storeroom);
    }

    @Override
    public void actOnEntity(GameEntity gameEntity) {
        if (gameEntity != null) {
            gameEntity.getActedUpon(this);
        }
    }

    @Override
    public void actOnEntity(Artefact artefact) {
        if (artefact != null) {
            // if an artefact is in the player's inventory,
            // it is removed and moved to the storeroom
            artefact.addToLocation(this.getStoreroom());
        }
    }

    @Override
    public void actOnEntity(Furniture furniture) {
        if (furniture != null) {
            furniture.addToLocation(this.getStoreroom());
        }
    }

    @Override
    public void actOnEntity(Character character) {
        if (character != null) {
            character.addToLocation(this.getStoreroom());
        }
    }

    // Consumed locations are not moved to the storeroom
    // instead, the path between the current location and consumed location is removed
    // (there may still be other paths to that location in other game locations).
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
