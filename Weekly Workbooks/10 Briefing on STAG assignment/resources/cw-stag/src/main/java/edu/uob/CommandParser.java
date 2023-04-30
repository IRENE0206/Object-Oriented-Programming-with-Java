package edu.uob;

import java.util.*;

public class CommandParser {
    List<String> basicCommands;
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
        this.basicCommands = new ArrayList<>();
        this.basicCommands.add("inventory");
        this.basicCommands.add("inv");
        this.basicCommands.add("get");
        this.basicCommands.add("drop");
        this.basicCommands.add("goto");
        this.basicCommands.add("look");
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
        List<String> basicCommandsMentioned = getBuiltinKeywordsMentioned();
        List<String> triggerPhrasesMentioned = getTriggerPhrasesMentioned();
        List<GameEntity> entitiesMentioned = getEntitiesMentioned();
        String builtinKeyword = getTheBuiltinCommandType(basicCommandsMentioned);
        if (builtinKeyword != null && triggerPhrasesMentioned.isEmpty() && entitiesMentioned.size() <= 1) {
            return chooseBasicCommand(builtinKeyword, entitiesMentioned);
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
        if (this.gameState.getPlayer(this.currentPlayerName) != null) {
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

    private List<String> getBuiltinKeywordsMentioned() {
        List<String> basicMentioned = new ArrayList<>();
        for (String s : this.splitCommand) {
            if (this.basicCommands.contains(s)) {
                basicMentioned.add(s);
            }
        }
        return basicMentioned;
    }

    private List<String> getTriggerPhrasesMentioned() {
        List<String> triggerPhrasesMentioned = new ArrayList<>();
        for (String triggerPhrase : this.gameState.getActions().keySet()) {
            if (triggerPhrase.contains(" ")) {
                List<String> triggerPhraseSplit = Arrays.stream(triggerPhrase.split(" ")).toList();
                int triggerPhraseWordCount = triggerPhraseSplit.size();
                for (int i = 0; i < this.splitCommand.size(); i++) {
                    if (this.splitCommand.get(i).equalsIgnoreCase(triggerPhraseSplit.get(0))) {
                        String substringOfCommand = String.join(" ", this.splitCommand.subList(i, i + triggerPhraseWordCount));
                        if (substringOfCommand.equalsIgnoreCase(triggerPhrase)) {
                            triggerPhrasesMentioned.add(substringOfCommand);
                        }
                    }
                }
            } else if (this.splitCommand.contains(triggerPhrase)) {
                triggerPhrasesMentioned.add(triggerPhrase);
            }
        }
        return triggerPhrasesMentioned;
    }

    private List<GameEntity> getEntitiesMentioned() {
        List<GameEntity> entitiesMentioned = new ArrayList<>();
        for (String word : this.splitCommand) {
            GameEntity gameEntity = this.gameState.getEntityByName(word);
            if (gameEntity != null) {
                entitiesMentioned.add(gameEntity);
            }
        }
        return entitiesMentioned;
    }

    private String getTheBuiltinCommandType(List<String> basicCommandMentioned) {
        if (basicCommandMentioned.size() != 1) {
            return null;
        }
        return basicCommandMentioned.get(0);
    }

    private GameEntity getTheEntityForBuiltinCommand(List<GameEntity> entitiesMentioned) {
        if (entitiesMentioned.size() != 1) {
            return null;
        }
        return entitiesMentioned.get(0);
    }

    // Basic Commands

    // input must have command first and then subject entity
    private boolean entityComeBeforeBuiltinKeyword(String entityMentioned, String builtinKeywordMentioned) {
        return this.splitCommand.indexOf(entityMentioned) - this.splitCommand.indexOf(builtinKeywordMentioned) <= 0;
    }

    private String chooseBuiltinCommandWithNoEntity(String commandKeyword) {
        if (commandKeyword.equalsIgnoreCase(this.basicCommands.get(5))) {
            return this.lookCommand();
        } else {
            return this.inventoryCommand();
        }
    }

    private String chooseBuiltinCommandWithOneEntity(String commandKeyword, GameEntity gameEntity) {
        if (commandKeyword.equalsIgnoreCase(this.basicCommands.get(2))) {
            return this.getArtefactCommand(gameEntity);
        } else if (commandKeyword.equalsIgnoreCase(this.basicCommands.get(3))) {
            return this.dropArtefactCommand(gameEntity);
        } else {
            return this.goToLocationCommand(gameEntity);
        }
    }

    private String chooseBasicCommand(String commandKeyword, List<GameEntity> entitiesMentioned) {
        if (entitiesMentioned.size() == 0) {
            return chooseBuiltinCommandWithNoEntity(commandKeyword);
        } else {
            GameEntity gameEntity = getTheEntityForBuiltinCommand(entitiesMentioned);
            if (gameEntity == null || entityComeBeforeBuiltinKeyword(gameEntity.getName(), commandKeyword)) {
                return "Invalid syntax for " + commandKeyword;
            }
            return chooseBuiltinCommandWithOneEntity(commandKeyword, gameEntity);
        }
    }

    // "inventory" (or "inv" for short):
    // lists all artefacts currently being carried by the player
    private String inventoryCommand() {
        List<String> artefactsCarried = new ArrayList<>(this.currentPlayer.getArtefacts().keySet());
        return "All the artefacts currently being carried by " + this.currentPlayerName + ":\n" +
                String.join(",", artefactsCarried);
    }

    // "get": picks up a specified artefact from the current location and
    // adds it into player's inventory
    private String getArtefactCommand(GameEntity gameEntity) {
        String entityName = gameEntity.getName();
        String locationName = this.playerLocation.getName();
        Artefact artefact = this.playerLocation.getArtefactByName(entityName);
        if (artefact != null) {
            this.currentPlayer.pickArtefact(artefact);
            return this.currentPlayerName + " picks up " + entityName +
                    " from the " + locationName + " and adds it into inventory";
        }
        return "Cannot pick up " + entityName + " from " + locationName;
    }

    // "drop": puts down an artefact from player's inventory and
    // places it into the current location
    private String dropArtefactCommand(GameEntity gameEntity) {
        String entityName = gameEntity.getName();
        String locationName = this.playerLocation.getName();
        Artefact artefact = this.currentPlayer.getArtefacts().get(entityName);
        if (artefact == null) {
            return "Cannot put down " + entityName + " from " + this.currentPlayerName +"'s inventory";
        }
        this.currentPlayer.dropArtefact(entityName);
        return "puts down " + entityName + "from " +
                this.currentPlayerName + "'s inventory and places it into " + locationName;
    }

    // "goto": moves the player to the specified location
    // (if there is a path to that location)
    private String goToLocationCommand(GameEntity gameEntity) {
        String entityName = gameEntity.getName();
        String currentLocationName = this.playerLocation.getName();
        Location destination = this.playerLocation.getDestinationByName(entityName);
        if (destination == null) {
            return "Cannot move to " + entityName + " from " + currentLocationName;
        }
        this.currentPlayer.addToLocation(destination);
        return this.currentPlayerName + " moves from " + currentLocationName + " to " + destination.getName();
    }

    // "look": prints names and descriptions of entities in the current location and
    // lists paths to other locations
    // keep the location descriptions for when you are in that location and do a look
    private String lookCommand() {
        return this.playerLocation.showAllInformation();
    }

}
