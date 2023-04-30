package edu.uob;

import java.util.HashMap;
import java.util.Set;

// represents the user in the game
public class Player extends GameEntity {
    private final HashMap<String, Artefact> artefacts;
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
        if (location == null) {
            return;
        }
        this.removeFromCurrentLocation();
        location.addEntity(this);
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

    public Artefact getArtefactByName(String artefactName) {
        return this.artefacts.get(artefactName);
    }

    public Set<String> getArtefactNames() {
        return this.artefacts.keySet();
    }

    public void dropArtefact(String artefactName) {
        Artefact artefact = this.artefacts.get(artefactName);
        if (artefact == null) {
            return;
        }
        // puts down an artefact from player's inventory
        this.artefacts.remove(artefactName);
        artefact.setCurrentOwner(null);
        // places it into the current location
        this.getCurrentLocation().addEntity(artefact);
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

    private void restoreHealth() {
        this.health = 3;
    }

    public String checkHealthRunOut(Location startLocation) {
        if (this.health != 0) {
            return "";
        }
        // When a player's health runs out (i.e. when it becomes zero)
        // they should lose all items in their inventory
        for (String artefactName : this.artefacts.keySet()) {
            // (which are dropped in the location where they ran out of health)
            this.dropArtefact(artefactName);
        }
        // The player should then be transported to the start location of the game and
        this.addToLocation(startLocation);
        // their health level restored to full (i.e. 3)
        this.restoreHealth();
        return this.getName() + " died and lost all items, must return to the start of the game\n";
    }

}
