package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class InappropriateComparisonTests {
    /*
    In situations where no appropriate comparison is possible (e.g. TRUE > FALSE)
     just return no data in the results table.
    Errors should NOT be returned
     */

    private DBServer server;
    SyntaxConstructor syntaxConstructor;
    String randomDatabaseName;
    String randomTableName;

    @BeforeEach
    public void setup() {
        server = new DBServer();
        syntaxConstructor = new SyntaxConstructor();
        randomDatabaseName = syntaxConstructor.randomNameGenerator();
        sendCommandToServer(syntaxConstructor.createDataBaseCommand(randomDatabaseName));
        sendCommandToServer(syntaxConstructor.useCommand(randomDatabaseName));
        randomTableName = syntaxConstructor.randomNameGenerator();
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            return server.handleCommand(command); },
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testInappropriateComparison() {
        sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "name, mark, pass"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Steve', 65, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Dave', 55, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Bob', 35, FALSE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Clive', 20, FALSE"));

        String response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name >= 50"));
        assertTrue(response.contains("[OK]"), "Errors should NOT be returned");
        assertFalse(response.contains("Steve"), "Should return an empty table");
        assertFalse(response.contains("Dave"), "Should return an empty table");
        assertFalse(response.contains("Bob"), "Should return an empty table");
        assertFalse(response.contains("Clive"), "Should return an empty table");

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name LIKE 50"));
        assertTrue(response.contains("[OK]"), "Errors should NOT be returned");
        assertTrue(response.contains("[OK]"), "Errors should NOT be returned");
        assertFalse(response.contains("Steve"), "Should return an empty table");
        assertFalse(response.contains("Dave"), "Should return an empty table");
        assertFalse(response.contains("Bob"), "Should return an empty table");
        assertFalse(response.contains("Clive"), "Should return an empty table");

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "mark == TRUE"));
        assertTrue(response.contains("[OK]"), "Errors should NOT be returned");
        assertTrue(response.contains("[OK]"), "Errors should NOT be returned");
        assertTrue(response.contains("[OK]"), "Errors should NOT be returned");
        assertFalse(response.contains("Steve"), "Should return an empty table");
        assertFalse(response.contains("Dave"), "Should return an empty table");
        assertFalse(response.contains("Bob"), "Should return an empty table");
        assertFalse(response.contains("Clive"), "Should return an empty table");

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "pass > FALSE"));
        assertTrue(response.contains("[OK]"), "Errors should NOT be returned");
        assertTrue(response.contains("[OK]"), "Errors should NOT be returned");
        assertTrue(response.contains("[OK]"), "Errors should NOT be returned");
        assertFalse(response.contains("Steve"), "Should return an empty table");
        assertFalse(response.contains("Dave"), "Should return an empty table");
        assertFalse(response.contains("Bob"), "Should return an empty table");
        assertFalse(response.contains("Clive"), "Should return an empty table");

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name > NULL"));
        assertTrue(response.contains("[OK]"), "Errors should NOT be returned");
        assertTrue(response.contains("[OK]"), "Errors should NOT be returned");
        assertTrue(response.contains("[OK]"), "Errors should NOT be returned");
        assertFalse(response.contains("Steve"), "Should return an empty table");
        assertFalse(response.contains("Dave"), "Should return an empty table");
        assertFalse(response.contains("Bob"), "Should return an empty table");
        assertFalse(response.contains("Clive"), "Should return an empty table");

        sendCommandToServer(syntaxConstructor.dropDatabaseCommand(randomDatabaseName));
    }
}
