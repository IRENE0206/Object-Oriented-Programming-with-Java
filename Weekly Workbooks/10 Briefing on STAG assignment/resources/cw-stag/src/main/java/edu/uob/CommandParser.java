package edu.uob;

import java.util.*;

public class CommandParser {
    private final List<String> basicCommandKeywords;
    private final List<String> basicCommandKeywordsFound;
    private final List<String> triggerPhrasesFound;
    private final List<String> entityNamesFound;
    private String rawCommandInLowerCase;
    private final List<String> splitCommand;
    private String currentPlayerName;
    private Player currentPlayer;
    private Location playerLocation;
    private final GameState gameState;

    public CommandParser(GameState gameState, String command) {
        this.basicCommandKeywordsFound = new ArrayList<>();
        this.triggerPhrasesFound = new ArrayList<>();
        this.entityNamesFound = new ArrayList<>();
        // All commands (including entity names, locations, built in commands and action triggers)
        // should be treated as case-insensitive
        this.rawCommandInLowerCase = command.toLowerCase();
        this.gameState = gameState;
        this.splitCommand = new ArrayList<>();
        this.basicCommandKeywords = new ArrayList<>();
        this.basicCommandKeywords.add("inventory");
        this.basicCommandKeywords.add("inv");
        this.basicCommandKeywords.add("get");
        this.basicCommandKeywords.add("drop");
        this.basicCommandKeywords.add("goto");
        this.basicCommandKeywords.add("look");
        this.basicCommandKeywords.add("health");
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
        findBuiltinKeywords();
        findTriggerPhrases();
        findEntityNames();
        String singleBuiltinKeywordMentioned = getTheBuiltinCommandType();
        boolean noTriggerPhrasesFound = this.triggerPhrasesFound.isEmpty();
        int entitiesMentionedCount = this.entityNamesFound.size();
        System.out.println(singleBuiltinKeywordMentioned);
        System.out.println(noTriggerPhrasesFound);
        System.out.println(entitiesMentionedCount);
        if (singleBuiltinKeywordMentioned != null && noTriggerPhrasesFound && entitiesMentionedCount <= 1) {
            return chooseBasicCommand(singleBuiltinKeywordMentioned);
        } else if (singleBuiltinKeywordMentioned == null && !noTriggerPhrasesFound && entitiesMentionedCount >= 1) {
            return chooseMatchingAction();
        }
        return "Invalid syntax";
    }

    private String reformatCommand() {
        // incoming command messages begin with the username of the player who issued that command
        String[] strings = this.rawCommandInLowerCase.split(":", 2);
        if (strings.length != 2) {
            return "Wrong format incoming command message";
        }
        // everything before the first : is the player's name
        this.currentPlayerName = strings[0];
        // assume actions and entities will only contain alphabetical characters
        // action triggers can have spaces as well
        // consider the punctuation as decoration, thus allowing you to process the command
        this.rawCommandInLowerCase = strings[1].replaceAll("[^a-zA-Z0-9]+", " ");
        this.rawCommandInLowerCase = this.rawCommandInLowerCase.trim().replaceAll("\\s+", " ");
        this.splitCommand.addAll(Arrays.stream(this.rawCommandInLowerCase.split(" ")).toList());
        return null;
    }

    private String setCurrentPlayer() {
        if (this.gameState.getPlayer(this.currentPlayerName) != null) {
            this.currentPlayer = this.gameState.getPlayer(this.currentPlayerName);
        } else if (!isValidPlayerName(this.currentPlayerName)) {
            return "Invalid player name";
        } else {
            // when the server encounters a command from a previously unseen user,
            // a new player should be created and placed in the start location of the game
            this.currentPlayer = new Player(currentPlayerName, "");
            this.currentPlayer.setCurrentLocation(this.gameState.getStartLocation());
            this.gameState.addPlayer(this.currentPlayer);
        }
        this.playerLocation = this.currentPlayer.getCurrentLocation();
        this.gameState.setCurrentPlayer(this.currentPlayer);
        return null;
    }

    // built-in commands are reserved words and
    // therefore cannot be used as names for any other elements of the command language
    // Valid player names can consist of uppercase and lowercase letters, spaces, apostrophes and hyphens
    private boolean isValidPlayerName(String playerName) {
        List<String> playerNameWords = Arrays.stream(playerName.split(" ")).toList();
        for (String basicCommand : this.basicCommandKeywords) {
            if (playerNameWords.contains(basicCommand)) {
                return false;
            }
        }
        return playerName.matches("[A-Za-z\\s`-]+");
    }

    private void findBuiltinKeywords() {
        for (String s : this.splitCommand) {
            if (this.basicCommandKeywords.contains(s)) {
                this.basicCommandKeywordsFound.add(s);
            }
        }
    }

    private void findTriggerPhrases() {
        for (String triggerPhrase : this.gameState.getActions().keySet()) {
            if (triggerPhrase.contains(" ")) {
                List<String> triggerPhraseSplit = Arrays.stream(triggerPhrase.split(" ")).toList();
                int triggerPhraseWordCount = triggerPhraseSplit.size();
                for (int i = 0; i < this.splitCommand.size(); i++) {
                    if (this.splitCommand.get(i).equalsIgnoreCase(triggerPhraseSplit.get(0))) {
                        String substringOfCommand = String.join(" ", this.splitCommand.subList(i, i + triggerPhraseWordCount));
                        if (substringOfCommand.equalsIgnoreCase(triggerPhrase)) {
                            this.triggerPhrasesFound.add(substringOfCommand);
                        }
                    }
                }
            } else if (this.splitCommand.contains(triggerPhrase)) {
                this.triggerPhrasesFound.add(triggerPhrase);
            }
        }
    }

    private void findEntityNames() {
        for (String word : this.splitCommand) {
            System.out.println("word: "+ word);
            if (this.gameState.getEntityByName(word) != null) {
                this.entityNamesFound.add(word);
            }
        }
    }

    private String getTheBuiltinCommandType() {
        if (this.basicCommandKeywordsFound.size() != 1) {
            return null;
        }
        return this.basicCommandKeywordsFound.get(0);
    }

    private String getTheEntityForBuiltinCommand() {
        if (this.entityNamesFound.size() != 1) {
            return null;
        }
        return this.entityNamesFound.get(0);
    }

    // Game Actions
    private String chooseMatchingAction() {
        Set<GameAction> allValidActionsMatched = new HashSet<>();
        for (String triggerPhrase : this.triggerPhrasesFound) {
            System.out.println(triggerPhrase);
            allValidActionsMatched.addAll(matchWithTriggerPhrase(triggerPhrase));
        }
        System.out.println("I mean " + allValidActionsMatched.size());
        GameAction singlePerformableAction = getSinglePerformableAction(allValidActionsMatched);
        if (singlePerformableAction == null) {
            return "Invalid command: fail to match a single performable action";
        } else {
            return singlePerformableAction.performAction(this.gameState);
        }
    }

    private GameAction getSinglePerformableAction(Set<GameAction> allActionsMatched) {
        if (allActionsMatched.size() != 1) {
            return null;
        }
        return allActionsMatched.stream().toList().get(0);
    }

    private List<GameAction> matchWithTriggerPhrase(String triggerPhrase) {
        List<GameAction> allValidActionsMatchedWithTriggerPhrase = new ArrayList<>();
        for (GameAction gameAction : this.gameState.getPossibleActions(triggerPhrase)) {
            System.out.println("HERE");
            System.out.println(this.gameState.getPossibleActions(triggerPhrase).size());
            if (matchWithAction(gameAction)) {
                System.out.println("YES");
                allValidActionsMatchedWithTriggerPhrase.add(gameAction);
                System.out.println(allValidActionsMatchedWithTriggerPhrase.size());
            }
        }
        return allValidActionsMatchedWithTriggerPhrase;
    }

    /*
    It is also worth explicitly distinguishing between:
- valid: a command which structurally correct (for example, action commands have a trigger key phrase and at least one subject)
- matched: the trigger key phrase and subject(s) of a command in an incoming message are found in the HashMap of actions
- performable: all the subjects of an action from the MashMap are "available" to the player
     */

    private boolean matchWithAction(GameAction gameAction) {
        gameAction.setTriggeredLocation(this.playerLocation);
        return gameAction.isGivenValidTriggerPhrases(this.triggerPhrasesFound) &&
                gameAction.isPerformable(this.gameState) &&
                gameAction.isGivenValidEntities(this.entityNamesFound);
    }

    // Basic Commands
    // input must have command first and then subject entity
    private boolean entityComeBeforeBuiltinKeyword(String entityMentioned, String builtinKeywordMentioned) {
        return this.splitCommand.indexOf(entityMentioned) - this.splitCommand.indexOf(builtinKeywordMentioned) <= 0;
    }

    private String chooseBuiltinCommandWithNoEntity(String commandKeyword) {
        if (commandKeyword.equalsIgnoreCase(this.basicCommandKeywords.get(5))) {
            return this.lookCommand();
        } else if (commandKeyword.equalsIgnoreCase(this.basicCommandKeywords.get(6))) {
            return this.healthCommand();
        }
        else {
            return this.inventoryCommand();
        }
    }

    private String chooseBuiltinCommandWithOneEntity(String commandKeyword, String gameEntityName) {
        if (commandKeyword.equalsIgnoreCase(this.basicCommandKeywords.get(2))) {
            return this.getArtefactCommand(gameEntityName);
        } else if (commandKeyword.equalsIgnoreCase(this.basicCommandKeywords.get(3))) {
            return this.dropArtefactCommand(gameEntityName);
        } else if (commandKeyword.equalsIgnoreCase(this.basicCommandKeywords.get(4))){
            return this.goToLocationCommand(gameEntityName);
        } else {
            return "Invalid syntax for " + commandKeyword;
        }
    }

    private String chooseBasicCommand(String commandKeyword) {
        if (this.entityNamesFound.size() == 0) {
            return chooseBuiltinCommandWithNoEntity(commandKeyword);
        } else {
            String gameEntityName = getTheEntityForBuiltinCommand();
            if (gameEntityName == null || entityComeBeforeBuiltinKeyword(gameEntityName, commandKeyword)) {
                return "Invalid syntax for " + commandKeyword;
            }
            return chooseBuiltinCommandWithOneEntity(commandKeyword, gameEntityName);
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
    private String getArtefactCommand(String gameEntityName) {
        String locationName = this.playerLocation.getName();
        Artefact artefact = this.playerLocation.getArtefactByName(gameEntityName);
        if (artefact != null) {
            this.currentPlayer.pickArtefact(artefact);
            return this.currentPlayerName + " picks up " + gameEntityName +
                    " from the " + locationName + " and adds it into inventory";
        }
        return "Cannot pick up " + gameEntityName + " from " + locationName;
    }

    // "drop": puts down an artefact from player's inventory and
    // places it into the current location
    private String dropArtefactCommand(String gameEntityName) {
        String locationName = this.playerLocation.getName();
        Artefact artefact = this.currentPlayer.getArtefacts().get(gameEntityName);
        if (artefact == null) {
            return "Cannot put down " + gameEntityName + " from " + this.currentPlayerName +"'s inventory";
        }
        this.currentPlayer.dropArtefact(gameEntityName);
        return "puts down " + gameEntityName + "from " +
                this.currentPlayerName + "'s inventory and places it into " + locationName;
    }

    // "goto": moves the player to the specified location
    // (if there is a path to that location)
    private String goToLocationCommand(String gameEntityName) {
        String currentLocationName = this.playerLocation.getName();
        Location destination = this.playerLocation.getDestinationByName(gameEntityName);
        if (destination == null) {
            return "Cannot move to " + gameEntityName + " from " + currentLocationName;
        }
        this.currentPlayer.addToLocation(destination);
        return this.currentPlayerName + " moves from " + currentLocationName + " to " + destination.getName();
    }

    // "look": prints names and descriptions of entities in the current location and
    // lists paths to other locations
    // keep the location descriptions for when you are in that location and do a look
    private String lookCommand() {
        return this.playerLocation.showAllInformation(this.currentPlayerName);
    }

    private String healthCommand() {
        return String.valueOf(this.currentPlayer.getHealth());
    }

}
