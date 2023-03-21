package edu.uob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tokeniser {
    private final String[] specialCharacters = {"(", ")", ",", ";"};
    private List<String> tokens = new ArrayList<String>();
    private int currentToken;

    public Tokeniser(String query) {
        this.setup(query);
    }

    private void setup(String query) {
        query = query.trim();
        String[] fragments = query.split("'");
        for (int i = 0; i < fragments.length; i++) {
            if (i % 2 != 0) {
                tokens.add("'" + fragments[i] + "'");
            } else {
                String[] nextBatchOfTokens = tokenize(fragments[i]);
                tokens.addAll(Arrays.asList(nextBatchOfTokens));
            }
        }
        this.currentToken = 0;
    }

    private String[] tokenize(String input) {
        for (String specialCharacter : specialCharacters) {
            input = input.replace(specialCharacter, " " + specialCharacter + " ");
        }
        while (input.contains("  ")) {
            input = input.replaceAll("  ", " ");
        }
        input = input.trim();
        return input.split(" ");
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
