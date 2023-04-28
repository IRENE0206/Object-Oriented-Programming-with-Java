package edu.uob;

import java.util.HashMap;
import java.util.HashSet;

public class GameState {
    private Location startLocation;
    private Location storeroom;
    private final HashMap<String, Location> locations;
    private final HashMap<String, HashSet<GameAction>> actions;
    private final HashMap<String, Player> players;

    public GameState() {
        this.players = new HashMap<>();
        this.locations = new HashMap<>();
        this.actions = new HashMap<>();
    }

    public void setStartLocation(Location l) {
        this.startLocation = l;
    }

    public Location getStartLocation() {
        return this.startLocation;
    }

    public void setStoreroom(Location l) {
        this.storeroom = l;
    }

    public Location getStoreroom() {
        return this.storeroom;
    }

    public void addLocation(Location l) {
        this.locations.put(l.getName(), l);
    }

    public Location getLocationByName(String locationName) {
        if (locationName.equalsIgnoreCase(this.startLocation.getName())) {
            return this.startLocation;
        } else if (locationName.equalsIgnoreCase(this.storeroom.getName())) {
            return this.storeroom;
        }
        return this.locations.get(locationName);
    }

    public boolean hasLocation(String locationName) {
        if (this.startLocation.getName().equalsIgnoreCase(locationName) || this.storeroom.getName().equalsIgnoreCase(locationName)) {
            return true;
        }
        return this.locations.containsKey(locationName.toLowerCase());
    }

    public void addPlayer(Player player) {
        this.players.put(player.getName(), player);
    }

    public Player getPlayer(String playerName) {
        return this.players.get(playerName);
    }

    public boolean hasPlayer(String playerName) {
        return this.players.containsKey(playerName);
    }

    public boolean hasTriggerPhrase(String phrase) {
        return this.actions.containsKey(phrase);
    }

    public void addAction(String triggerPhrase, GameAction gameAction) {
        if (this.hasTriggerPhrase(triggerPhrase)) {
            this.actions.get(triggerPhrase).add(gameAction);
        } else {
            HashSet<GameAction> gameActions = new HashSet<>();
            gameActions.add(gameAction);
            this.actions.put(triggerPhrase, gameActions);
        }
    }

    public HashSet<GameAction> getPossibleActions(String triggerPhrase) {
        return this.actions.get(triggerPhrase);
    }
}
