package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class AlterTableTests {
    /*
    <Alter> ::=  "ALTER TABLE " [TableName] " " [AlterationType] " " [AttributeName]
    changes the structure (columns) of an existing table
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
        randomTableName = syntaxConstructor.randomNameGenerator();
        sendCommandToServer("CREATE DATABASE " + randomDatabaseName + ";");
        sendCommandToServer("USE " + randomDatabaseName + ";");
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            return server.handleCommand(command); },
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testAlter() {
        randomTableName = syntaxConstructor.randomNameGenerator();
        sendCommandToServer("CREATE TABLE " + randomTableName + ";");

        String response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "ADD", "id"));
        assertTrue(response.contains("[ERROR]"), "ID cannot be added manually");
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "DROP", "pass"));
        assertTrue(response.contains("[ERROR]"),
                "Cannot drop a non-existing column in an empty table");

        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "ADD", "name"));
        assertTrue(response.contains("[OK]"),
                "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "DROP", "name"));
        assertTrue(response.contains("[OK]"),
                "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "ADD", "name"));
        assertTrue(response.contains("[OK]"),
                "A valid query was made, however an [OK] tag was not returned");

        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "ADD", "mark"));
        assertTrue(response.contains("[OK]"),
                "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "DROP", "mark"));
        assertTrue(response.contains("[OK]"),
                "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "ADD", "mark"));
        assertTrue(response.contains("[OK]"),
                "A valid query was made, however an [OK] tag was not returned");

        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "ADD", "pass"));
        assertTrue(response.contains("[OK]"),
                "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "DROP", "pass"));
        assertTrue(response.contains("[OK]"),
                "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "ADD", "pass"));
        assertTrue(response.contains("[OK]"),
                "A valid query was made, however an [OK] tag was not returned");

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "id"),
                "ID column should be added automatically");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "name"),
                "name column should have been added");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "mark"),
                "name column should have been added");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "pass"),
                "pass column should have been added");

        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "DROP", "name"));
        assertTrue(response.contains("[OK]"),
                "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "name"),
                "name column should have been deleted");

        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "DROP", "mark"));
        assertTrue(response.contains("[OK]"),
                "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "mark"),
                "mark column should have been deleted");

        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "DROP", "pass"));
        assertTrue(response.contains("[OK]"),
                "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "pass"),
                "pass column should have been deleted");

        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "DROP", "id"));
        assertTrue(response.contains("[ERROR]"), "Cannot drop id column");

        sendCommandToServer("DROP DATABASE " + randomDatabaseName);
    }

}
