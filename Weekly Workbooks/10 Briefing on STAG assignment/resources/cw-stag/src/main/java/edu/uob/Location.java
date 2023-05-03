package edu.uob;

import java.util.HashMap;

// Rooms or places within the game
public class Location extends GameEntity {
    // Artefacts that are currently present in a location
    private final HashMap<String, Artefact> artefacts;
    // Furniture that belongs within a location
    private final HashMap<String, Furniture> furniture;
    // Characters that are currently at a location
    private final HashMap<String, Character> characters;
    // Paths to other locations
    private final HashMap<String, Location> locations;
    private final HashMap<String, Player> players;

    public Location(String name, String description) {
        super(name, description);
        this.artefacts = new HashMap<>();
        this.furniture = new HashMap<>();
        this.characters = new HashMap<>();
        this.locations = new HashMap<>();
        this.players = new HashMap<>();
        this.setCurrentLocation(this);
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
    public void addToGameState(GameState gameState) {
        gameState.addEntity(this);
    }

    @Override
    public void getActedUpon(EntityVisitor entityVisitor) {
        entityVisitor.actOnEntity(this);
    }

    public void removeEntity(Artefact artefact) {
        if (artefact != null) {
            this.artefacts.remove(artefact.getName());
        }
    }

    public void removeEntity(Furniture furnitureToRemove) {
        if (furnitureToRemove != null) {
            this.furniture.remove(furnitureToRemove.getName());
        }
    }

    public void removeEntity(Character character) {
        if (character != null) {
            this.characters.remove(character.getName());
        }
    }

    public void removeEntity(Location location) {
        if (location != null) {
            this.locations.remove(location.getName());
        }
    }

    public void removeEntity(Player player) {
        if (player != null) {
            this.players.remove(player.getName());
        }
    }

    public void addEntity(GameEntity gameEntity) {
        if (gameEntity != null) {
            gameEntity.addToLocation(this);
        }
    }

    public void addEntity(Artefact artefact) {
        if (artefact != null) {
            this.artefacts.put(artefact.getName(), artefact);
            artefact.setCurrentLocation(this);
        }
    }

    public void addEntity(Furniture furnitureToAdd) {
        if (furnitureToAdd != null) {
            this.furniture.put(furnitureToAdd.getName(), furnitureToAdd);
            furnitureToAdd.setCurrentLocation(this);
        }
    }

    public void addEntity(Character character) {
        if (character != null) {
            this.characters.put(character.getName(), character);
            character.setCurrentLocation(this);
        }
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
            this.players.put(player.getName(), player);
            player.setCurrentLocation(this);
        }
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

    public String observedByCurrentPlayer(String currentPlayerName) {
        // prints names and descriptions of entities in the current location
        StringBuilder information = new StringBuilder(" * current location * \n");
        information.append(this.getName()).append(": ").append(this.getDescription()).append("\n");
        information.append(" * artefacts * \n");
        for (String artefactName : this.artefacts.keySet()) {
            information.append(artefactName).append(": ")
                    .append(this.getArtefactByName(artefactName).getDescription()).append("\n");
        }
        information.append(" * furniture * \n");
        for (String furnitureName : this.furniture.keySet()) {
            information.append(furnitureName).append(": ")
                    .append(this.getFurnitureByName(furnitureName).getDescription()).append("\n");
        }
        information.append(" * characters * \n");
        for (String characterName : this.characters.keySet()) {
            information.append(characterName).append(": ")
                    .append(this.getCharacterByName(characterName).getDescription()).append("\n");
        }
        // lists paths to other locations
        information.append(" * paths to * \n");
        for (String locationName : this.locations.keySet()) {
            information.append(locationName).append(" ");
        }
        information.append("\n");
        // include other players in your description of a location when a look command is issued by a user
        if (this.players.size() > 1) {
            information.append(" * Other players here * \n");
            for (String playerName : this.players.keySet()) {
                if (!playerName.equalsIgnoreCase(currentPlayerName)) {
                    information.append(playerName).append("\n");
                }
            }
        }
        return information.toString();
    }

}
