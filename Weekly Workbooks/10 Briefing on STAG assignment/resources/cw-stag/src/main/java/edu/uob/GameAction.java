package edu.uob;

import java.util.ArrayList;
import java.util.List;

// Note that the action is only valid if ALL subject entities are available to the player.
// If a valid action is found, your server must undertake the relevant additions/removals (production/consumption).
// it is NOT possible to perform an action where a subject, or a consumed or produced entity is currently in another player's inventory
public class GameAction {
    // One or more subject entities that are acted upon
    // (ALL of which need to be available to perform the action)
    // requires the entity to either be in the inventory of the player invoking the action or
    // for that entity to be in the room/location where the action is being performed
    // subjects of an action can be locations, characters or furniture
    List<String> subjectEntities;

    // An optional set of consumed entities that are all removed by the action
    // it should be removed from its current location
    // (which could be any location within the game) moved into the storeroom location
    List<String> consumedEntities;

    // An optional set of produced entities that are all created by the action
    // When an entity is produced,
    // it should be moved from its current location in the game
    // (which might be in the storeroom) to the location in which the action is triggered.
    List<String> producedEntities;
    String narration;

    public GameAction() {
        this.subjectEntities = new ArrayList<>();
        this.consumedEntities = new ArrayList<>();
        this.producedEntities = new ArrayList<>();
        this.narration = null;
    }

    public boolean hasSubjectEntity(String entityName) {
        return this.subjectEntities.contains(entityName);
    }

    public void addSubjectEntity(String entityName) {
        this.subjectEntities.add(entityName);
    }

    public List<String> getSubjectEntities() {
        return this.subjectEntities;
    }

    public boolean hasConsumedEntity(String entityName) {
        return this.consumedEntities.contains(entityName);
    }

    public void addConsumedEntity(String entityName) {
        this.consumedEntities.add(entityName);
    }

    public List<String> getConsumedEntities() {
        return this.consumedEntities;
    }

    public boolean hasProducedEntity(String entityName) {
        return this.producedEntities.contains(entityName);
    }

    public void addProducedEntity(String entityName) {
        this.producedEntities.add(entityName);
    }

    public List<String> getProducedEntities() {
        return this.producedEntities;
    }

    public void setNarration(String explanation) {
        this.narration = explanation;
    }

    public String getNarration() {
        return this.narration;
    }
}
