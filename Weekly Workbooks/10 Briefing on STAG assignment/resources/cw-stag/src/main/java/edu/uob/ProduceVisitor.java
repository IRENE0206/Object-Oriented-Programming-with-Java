package edu.uob;

// When an entity is produced, it should be moved from its current location (which might be in the storeroom)
// to the location in which the action was triggered.
// The entity should NOT automatically appear in a players inventory
public class ProduceVisitor extends EntityVisitor {

    public ProduceVisitor(Location triggeredLocation, Location storeroom) {
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
            artefact.addToLocation(this.getTriggeredLocation());
        }
    }

    @Override
    public void actOnEntity(Furniture furniture) {
        if (furniture != null) {
            furniture.addToLocation(this.getTriggeredLocation());
        }
    }

    @Override
    public void actOnEntity(Character character) {
        if (character != null) {
            character.addToLocation(this.getTriggeredLocation());
        }
    }

    // For produced locations, a new (one-way) path is added from the current location to the "produced" location
    @Override
    public void actOnEntity(Location location) {
        Location triggeredLocation = this.getTriggeredLocation();
        if (triggeredLocation != null) {
            triggeredLocation.addEntity(location);
        }
    }

    @Override
    public void actOnEntity(Player player) {
        if (player != null) {
            player.increaseHealth();
        }
    }
}
