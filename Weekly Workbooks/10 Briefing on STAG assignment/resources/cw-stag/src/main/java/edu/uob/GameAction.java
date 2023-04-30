package edu.uob;

import java.util.ArrayList;
import java.util.List;

// Note that the action is only valid if ALL subject entities are available to the player.
// If a valid action is found, your server must undertake the relevant additions/removals (production/consumption).
// it is NOT possible to perform an action where a subject, or a consumed or produced entity is currently in another player's inventory
public class GameAction {
    private final List<String> triggerPhrases;
    // One or more subject entities that are acted upon
    // (ALL of which need to be available to perform the action)
    // requires the entity to either be in the inventory of the player invoking the action or
    // for that entity to be in the room/location where the action is being performed
    // subjects of an action can be locations, characters or furniture
    private final List<String> subjectEntityNames;

    // optional, all removed by the action
    // removed from its current location (which could be any location within the game)
    // moved into the storeroom location
    private final List<String> consumedEntityNames;

    // optional, all created by the action
    // moved from its current location in the game (which might be in the storeroom)
    // to the location in which the action is triggered.
    private final List<String> producedEntityNames;
    private String narration;
    private Location triggeredLocation;

    public GameAction() {
        this.triggerPhrases = new ArrayList<>();
        this.subjectEntityNames = new ArrayList<>();
        this.consumedEntityNames = new ArrayList<>();
        this.producedEntityNames = new ArrayList<>();
        this.narration = null;
    }

    public void addTriggerPhrases(String triggerPhrase) {
        this.triggerPhrases.add(triggerPhrase);
    }

    public Location getTriggeredLocation() {
        return this.triggeredLocation;
    }

    public void setTriggeredLocation(Location triggeredLocation) {
        this.triggeredLocation = triggeredLocation;
    }

    public boolean isSubjectEntityName(String entityName) {
        return this.subjectEntityNames.contains(entityName);
    }

    public void addSubjectEntityName(String entityName) {
        this.subjectEntityNames.add(entityName.toLowerCase());
    }

    public boolean isConsumedEntityName(String entityName) {
        return this.consumedEntityNames.contains(entityName);
    }

    public void addConsumedEntityName(String entityName) {
        this.consumedEntityNames.add(entityName.toLowerCase());
    }

    public boolean isProducedEntityName(String entityName) {
        return this.producedEntityNames.contains(entityName);
    }

    public void addProducedEntityName(String entityName) {
        this.producedEntityNames.add(entityName.toLowerCase());
    }

    public void setNarration(String explanation) {
        this.narration = explanation;
    }

    public String getNarration() {
        return this.narration;
    }

    public String performAction(GameState gameState) {
        String playerHealthRunOut = "";
        if (!this.consumedEntityNames.isEmpty()) {
            playerHealthRunOut += this.consumeEntities(gameState);
        }
        if (!this.producedEntityNames.isEmpty()) {
            this.produceEntities(gameState);
        }
        return this.getNarration() + playerHealthRunOut;
    }

    private String consumeEntities(GameState gameState) {
        EntityVisitor consumeVisitor = new ConsumeVisitor(this.triggeredLocation, gameState);
        List<GameEntity> consumedEntities = new ArrayList<>();
        for (String entityName : this.consumedEntityNames) {
            consumedEntities.add(gameState.getEntityByName(entityName));
        }
        for (GameEntity entity : consumedEntities) {
            consumeVisitor.actOnEntity(entity);
        }
        return gameState.checkIfCurrentPlayerHealthRunOut();
    }

    private void produceEntities(GameState gameState) {
        EntityVisitor produceVisitor = new ProduceVisitor(this.triggeredLocation, gameState);
        List<GameEntity> producedEntities = new ArrayList<>();
        for (String entityName : this.producedEntityNames) {
            producedEntities.add(gameState.getEntityByName(entityName));
        }
        for (GameEntity entity : producedEntities) {
            produceVisitor.actOnEntity(entity);
        }
    }

    public boolean isPerformable(GameState gameState) {
        if (this.triggeredLocation == null) {
            return false;
        }
        for (String entityName : this.subjectEntityNames) {
            Location location = gameState.getEntityByName(entityName).getCurrentLocation();
            Player currentPlayer = gameState.getCurrentPlayer();
            if (location == null && currentPlayer.getArtefactByName(entityName) == null) {
                return false;
            } else if (location == gameState.getStoreroom()) {
                return false;
            }
        }
        return true;
    }

    public boolean isGivenValidEntities(List<String> entitiesMentionedInCommand) {
        int subjectMentioned = 0;
        // If you have entities from different actions, it will probably get caught as "extraneous entities"
        int extraneousEntity = 0;
        for (String entityName : entitiesMentionedInCommand) {
            System.out.println("entityName "+ entityName);
            if (this.isSubjectEntityName(entityName)) {
                subjectMentioned += 1;
            } else if (!this.isConsumedEntityName(entityName) && !this.isProducedEntityName(entityName)) {
                extraneousEntity += 1;
            }
        }
        System.out.println("subjectMentioned" + subjectMentioned);
        System.out.println(extraneousEntity);
        return  subjectMentioned >= 1 && extraneousEntity == 0;
    }

    // Trigger keywords can't be used decorations
    public boolean isGivenValidTriggerPhrases(List<String> triggerPhrasesMentioned) {
        for (String phrase : triggerPhrasesMentioned) {
            if (!this.triggerPhrases.contains(phrase)) {
                return false;
            }
        }
        return true;
    }
}
