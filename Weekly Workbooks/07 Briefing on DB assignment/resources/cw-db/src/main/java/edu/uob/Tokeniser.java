package edu.uob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tokeniser {
    private final String[] specialCharacters = {"(",")",",",";"};
    private List<String> tokens = new ArrayList<String>();
    private int currentToken;

    public Tokeniser(String query) {
        this.setup(query);
    }

    private void setup(String query)
    {
        // Remove any whitespace at the beginning and end of the query
        query = query.trim();
        // Split the query on single quotes (to separate out query characters from string literals)
        String[] fragments = query.split("'");
        for (int i = 0; i < fragments.length; i++) {
            // Every odd fragment is a string literal, so just append it without any alterations
            if (i % 2 != 0) {
                tokens.add("'" + fragments[i] + "'");
            } else {
                // Tokenize the fragments into an array of strings
                String[] nextBatchOfTokens = tokenize(fragments[i]);
                // Then add these to the "result" array list (needs a bit of conversion)
                tokens.addAll(Arrays.asList(nextBatchOfTokens));
            }
        }
        this.currentToken = 0;
        // Finally, loop through the result array list, printing out each token a line at a time
        /* for (String token : tokens) {
            System.out.println(token);
        } */
    }

    private String[] tokenize(String input)
    {
        // Add in some extra padding spaces around the "special characters"
        // so we can be sure that they are separated by AT LEAST one space (possibly more)
        for (String specialCharacter : specialCharacters) {
            input = input.replace(specialCharacter, " " + specialCharacter + " ");
        }
        // Remove all double spaces (the previous replacements may have added some)
        // This is "blind" replacement - replacing if they exist, doing nothing if they don't
        while (input.contains("  ")) input = input.replaceAll("  ", " ");
        // Again, remove any whitespace from the beginning and end that might have been introduced
        input = input.trim();
        // Finally split on the space char (since there will now ALWAYS be a space between tokens)
        return input.split(" ");
    }

    public void nextToken() {
        if (hasNextToken()) {
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
