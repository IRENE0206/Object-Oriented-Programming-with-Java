package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommandParser {
    String[] basicCommands = {"inventory", "inv", "get", "drop", "goto", "look"};
    private String command;
    private final List<String> splitCommand;
    private String currentPlayerName;
    private Player currentPlayer;
    private Location playerLocation;
    private final GameState gameState;

    public CommandParser(GameState gameState, String command) {
        this.command = command.toLowerCase();
        this.gameState = gameState;
        this.splitCommand = new ArrayList<>();
    }

    public String parseCommand() {
        String commandFormatError = this.reformatCommand();
        if (commandFormatError != null) {
            return commandFormatError;
        }
        String playerNameError = this.setCurrentPlayer();
        if (playerNameError != null) {
            return playerNameError;
        }
        List<String> basicCommandsContained = findBasicCommand();
        List<String> triggerPhrasesContained = findTriggerPhrases();
        if (findNoSpecifiedCommand(triggerPhrasesContained) && findOnlyOne(basicCommandsContained)) {
            return this.chooseBasicCommand(basicCommandsContained.get(0));
        }
        return "";
    }

    private String reformatCommand() {
        String[] strings = this.command.split(":", 2);
        if (strings.length != 2) {
            return "Wrong format incoming command message";
        }
        this.currentPlayerName = strings[0];
        this.command = strings[1].trim().replaceAll("\\s+", " ");
        this.splitCommand.addAll(Arrays.stream(this.command.split(" ")).toList());
        return null;
    }

    private String setCurrentPlayer() {
        if (this.gameState.hasPlayer(this.currentPlayerName)) {
            this.currentPlayer = this.gameState.getPlayer(this.currentPlayerName);
        } else if (!isValidPlayerName(this.currentPlayerName)) {
            return "Invalid player name";
        } else {
            this.currentPlayer = new Player(currentPlayerName, "");
            this.currentPlayer.setCurrentLocation(this.gameState.getStartLocation());
            this.gameState.addPlayer(this.currentPlayer);
        }
        this.playerLocation = this.currentPlayer.getCurrentLocation();
        return null;
    }

    // built-in commands are reserved words and
    // therefore cannot be used as names for any other elements of the command language
    private boolean isValidPlayerName(String playerName) {
        List<String> playerNameWords = Arrays.stream(playerName.split(" ")).toList();
        for (String basicCommand : this.basicCommands) {
            if (playerNameWords.contains(basicCommand)) {
                return false;
            }
        }
        return true;
    }

    private List<String> findTriggerPhrases() {
        List<String> triggerPhrasesContained = new ArrayList<>();
        for (String triggerPhrase : this.gameState.getActions().keySet()) {
            if (this.splitCommand.contains(triggerPhrase.toLowerCase())) {
                triggerPhrasesContained.add(triggerPhrase);
            }
        }
        return triggerPhrasesContained;
    }

    private boolean findNoSpecifiedCommand(List<String> found) {
        return found.size() == 0;
    }

    private boolean findOnlyOne(List<String> found) {
        return found.size() == 1;
    }

    // game-specific commands
    /* private String getMatchedActions() {
        List<String> triggerPhrasesFound = findTriggerPhrases(this.command);
        if (!findOnlyOne(triggerPhrasesFound)) {

        // }
    } */

    // Basic Commands
    private List<String> findBasicCommand() {
        List<String> containedCommands = new ArrayList<>();
        for (String s : this.basicCommands) {
            if (this.splitCommand.contains(s)) {
                containedCommands.add(s);
            }
        }
        return containedCommands;
    }

    private String chooseBasicCommand(String commandType) {
        int index = this.splitCommand.indexOf(commandType);
        if (commandType.equalsIgnoreCase("inventory") || commandType.equalsIgnoreCase("inv")) {
            return this.inventoryCommand();
        } else if (commandType.equalsIgnoreCase("get")) {
            return this.getArtefactCommand(index);
        } else if (commandType.equalsIgnoreCase("drop")) {
            return this.dropArtefactCommand(index);
        } else if (commandType.equalsIgnoreCase("goto")) {
            return this.goToLocationCommand(index);
        } else {
            return this.lookCommand();
        }
    }

    // "inventory" (or "inv" for short):
    // lists all artefacts currently being carried by the player
    private String inventoryCommand() {
        HashMap<String, Artefact> artefacts = this.currentPlayer.getArtefacts();
        List<String> artefactsCarried = new ArrayList<>(artefacts.keySet());
        return "All the artefacts currently being carried by " + this.currentPlayerName + ": " +
                String.join(" ", artefactsCarried);
    }

    // "get": picks up a specified artefact from the current location and
    // adds it into player's inventory
    private String getArtefactCommand(int index) {
        List<String> artefacts = new ArrayList<>();
        for (String artefactName : this.playerLocation.getArtefacts().keySet()) {
            if (this.splitCommand.subList(0, index).contains(artefactName)) {
                return "Invalid syntax";
            }
            if (this.splitCommand.subList(index + 1, this.splitCommand.size()).contains(artefactName)) {
                artefacts.add(artefactName);
            }
        }
        if (artefacts.size() != 1) {
            return "Invalid syntax";
        }
        String artefactName = artefacts.get(0);
        Artefact artefact = this.playerLocation.getArtefacts().get(artefactName);
        this.currentPlayer.pickArtefact(artefact);
        this.playerLocation.removeArtefact(artefactName);
        return "picks up " + artefactName +
                "from the " + this.playerLocation.getName() + " and adds it into " +
                this.currentPlayerName + "'s inventory";
    }

    // "drop": puts down an artefact from player's inventory and
    // places it into the current location
    private String dropArtefactCommand(int index) {
        List<String> artefacts = new ArrayList<>();
        for (String artefactName : this.currentPlayer.getArtefacts().keySet()) {
            if (this.splitCommand.subList(0, index).contains(artefactName)) {
                return "Invalid syntax";
            }
            if (this.splitCommand.subList(index + 1, this.splitCommand.size()).contains(artefactName)) {
                artefacts.add(artefactName);
            }
        }
        if (artefacts.size() != 1) {
            return "Invalid artefact";
        }
        String artefactName = artefacts.get(0);
        Artefact artefact = this.currentPlayer.getArtefacts().get(artefactName);
        this.currentPlayer.dropArtefact(artefactName);
        this.playerLocation.addArtefact(artefact);
        return "puts down " + artefactName + "from " +
                this.currentPlayerName + "'s inventory and places it into " +
                this.playerLocation.getName();
    }

    // "goto": moves the player to the specified location
    // (if there is a path to that location)
    private String goToLocationCommand(int index) {
        List<String> locations = new ArrayList<>();
        for (String locationName : this.playerLocation.getPathsToLocations().keySet()) {
            if (this.splitCommand.subList(0, index).contains(locationName)) {
                return "Invalid syntax";
            }
            if (this.splitCommand.subList(index + 1, this.splitCommand.size()).contains(locationName)) {
                locations.add(locationName);
            }
        }
        if (locations.size() != 1) {
            return "Invalid location";
        }
        String destinationName = locations.get(0);
        Location destination = this.playerLocation.getPathsToLocations().get(destinationName);
        this.currentPlayer.setCurrentLocation(destination);
        return "moves player" + this.currentPlayerName + "to " + destinationName;
    }

    // "look": prints names and descriptions of entities in the current location and
    // lists paths to other locations
    private String lookCommand() {
        StringBuilder currentLocation = new StringBuilder("Current location:\n");
        currentLocation.append(this.playerLocation.getName())
                .append(": ").append(this.playerLocation.getDescription()).append("\n");
        StringBuilder artefacts = new StringBuilder("Artefacts:\n");
        for (Artefact artefact : this.playerLocation.getArtefacts().values()) {
            artefacts.append(artefact.getName()).append(": ").append(artefact.getDescription()).append("\n");
        }
        StringBuilder furnitures = new StringBuilder("Furnitures:\n");
        for (Furniture furniture : this.playerLocation.getFurniture().values()) {
            furnitures.append(furniture.getName()).append(": ").append(furniture.getDescription()).append("\n");
        }
        StringBuilder characters = new StringBuilder("Characters:\n");
        for (Character character : this.playerLocation.getCharacters().values()) {
            characters.append(character.getName()).append(": ").append(character.getDescription()).append("\n");
        }
        StringBuilder locations = new StringBuilder("Paths to:\n");
        for (Location location : this.playerLocation.getPathsToLocations().values()) {
            locations.append(location.getName()).append(": ").append(location.getDescription()).append("\n");
        }
        return currentLocation.append(artefacts).append(furnitures).append(characters).append(locations).toString();
    }

}
