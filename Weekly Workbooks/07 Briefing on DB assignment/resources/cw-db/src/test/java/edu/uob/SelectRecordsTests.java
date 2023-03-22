package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class SelectRecordsTests {
    /*
    <Select> ::=  "SELECT " <WildAttribList> " FROM " [TableName] |
    "SELECT " <WildAttribList> " FROM " [TableName] " WHERE " <Condition>
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
    public void testSelect() {
        String response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertTrue(response.contains("[ERROR]"), "Cannot select a non-existing table");

        sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "name, mark, pass"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Steve', 65, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Dave', 55, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Bob', 35, FALSE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Clive', 20, FALSE"));

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertTrue(response.contains("Steve"), "Failed to get the right selected result");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertTrue(response.contains("Dave"), "Failed to get the right selected result");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertTrue(response.contains("Bob"), "Failed to get the right selected result");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertTrue(response.contains("Clive"), "Failed to get the right selected result");

        response = sendCommandToServer(syntaxConstructor.selectCommand("name, pass", randomTableName));
        assertTrue(response.contains("name"), "Failed to get the right selected result");
        assertTrue(response.contains("pass"), "Failed to get the right selected result");
        assertFalse(response.contains("id"), "Failed to get the right selected result");
        assertFalse(response.contains("mark"), "Failed to get the right selected result");
        assertTrue(response.contains("Steve"), "Failed to get the right selected result");
        assertTrue(response.contains("Dave"), "Failed to get the right selected result");
        assertTrue(response.contains("Bob"), "Failed to get the right selected result");
        assertTrue(response.contains("Clive"), "Failed to get the right selected result");

        response = sendCommandToServer(syntaxConstructor.selectCommand("name, mark", randomTableName, "pass != True"));
        assertTrue(response.contains("name"), "Failed to get the right selected result");
        assertTrue(response.contains("mark"), "Failed to get the right selected result");
        assertFalse(response.contains("id"), "Failed to get the right selected result");
        assertFalse(response.contains("pass"), "Failed to get the right selected result");
        assertFalse(response.contains("Steve"), "Failed to get the right selected result");
        assertFalse(response.contains("Dave"), "Failed to get the right selected result");
        assertTrue(response.contains("Bob"), "Failed to get the right selected result");
        assertTrue(response.contains("Clive"), "Failed to get the right selected result");

        response = sendCommandToServer(syntaxConstructor.selectCommand("name", randomTableName,
                "pass == True AND name Like 've' AND mark > 60"));
        assertTrue(response.contains("name"), "Failed to get the right selected result");
        assertFalse(response.contains("mark"), "Failed to get the right selected result");
        assertFalse(response.contains("id"), "Failed to get the right selected result");
        assertFalse(response.contains("pass"), "Failed to get the right selected result");
        assertTrue(response.contains("Steve"), "Failed to get the right selected result");
        assertFalse(response.contains("Dave"), "Failed to get the right selected result");
        assertFalse(response.contains("Bob"), "Failed to get the right selected result");
        assertFalse(response.contains("Clive"), "Failed to get the right selected result");

        sendCommandToServer(syntaxConstructor.dropDatabaseCommand(randomDatabaseName));
    }
}
