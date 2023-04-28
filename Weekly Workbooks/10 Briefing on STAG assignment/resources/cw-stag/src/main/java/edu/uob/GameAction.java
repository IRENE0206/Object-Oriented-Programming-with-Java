package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class GameAction {
    List<String> subjectEntities;
    List<String> consumedEntities;
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
