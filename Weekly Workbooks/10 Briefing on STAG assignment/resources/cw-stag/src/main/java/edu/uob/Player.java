package edu.uob;

import java.util.HashMap;

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
        this.setCurrentLocation(location);
    }

    @Override
    public void getActedUpon(EntityVisitor entityVisitor) {
        entityVisitor.actOnEntity(this);
    }

    public boolean hasArtefact(String artefactName) {
        return this.artefacts.containsKey(artefactName);
    }

    public void pickArtefact(Artefact artefact) {
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

    public HashMap<String, Artefact> getArtefacts() {
        return this.artefacts;
    }

    public void dropArtefact(String artefactName) {
        Artefact artefact = this.artefacts.get(artefactName);
        if (artefact == null) {
            return;
        }
        this.artefacts.remove(artefactName);
        artefact.setCurrentOwner(null);
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

    public void gotoLocation(Location location) {
        if (location == null) {
            return;
        }
        this.addToLocation(location);
    }

}
