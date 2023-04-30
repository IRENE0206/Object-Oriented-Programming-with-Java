package edu.uob;

import java.util.*;

public class CommandParser {
    private final List<String> basicCommandKeywords;
    private final List<String> basicCommandKeywordsFound;
    private final Set<String> triggerPhrasesFound;
    private final List<String> entityNamesFound;
    private String rawCommandInLowerCase;
    private final List<String> splitCommand;
    private String currentPlayerName;
    private Player currentPlayer;
    private Location playerLocation;
    private final GameState gameState;

    public CommandParser(GameState gameState, String command) {
        // All commands (including entity names, locations, built in commands and action triggers) are case-insensitive
        this.rawCommandInLowerCase = command.toLowerCase();
        this.gameState = gameState;
        this.basicCommandKeywordsFound = new ArrayList<>();
        this.triggerPhrasesFound = new HashSet<>();
        this.entityNamesFound = new ArrayList<>();
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
        this.setCurrentPlayer();
        this.findBuiltinKeywords();
        this.findTriggerPhrases();
        this.findEntityNames();
        String singleBuiltinKeywordMentioned = getTheOnlyBasicCommandKeywordMentioned();
        boolean noTriggerPhrasesFound = this.triggerPhrasesFound.isEmpty();
        int entityNamesCount = this.entityNamesFound.size();
        /*
         System.out.println(singleBuiltinKeywordMentioned);
         System.out.println(noTriggerPhrasesFound);
         System.out.println(entityNamesCount);
        */
        if (singleBuiltinKeywordMentioned != null && noTriggerPhrasesFound && entityNamesCount <= 1) {
            return chooseBasicCommand(singleBuiltinKeywordMentioned);
        } else if (singleBuiltinKeywordMentioned == null && !noTriggerPhrasesFound && entityNamesCount >= 1) {
            return chooseAction();
        }
        return "Invalid syntax";
    }

    private String reformatCommand() {
        // incoming command messages begin with the username of the player issuing that command
        String[] strings = this.rawCommandInLowerCase.split(":", 2);
        if (strings.length != 2) {
            return "Wrong format of incoming command message";
        }
        // everything before the first : is the player's name
        this.currentPlayerName = strings[0].trim().replaceAll("\\s+", " ");
        if (this.isInvalidPlayerName(this.currentPlayerName)) {
            return "Wrong format of player's name";
        }
        // assume actions and entities will only contain alphabetical characters
        // consider the punctuation as decoration
        this.rawCommandInLowerCase = strings[1].replaceAll("[^a-zA-Z0-9]+", " ");
        this.rawCommandInLowerCase = this.rawCommandInLowerCase.trim().replaceAll("\\s+", " ");
        this.splitCommand.addAll(Arrays.stream(this.rawCommandInLowerCase.split(" ")).toList());
        return null;
    }

    private void setCurrentPlayer() {
        Player player = this.gameState.getPlayerByName(this.currentPlayerName);
        if (player != null) {
            this.currentPlayer = player;
        } else {
            // when the server encounters a command from a previously unseen user,
            // a new player should be created and placed in the *start* location of the game
            this.currentPlayer = new Player(this.currentPlayerName, "");
            this.gameState.getStartLocation().addEntity(this.currentPlayer);
            this.gameState.addPlayer(this.currentPlayer);
        }
        this.playerLocation = this.currentPlayer.getCurrentLocation();
        this.gameState.setCurrentPlayer(this.currentPlayer);
    }

    private boolean isInvalidPlayerName(String playerName) {
        List<String> playerNameWords = Arrays.stream(playerName.split(" ")).toList();
        for (String basicCommand : this.basicCommandKeywords) {
            // built-in commands cannot be used as names for any other elements of the command language
            if (playerNameWords.contains(basicCommand)) {
                return true;
            }
        }
        // Valid player names can consist of uppercase and lowercase letters, spaces, apostrophes and hyphens
        return !playerName.matches("[A-Za-z\\s`-]+");
    }

    private void findBuiltinKeywords() {
        for (String word : this.splitCommand) {
            if (this.basicCommandKeywords.contains(word)) {
                this.basicCommandKeywordsFound.add(word);
            }
        }
    }

    private void findTriggerPhrases() {
        for (String triggerPhrase : this.gameState.getActions().keySet()) {
            // action triggers can have spaces
            if (triggerPhrase.contains(" ")) {
                this.findTriggerPhraseWithSpace(triggerPhrase);
            } else if (this.splitCommand.contains(triggerPhrase)) {
                this.triggerPhrasesFound.add(triggerPhrase);
            }
        }
    }

    private void findTriggerPhraseWithSpace(String triggerPhraseWithSpace) {
        String[] triggerPhraseSplit = triggerPhraseWithSpace.split(" ");
        int triggerPhraseWordCount = triggerPhraseSplit.length;
        for (int i = 0; i <= this.splitCommand.size() - triggerPhraseWordCount; i++) {
            if (this.splitCommand.get(i).equalsIgnoreCase(triggerPhraseSplit[0])) {
                String substringOfCommand = String.join(" ", this.splitCommand.subList(i, i + triggerPhraseWordCount));
                if (substringOfCommand.equalsIgnoreCase(triggerPhraseWithSpace)) {
                    this.triggerPhrasesFound.add(triggerPhraseWithSpace);
                }
            }
        }
    }

    private void findEntityNames() {
        // entity names cannot contain spaces
        for (String word : this.splitCommand) {
            if (!this.basicCommandKeywords.contains(word) && this.gameState.getEntityByName(word) != null) {
                this.entityNamesFound.add(word);
            }
        }
    }

    private String getTheOnlyBasicCommandKeywordMentioned() {
        if (this.basicCommandKeywordsFound.size() != 1) {
            return null;
        }
        return this.basicCommandKeywordsFound.get(0);
    }

    private String getTheEntityForBuiltinCommandFound() {
        if (this.entityNamesFound.size() != 1) {
            return null;
        }
        return this.entityNamesFound.get(0);
    }

    // Game Actions
    private String chooseAction() {
        Set<GameAction> allValidActionsMatched = new HashSet<>();
        for (String triggerPhrase : this.triggerPhrasesFound) {
            // System.out.println(triggerPhrase);
            allValidActionsMatched.addAll(this.matchWithTriggerPhrase(triggerPhrase));
        }
        // System.out.println("I mean " + allValidActionsMatched.size());
        GameAction singlePerformableAction = this.getSinglePerformableAction(allValidActionsMatched);
        if (singlePerformableAction == null) {
            return "Invalid command: fail to match a single performable action";
        }
        return singlePerformableAction.performAction(this.gameState);
    }

    private GameAction getSinglePerformableAction(Set<GameAction> allActionsMatched) {
        if (allActionsMatched.size() != 1) {
            return null;
        }
        return allActionsMatched.stream().toList().get(0);
    }

    private Set<GameAction> matchWithTriggerPhrase(String triggerPhrase) {
        Set<GameAction> allValidActionsMatchedWithGivenTriggerPhrase = new HashSet<>();
        for (GameAction gameAction : this.gameState.getPossibleActions(triggerPhrase)) {
            // System.out.println(this.gameState.getPossibleActions(triggerPhrase).size());
            if (this.matchWithAction(gameAction)) {
                // System.out.println("YES");
                allValidActionsMatchedWithGivenTriggerPhrase.add(gameAction);
                // System.out.println(allValidActionsMatchedWithGivenTriggerPhrase.size());
            }
        }
        return allValidActionsMatchedWithGivenTriggerPhrase;
    }

    private boolean matchWithAction(GameAction gameAction) {
        gameAction.setTriggeredLocation(this.playerLocation);
        return gameAction.isGivenValidTriggerPhrases(this.triggerPhrasesFound) &&
                gameAction.isGivenValidEntities(this.entityNamesFound) &&
                gameAction.isPerformable(this.gameState);
    }

    // Basic Commands

    // input must have command first and then subject entity
    private boolean entityNameComeBeforeBuiltinKeyword(String entityNameMentioned, String builtinKeywordMentioned) {
        return this.splitCommand.indexOf(entityNameMentioned) - this.splitCommand.indexOf(builtinKeywordMentioned) <= 0;
    }

    private String chooseBuiltinCommandWithNoEntity(String commandKeyword) {
        if (commandKeyword.equalsIgnoreCase(this.basicCommandKeywords.get(5))) {
            return this.lookCommand();
        } else if (commandKeyword.equalsIgnoreCase(this.basicCommandKeywords.get(6))) {
            return this.healthCommand();
        }
        return this.inventoryCommand();
    }

    private String chooseBuiltinCommandWithOneEntity(String commandKeyword, String entityNameFound) {
        if (commandKeyword.equalsIgnoreCase(this.basicCommandKeywords.get(2))) {
            return this.getArtefactCommand(entityNameFound);
        } else if (commandKeyword.equalsIgnoreCase(this.basicCommandKeywords.get(3))) {
            return this.dropArtefactCommand(entityNameFound);
        } else if (commandKeyword.equalsIgnoreCase(this.basicCommandKeywords.get(4))){
            return this.goToLocationCommand(entityNameFound);
        }
        return "Invalid syntax for " + commandKeyword;
    }

    private String chooseBasicCommand(String commandKeyword) {
        if (this.entityNamesFound.size() == 0) {
            return chooseBuiltinCommandWithNoEntity(commandKeyword);
        }
        String gameEntityName = getTheEntityForBuiltinCommandFound();
        if (gameEntityName == null || entityNameComeBeforeBuiltinKeyword(gameEntityName, commandKeyword)) {
            return "Invalid syntax for " + commandKeyword;
        }
        return chooseBuiltinCommandWithOneEntity(commandKeyword, gameEntityName);
    }

    private String inventoryCommand() {
        return "All the artefacts currently being carried by " + this.currentPlayerName + ":\n" +
                String.join(",", this.currentPlayer.getArtefactNames());
    }

    private String getArtefactCommand(String gameEntityName) {
        String locationName = this.playerLocation.getName();
        Artefact artefact = this.playerLocation.getArtefactByName(gameEntityName);
        if (artefact != null) {
            this.currentPlayer.pickUpArtefact(artefact);
            return this.currentPlayerName + " picks up " + gameEntityName +
                    " from the " + locationName + " and adds it into inventory";
        }
        return "Cannot pick up " + gameEntityName + " from " + locationName;
    }

    private String dropArtefactCommand(String gameEntityName) {
        Artefact artefact = this.currentPlayer.getArtefactByName(gameEntityName);
        if (artefact == null) {
            return "There is no " + gameEntityName + " in " + this.currentPlayerName +"'s inventory";
        }
        this.currentPlayer.dropArtefact(gameEntityName);
        return this.currentPlayerName + "puts down " + gameEntityName +
                "from inventory and places it into " + this.playerLocation.getName();
    }

    private String goToLocationCommand(String gameEntityName) {
        String currentLocationName = this.playerLocation.getName();
        Location destination = this.playerLocation.getDestinationByName(gameEntityName);
        if (destination == null) {
            return "There is no path to " + gameEntityName + " from " + currentLocationName;
        }
        this.currentPlayer.addToLocation(destination);
        return this.currentPlayerName + " moves from " + currentLocationName + " to " + gameEntityName;
    }

    private String lookCommand() {
        return this.playerLocation.observedByCurrentPlayer(this.currentPlayerName);
    }

    private String healthCommand() {
        return String.valueOf(this.currentPlayer.getHealth());
    }

}
