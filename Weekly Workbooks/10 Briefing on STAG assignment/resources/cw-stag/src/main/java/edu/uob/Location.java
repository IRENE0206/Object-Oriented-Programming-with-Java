package edu.uob;

import java.util.HashMap;

public class Location extends GameEntity {
    private HashMap<String, Artefact> artefacts;
    private HashMap<String, Furniture> furnitures;
    private HashMap<String, Character> characters;
    private HashMap<String, Location> locations;

    public Location(String name, String description) {
        super(name, description);
        this.artefacts = new HashMap<>();
        this.furnitures = new HashMap<>();
        this.characters = new HashMap<>();
        this.locations = new HashMap<>();
    }

    public boolean hasArtefact(String artefactName) {
        return this.artefacts.containsKey(artefactName);
    }

    public void addArtefact(Artefact artefact) {
        this.artefacts.put(artefact.getName(), artefact);
    }

    public void removeArtefact(String artefactName) {
        this.artefacts.remove(artefactName);
    }

    public boolean hasFurniture(String furnitureName) {
        return this.furnitures.containsKey(furnitureName);
    }

    public void addFurniture(Furniture furniture) {
        this.furnitures.put(furniture.getName(), furniture);
    }

    public void removeFurniture(String furnitureName) {
        this.furnitures.remove(furnitureName);
    }

    public boolean hasCharacter(String characterName) {
        return this.characters.containsKey(characterName);
    }

    public void addCharacter(Character character) {
        this.characters.put(character.getName(), character);
    }

    public void removeCharacter(String characterName) {
        this.characters.remove(characterName);
    }

    public boolean hasPathToLocation(String locationName) {
        return this.locations.containsKey(locationName);
    }
    public HashMap<String, Location> getPathsToLocations() {
        return this.locations;
    }

    public HashMap<String, Artefact> getArtefacts() {
        return this.artefacts;
    }

    public HashMap<String, Furniture> getFurnitures() {
        return furnitures;
    }

    public HashMap<String, Character> getCharacters() {
        return characters;
    }

    public void addPathToLocation(Location location) {
        this.locations.put(location.getName(), location);
    }

    public void removePathToLocation(String locationName) {
        this.locations.remove(locationName);
    }
}
