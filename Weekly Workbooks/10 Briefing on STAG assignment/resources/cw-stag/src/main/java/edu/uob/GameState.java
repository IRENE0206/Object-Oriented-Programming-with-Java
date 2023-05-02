package edu.uob;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GameState {
    // a "special" location that is the starting point for an adventure
    private Location startLocation;
    // a container for all entities that have no initial location in the game
    private Location storeroom;
    private final HashMap<String, Location> locations;

    // action trigger keyPhrases are NOT unique
    // trigger phrases cannot (and will not) contain the names of entities
    private final HashMap<String, HashSet<GameAction>> actions;
    private final HashMap<String, Player> players;

    private Player currentPlayer;
    // entity names defined in the configuration files will be unique
    // there should only be a single instance of each entity within the game
    private final HashMap<String, GameEntity> entities;

    public GameState() {
        this.players = new HashMap<>();
        this.locations = new HashMap<>();
        this.actions = new HashMap<>();
        this.entities = new HashMap<>();
    }

    public void setStartLocation(Location l) {
        if (l == null) {
            return;
        }
        this.startLocation = l;
        this.entities.put(l.getName(), l);
    }

    public Location getStartLocation() {
        return this.startLocation;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    // a container for all entities that have no initial location in the game
    // These entities will not enter the game until
    // an action places them into another location within the game
    // there will be no paths to/from it
    public void setStoreroom(Location l) {
        if (l == null) {
            return;
        }
        this.storeroom = l;
        this.entities.put(l.getName(), l);
    }

    public Location getStoreroom() {
        return this.storeroom;
    }

    public void addLocation(Location l) {
        if (l == null) {
            return;
        }
        this.locations.put(l.getName(), l);
        this.entities.put(l.getName(), l);
    }

    public Location getLocationByName(String locationName) {
        if (locationName.equalsIgnoreCase(this.startLocation.getName())) {
            return this.startLocation;
        } else if (locationName.equalsIgnoreCase(this.storeroom.getName())) {
            return this.storeroom;
        }
        return this.locations.get(locationName.toLowerCase());
    }

    public void addPlayer(Player player) {
        this.players.put(player.getName(), player);
    }

    public Player getPlayerByName(String playerName) {
        return this.players.get(playerName);
    }

    public void addAction(String triggerPhrase, GameAction gameAction) {
        triggerPhrase = triggerPhrase.toLowerCase();
        if (this.actions.get(triggerPhrase) != null) {
            this.actions.get(triggerPhrase).add(gameAction);
        } else {
            HashSet<GameAction> gameActions = new HashSet<>();
            gameActions.add(gameAction);
            this.actions.put(triggerPhrase, gameActions);
        }
    }

    public HashSet<GameAction> getPossibleActions(String triggerPhrase) {
        return this.actions.get(triggerPhrase.toLowerCase());
    }

    public Set<String> getTriggerPhrases() {
        return this.actions.keySet();
    }

    public GameEntity getEntityByName(String entityName) {
        if (entityName.equalsIgnoreCase("health")) {
            return this.currentPlayer;
        }
        return this.entities.get(entityName.toLowerCase());
    }

    public void addEntity(Character character) {
        if (character == null) {
            return;
        }
        this.entities.put(character.getName(), character);
    }

    public void addEntity(Artefact artefact) {
        if (artefact == null) {
            return;
        }
        this.entities.put(artefact.getName(), artefact);
    }

    public void addEntity(Furniture furniture) {
        if (furniture == null) {
            return;
        }
        this.entities.put(furniture.getName(), furniture);
    }

    public String checkIfCurrentPlayerHealthRunOut() {
        return this.currentPlayer.checkHealthRunOut(this.startLocation);
    }
}
