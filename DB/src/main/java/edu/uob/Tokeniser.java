package edu.uob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tokeniser {
    private final String[] specialCharacters = {"(", ")", ",", ";", "<=", ">=", "!=", "=="};
    private final String[] otherSpecialCharacters = {"<", ">", "="};
    private List<String> tokens = new ArrayList<>();
    private int currentToken;

    public Tokeniser(String query) {
        this.setup(query);
    }

    private void setup(String query) {
        String queryString = query.trim();
        String[] fragments = queryString.split("'");
        for (int i = 0; i < fragments.length; i++) {
            if (i % 2 != 0) {
                tokens.add("'" + fragments[i] + "'");
            } else {
                String[] nextBatchOfTokens = tokenize(fragments[i]);
                tokens.addAll(furtherTokenize(nextBatchOfTokens));
            }
        }
        this.currentToken = 0;
    }

    private String[] tokenize(String input) {
        String inputString = input;
        for (String specialCharacter : specialCharacters) {
            inputString = inputString.replace(specialCharacter, " " + specialCharacter + " ");
        }
        while (inputString.contains("  ")) {
            inputString = inputString.replaceAll("  ", " ");
        }
        inputString = inputString.trim();
        return inputString.split(" ");
    }

    private List<String> furtherTokenize(String[] strings) {
        List<String> result = new ArrayList<>();
        for (String s : strings) {
            if (stringHasNoOtherSpecialCharacters(s) || Arrays.asList(specialCharacters).contains(s)) {
                result.add(s);
            } else {
                for (String otherSpecialCharacter : otherSpecialCharacters) {
                    s = s.replace(otherSpecialCharacter, " " + otherSpecialCharacter + " ");
                }
                while (s.contains("  ")) {
                    s = s.replaceAll("  ", " ");
                }
                s = s.trim();
                result.addAll(Arrays.asList(s.split(" ")));
            }
        }
        return result;
    }

    private boolean stringHasNoOtherSpecialCharacters(String s) {
        for (String otherSpecialCharacter : otherSpecialCharacters) {
            if (s.contains(otherSpecialCharacter)) {
                return false;
            }
        }
        return true;
    }

    public void nextToken() {
        if (this.hasNextToken()) {
            this.currentToken += 1;
        }
    }

    public boolean hasNextToken() {
        return this.currentToken < this.tokens.size() - 1;
    }

    public String getToken() {
        return this.tokens.get(this.currentToken);
    }
}
