package edu.uob;

import java.util.HashMap;

public class Player extends GameEntity {
    private Location currentLocation;
    private final HashMap<String, Artefact> artefacts;
    private int health;

    public Player(String name, String description) {
        super(name, description);
        this.artefacts = new HashMap<>();
        this.health = 3;
    }

    public void setCurrentLocation(Location l) {
        this.currentLocation = l;
    }

    public Location getCurrentLocation() {
        return this.currentLocation;
    }

    public boolean hasArtefact(String artefactName) {
        return this.artefacts.containsKey(artefactName);
    }

    public void pickArtefact(Artefact artefact) {
        this.artefacts.put(artefact.getName(), artefact);
    }

    public HashMap<String, Artefact> getArtefacts() {
        return this.artefacts;
    }

    public void dropArtefact(String artefactName) {
        this.artefacts.remove(artefactName);
    }

    public int getHealth() {
        return this.health;
    }

}
