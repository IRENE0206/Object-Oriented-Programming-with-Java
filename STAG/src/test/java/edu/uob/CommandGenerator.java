package edu.uob;

import java.util.*;
import static edu.uob.ExtendedActionsHelper.*;
import static edu.uob.ExtendedEntitiesHelper.*;

public final class CommandGenerator {

    private static String validCharactersForPlayerName;
    private static final String[] builtinCommands = {"inventory", "inv", "get", "drop", "goto", "look", "health"};

    private static Set<String> reservedWords;
    private static Set<String> decorateWords;
    private static final Random random;

    static {
        setValidCharactersForPlayerName();
        setReservedWords();
        setDecorateWords();
        random = new Random();
    }

    private static void setValidCharactersForPlayerName() {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c = 'a'; c <= 'z'; c += 1) {
            stringBuilder.append(c);
        }
        stringBuilder.append(stringBuilder.toString().toUpperCase());
        stringBuilder.append("`");
        stringBuilder.append("-");
        stringBuilder.append(" ");
        validCharactersForPlayerName = stringBuilder.toString();
    }

    public static String generateRandomPlayerName(String name) {
        int randomNameLength = random.nextInt(1, 11);
        StringBuilder stringBuilder = new StringBuilder(name);
        for (int i = 0; i < randomNameLength; i++) {
            int randomIndex = random.nextInt(validCharactersForPlayerName.length());
            stringBuilder.append(validCharactersForPlayerName.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }

    private static void setReservedWords() {
        reservedWords = new HashSet<>();
        reservedWords.addAll(Arrays.stream(builtinCommands).toList());
        reservedWords.addAll(getAllEntityNames());
        reservedWords.addAll(getAllTriggers());
    }

    private static boolean isReservedWord(String string) {
        return reservedWords.contains(string.toLowerCase());
    }

    private static String randomizeString(List<String> stringToRandomize) {
        String charsToInsert = " `~!@#$%^&*()-_+={}[]|\":;'<,>.?/\\";
        StringBuilder stringBuilder = new StringBuilder();
        for (String part : stringToRandomize) {
            stringBuilder.append(" ");
            stringBuilder.append(charsToInsert.charAt(random.nextInt(charsToInsert.length())));
            for (int i = 0; i < part.length(); i++) {
                // Case Insensitivity
                if (random.nextBoolean()) {
                    stringBuilder.append(String.valueOf(part.charAt(i)).toLowerCase());
                } else {
                    stringBuilder.append(String.valueOf(part.charAt(i)).toUpperCase());
                }
            }
        }
        stringBuilder.append(charsToInsert.charAt(random.nextInt(charsToInsert.length())));
        return stringBuilder.toString();
    }

    public static String generateValidBuiltinCommand(String playerName, String builtinCommand, String entityName) {
        List<String> words = new ArrayList<>();
        words.add(builtinCommand);
        words.add(entityName);
        insertDecorateWord(words);
        return playerName + ":" + randomizeString(words);
    }

    // Decorated Commands
    private static void insertDecorateWord(List<String> words) {
        int randomNum = random.nextInt(5);
        for (int i = 0; i < randomNum; i++) {
            int decorateWordIndex = random.nextInt(decorateWords.size());
            int randomIndex = random.nextInt(words.size() + 1);
            words.add(randomIndex, decorateWords.stream().toList().get(decorateWordIndex));
        }

    }

    private static void insertReservedWord(List<String> words) {
        int randomNum = random.nextInt(1, 5);
        for (int i = 0; i < randomNum; i++) {
            int reservedWordIndex = random.nextInt(reservedWords.size());
            int randomIndex = random.nextInt(words.size() + 1);
            words.add(randomIndex, reservedWords.stream().toList().get(reservedWordIndex));
        }

    }

    private static void setDecorateWords() {
        Set<String> wordsInDescription = new HashSet<>();
        for (String entityName : getAllEntityNames()) {
            String description = getDescriptionOfEntity(entityName).toLowerCase();
            wordsInDescription.addAll((Arrays.stream(description.replaceAll("\\s+", " ").trim().split(" ")).toList()));
        }
        decorateWords = new HashSet<>(wordsInDescription);
        for (String wordInDescription : wordsInDescription) {
            if (isReservedWord(wordInDescription)) {
                decorateWords.remove(wordInDescription);
            }
        }
    }

    public static String generateInvalidBuiltinCommand(String playerName, String builtinCommand, String entityName) {
        List<String> words = new ArrayList<>();
        words.add(builtinCommand);
        words.add(entityName);
        insertReservedWord(words);
        return playerName + ":" + randomizeString(words);
    }

    public static String generateValidActionCommand(String playerName, int actionIndex) {
        Set<String> triggers = getActionTriggers(actionIndex);
        Set<String> subjects = getActionSubjects(actionIndex);
        List<String> selected = new ArrayList<>();
        int triggerCount = 0;
        int subjectCount = 0;
        while (triggerCount == 0) {
            for (String trigger : triggers) {
                if (random.nextBoolean()) {
                    selected.add(trigger);
                    triggerCount += 1;
                }
            }
        }
        while (subjectCount == 0) {
            for (String subject : subjects) {
                if (random.nextBoolean()) {
                    selected.add(subject);
                    subjectCount += 1;
                }
            }
        }
        insertDecorateWord(selected);
        List<String> result = new ArrayList<>(selected);
        for (String word : selected) {
            if (random.nextBoolean()) {
                result.remove(word);
                result.add(word);
            }
        }
        return playerName + ":" + randomizeString(result);
    }

    public static String validInvCommand(String playerName) {
        String inventoryOrInv = chooseInventoryOrInvRandomly();
        return generateValidBuiltinCommand(playerName, inventoryOrInv, "");
    }

    public static String invalidInvCommand(String playerName) {
        String inventoryOrInv = chooseInventoryOrInvRandomly();
        return generateInvalidBuiltinCommand(playerName, inventoryOrInv, "");
    }

    private static String chooseInventoryOrInvRandomly() {
        if (random.nextBoolean()) {
            return builtinCommands[0];
        }
        return builtinCommands[1];
    }

    public static String validGetCommand(String playerName, String entityName) {
        return generateValidBuiltinCommand(playerName, builtinCommands[2], entityName);
    }

    public static String invalidGetCommand(String playerName, String entityName) {
        return generateInvalidBuiltinCommand(playerName, builtinCommands[2], entityName);
    }

    public static String validDropCommand(String playerName, String entityName) {
        return generateValidBuiltinCommand(playerName, builtinCommands[3], entityName);
    }

    public static String invalidDropCommand(String playerName, String entityName) {
        return generateInvalidBuiltinCommand(playerName, builtinCommands[3], entityName);
    }

    public static String validGotoCommand(String playerName, String locationName) {
        return generateValidBuiltinCommand(playerName, builtinCommands[4], locationName);
    }

    public static String invalidGotoCommand(String playerName, String locationName) {
        return generateInvalidBuiltinCommand(playerName, builtinCommands[4], locationName);
    }

    public static String validLookCommand(String playerName) {
        return generateValidBuiltinCommand(playerName, builtinCommands[5], "");
    }

    public static String invalidLookCommand(String playerName) {
        return generateInvalidBuiltinCommand(playerName, builtinCommands[5], "");
    }

    public static String validHealthCommand(String playerName) {
        return generateValidBuiltinCommand(playerName, builtinCommands[6], "");
    }
}
