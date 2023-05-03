package edu.uob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// represents the user in the game
public class Player extends GameEntity {
    private final Map<String, Artefact> artefacts;
    private int health;

    public Player(String name, String description) {
        super(name, description);
        this.artefacts = new HashMap<>();
        this.health = 3;
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
    public void addToGameState(GameState gameState) {
        gameState.addEntity(this);
    }

    @Override
    public void getActedUpon(EntityVisitor entityVisitor) {
        entityVisitor.actOnEntity(this);
    }

    public void pickUpArtefact(Artefact artefact) {
        if (artefact == null) {
            return;
        }
        artefact.removeFromCurrentLocation();
        this.artefacts.put(artefact.getName(), artefact);
        artefact.setCurrentOwner(this);
    }

    public boolean doesNotHaveArtefact(String artefactName) {
        return !this.artefacts.containsKey(artefactName);
    }

    public String showInventoryContents() {
        StringBuilder information = new StringBuilder("All the artefacts currently being carried by ");
        information.append(this.getName()).append(":\n");
        for (Artefact artefact : this.artefacts.values()) {
            information.append(artefact.getDescription()).append("\n");
        }
        return information.toString();
    }

    public void dropArtefact(String artefactName) {
        Artefact artefact = this.artefacts.get(artefactName);
        if (artefact != null) {
            artefact.addToLocation(this.getCurrentLocation());
        }
        // puts down an artefact from player's inventory
        // places it into the current location
    }

    public void removeArtefact(String artefactName) {
        this.artefacts.remove(artefactName);
    }

    public int getHealth() {
        return this.health;
    }

    public void increaseHealth() {
        if (this.health < 3) {
            this.health += 1;
        }
    }

    public void decreaseHealth() {
        if (this.health > 0) {
            this.health -= 1;
        }
    }

    public String dieIfHealthRunOut(Location startLocation) {
        if (this.health != 0) {
            return "";
        }
        // When a player's health runs out (i.e. when it becomes zero)
        // they should lose all items in their inventory
        Set<String> artefactNames = new HashSet<>(this.artefacts.keySet());
        for (String artefactName : artefactNames) {
            // (which are dropped in the location where they ran out of health)
            this.dropArtefact(artefactName);
        }
        // The player should then be transported to the start location of the game and
        this.addToLocation(startLocation);
        // their health level restored to full (i.e. 3)
        this.health = 3;
        return this.getName() + " died and lost all items, returned to the start of the game\n";
    }

}
