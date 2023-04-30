package edu.uob;

import java.util.HashMap;

public class Location extends GameEntity {
    private final HashMap<String, Artefact> artefacts;
    private final HashMap<String, Furniture> furniture;
    private final HashMap<String, Character> characters;
    private final HashMap<String, Location> locations;
    private final HashMap<String, Player> players;

    public Location(String name, String description) {
        super(name, description);
        this.artefacts = new HashMap<>();
        this.furniture = new HashMap<>();
        this.characters = new HashMap<>();
        this.locations = new HashMap<>();
        this.players = new HashMap<>();
    }

    @Override
    public void removeFromCurrentLocation() {
    }

    @Override
    public void addToLocation(Location location) {
        if (location != null) {
            location.addEntity(this);
        }
    }

    @Override
    public void getActedUpon(EntityVisitor entityVisitor) {
        entityVisitor.actOnEntity(this);
    }

    public void removeEntity(Artefact artefact) {
        if (artefact != null) {
            this.removeArtefact(artefact.getName());
        }
    }

    private void removeArtefact(String artefactName) {
        this.artefacts.remove(artefactName);
    }

    public void removeEntity(Furniture furniture) {
        if (furniture != null) {
            this.removeFurniture(furniture.getName());
        }
    }

    private void removeFurniture(String furnitureName) {
        this.furniture.remove(furnitureName);
    }

    public void removeEntity(Character character) {
        if (character != null) {
            this.removeCharacter(character.getName());
        }
    }

    private void removeCharacter(String characterName) {
        this.characters.remove(characterName);
    }

    public void removeEntity(Location location) {
        if (location != null) {
            this.removePathToLocation(location.getName());
        }
    }

    private void removePathToLocation(String locationName) {
        this.locations.remove(locationName);
    }

    public void removeEntity(Player player) {
        if (player != null) {
            this.removePlayerFromLocation(player.getName());
        }
    }

    private void removePlayerFromLocation(String playerName) {
        this.players.remove(playerName);
    }

    public void addEntity(GameEntity gameEntity) {
        gameEntity.addToLocation(this);
    }

    public void addEntity(Artefact artefact) {
        if (artefact != null) {
            this.addArtefact(artefact);
            artefact.setCurrentLocation(this);
        }
    }

    private void addArtefact(Artefact artefact) {
        if (artefact != null) {
            this.artefacts.put(artefact.getName(), artefact);
        }
    }

    public void addEntity(Furniture furniture) {
        if (furniture != null) {
            this.addFurniture(furniture);
            furniture.setCurrentLocation(this);
        }
    }

    private void addFurniture(Furniture furniture) {
        this.furniture.put(furniture.getName(), furniture);
    }

    public void addEntity(Character character) {
        if (character != null) {
            this.addCharacter(character);
            character.setCurrentLocation(this);
        }
    }

    private void addCharacter(Character character) {
        this.characters.put(character.getName(), character);
    }

    public void addEntity(Location location) {
        if (location != null) {
            this.addPathToLocation(location);
        }
    }

    private void addPathToLocation(Location location) {
        this.locations.put(location.getName(), location);
    }

    public void addEntity(Player player) {
        if (player != null) {
            this.addPlayerToLocation(player.getName(), player);
            player.setCurrentLocation(this);
        }
    }

    private void addPlayerToLocation(String playerName, Player player) {
        this.players.put(playerName, player);
    }

    public boolean hasArtefact(String artefactName) {
        return this.artefacts.containsKey(artefactName);
    }

    public boolean hasFurniture(String furnitureName) {
        return this.furniture.containsKey(furnitureName);
    }

    public boolean hasCharacter(String characterName) {
        return this.characters.containsKey(characterName);
    }

    public Location getDestinationByName(String destinationName) {
        return this.locations.get(destinationName);
    }

    public Artefact getArtefactByName(String artefactName) {
        return this.artefacts.get(artefactName);
    }

    public Furniture getFurnitureByName(String furnitureName) {
        return this.furniture.get(furnitureName);
    }

    public Character getCharacterByName(String characterName) {
        return this.characters.get(characterName);
    }

    public int getPathsNumber() {
        return this.locations.size();
    }

    public String showAllInformation(String currentPlayerName) {
        StringBuilder information = new StringBuilder("Location:\n");
        information.append(this.getName()).append(": ")
                .append(this.getDescription()).append("\n").append("Artefacts:\n");
        for (String artefactName : this.artefacts.keySet()) {
            information.append(artefactName).append(": ")
                    .append(this.getArtefactByName(artefactName).getDescription()).append("\n");
        }
        information.append("Furniture:\n");
        for (String furnitureName : this.furniture.keySet()) {
            information.append(furnitureName).append(": ")
                    .append(this.getFurnitureByName(furnitureName).getDescription()).append("\n");
        }
        information.append("Characters:\n");
        for (String characterName : this.characters.keySet()) {
            information.append(characterName).append(": ")
                    .append(this.getCharacterByName(characterName).getDescription()).append("\n");
        }
        information.append("Paths to:\n");
        for (String locationName : this.locations.keySet()) {
            information.append(locationName).append(" ");
        }
        information.append("\n");
        // include other players in your description of a location when a look command is issued by a user
        information.append("Other players:\n");
        for (String playerName : this.players.keySet()) {
            if (!playerName.equalsIgnoreCase(currentPlayerName)) {
                information.append(playerName).append("\n");
            }
        }
        return information.toString();
    }

}
