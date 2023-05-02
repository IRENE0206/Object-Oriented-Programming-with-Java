package edu.uob;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GameAction {
    // One or more possible trigger phrases (*ANY* of which can be used to initiate the action)
    private final List<String> triggerPhrases;

    /**
     * One or more* subject entities
     * ALL of which need to be available to perform the action:
     * in the inventory of the player invoking the action OR in the room/location where the action is being *performed*
     * subjects of an action can be locations, characters or furniture
     */
    private final List<String> subjectEntityNames;

    /**
     * optional, all removed by the action
     * removed from its current location (which could be *any* location within the game)
     * moved into the storeroom location
     */
    private final List<String> consumedEntityNames;

    /**
     * optional, all created by the action
     * moved from its current location (which might be in the storeroom)
     * to the location in which the action is triggered.
     */
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

    public void setTriggeredLocation(Location triggeredLocation) {
        this.triggeredLocation = triggeredLocation;
    }

    public boolean isSubjectEntityName(String entityName) {
        return this.subjectEntityNames.contains(entityName);
    }

    public void addSubjectEntityName(String entityName) {
        this.subjectEntityNames.add(entityName);
    }

    public boolean isConsumedEntityName(String entityName) {
        return this.consumedEntityNames.contains(entityName);
    }

    public void addConsumedEntityName(String entityName) {
        this.consumedEntityNames.add(entityName);
    }

    public boolean isProducedEntityName(String entityName) {
        return this.producedEntityNames.contains(entityName);
    }

    public void addProducedEntityName(String entityName) {
        this.producedEntityNames.add(entityName);
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
        if (playerHealthRunOut.isEmpty()) {
            return this.getNarration();
        } else {
            return this.getNarration() + "\n" + playerHealthRunOut;
        }
    }

    private String consumeEntities(GameState gameState) {
        EntityVisitor consumeVisitor = new ConsumeVisitor(this.triggeredLocation, gameState);
        List<GameEntity> entitiesToConsume = new ArrayList<>();
        for (String entityName : this.consumedEntityNames) {
            entitiesToConsume.add(gameState.getEntityByName(entityName));
        }
        for (GameEntity entity : entitiesToConsume) {
            consumeVisitor.actOnEntity(entity);
        }
        return gameState.checkIfCurrentPlayerHealthRunOut();
    }

    private void produceEntities(GameState gameState) {
        EntityVisitor produceVisitor = new ProduceVisitor(this.triggeredLocation, gameState);
        List<GameEntity> entitiesToProduce = new ArrayList<>();
        for (String entityName : this.producedEntityNames) {
            entitiesToProduce.add(gameState.getEntityByName(entityName));
        }
        for (GameEntity entity : entitiesToProduce) {
            produceVisitor.actOnEntity(entity);
        }
    }

    // it is NOT possible to perform an action where a subject, or a consumed or produced entity
    // is currently in another player's inventory
    public boolean isPerformable(GameState gameState) {
        if (this.triggeredLocation == null) {
            return false;
        }
        // is only valid if ALL subject entities are available to the player.
        for (String entityName : this.subjectEntityNames) {
            Location entityLocation = gameState.getEntityByName(entityName).getCurrentLocation();
            if (entityLocation == null && this.isArtefactInOtherPlayerInv(entityName, gameState)) {
                return false;
            } else if (entityLocation != null && entityLocation != this.triggeredLocation) {
                return false;
            }
        }
        return this.listedEntitiesNotInOtherPlayerInv(this.consumedEntityNames, gameState) &&
                this.listedEntitiesNotInOtherPlayerInv(this.producedEntityNames, gameState);
    }

    private boolean listedEntitiesNotInOtherPlayerInv(List<String> entityNames, GameState gameState) {
        for (String entityName : entityNames) {
            Location entityLocation = gameState.getEntityByName(entityName).getCurrentLocation();
            if (entityLocation == null && this.isArtefactInOtherPlayerInv(entityName, gameState)) {
                return false;
            }
        }
        return true;
    }

    private boolean isArtefactInOtherPlayerInv(String entityName, GameState gameState) {
        return gameState.getCurrentPlayer().getArtefactByName(entityName) == null;
    }

    public boolean isGivenValidEntityNames(List<String> entityNamesMentioned) {
        int subjectEntitiesCount = 0;
        for (String entityName : entityNamesMentioned) {
            // System.out.println("entityName "+ entityName);
            if (this.isSubjectEntityName(entityName)) {
                subjectEntitiesCount += 1;
            } else if (!this.isConsumedEntityName(entityName) && !this.isProducedEntityName(entityName)) {
                // entities from different actions as "extraneous entities"
                return false;
            }
        }
        return  subjectEntitiesCount >= 1;
    }

    // Trigger keywords can't be used as decorations
    public boolean isGivenValidTriggerPhrases(Set<String> triggerPhrasesMentioned) {
        for (String phrase : triggerPhrasesMentioned) {
            if (!this.triggerPhrases.contains(phrase)) {
                return false;
            }
        }
        return true;
    }
}
