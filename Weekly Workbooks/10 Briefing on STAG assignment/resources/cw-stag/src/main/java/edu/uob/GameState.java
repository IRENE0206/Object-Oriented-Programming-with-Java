package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

public class GameState {
    private Location startLocation;
    private Location storeroom;
    private final HashMap<String, Location> locations;
    // One or more possible trigger phrases (ANY of which can be used to initiate the action)
    // action trigger keyPhrases are NOT unique
    // trigger phrases cannot (and will not) contain the names of entities
    private final HashMap<String, HashSet<GameAction>> actions;
    private final HashMap<String, Player> players;

    private Player currentPlayer;
    // entity names cannot contain spaces
    private final HashMap<String, GameEntity> entities;

    public GameState() {
        this.players = new HashMap<>();
        this.locations = new HashMap<>();
        this.actions = new HashMap<>();
        this.entities = new HashMap<>();
    }

    public void setStartLocation(Location l) {
        this.startLocation = l;
        addEntity(l);
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
        this.storeroom = l;
        addEntity(l);
    }

    public Location getStoreroom() {
        return this.storeroom;
    }

    public void addLocation(Location l) {
        this.locations.put(l.getName(), l);
        addEntity(l);
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

    public Player getPlayer(String playerName) {
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

    public void addEntity(GameEntity entity) {
        if (entity != null) {
            this.entities.put(entity.getName(), entity);
        }
    }

    public HashMap<String, HashSet<GameAction>> getActions() {
        return this.actions;
    }

    public GameEntity getEntityByName(String entityName) {
        return this.entities.get(entityName.toLowerCase());
    }
}
