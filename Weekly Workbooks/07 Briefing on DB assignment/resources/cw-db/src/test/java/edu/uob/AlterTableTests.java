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
    private SyntaxConstructor syntaxConstructor;
    private String randomDatabaseName;
    private String randomTableName;

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
        String oKTagMessage = "A valid query was made, however an [OK] tag was not returned";
        randomTableName = syntaxConstructor.randomNameGenerator();
        sendCommandToServer("CREATE TABLE " + randomTableName + ";");

        String add = "ADD";
        String response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, add, "id"));
        String errorTag = "[ERROR]";
        assertTrue(response.contains(errorTag), "ID cannot be added manually");
        String drop = "DROP";
        String pass = "pass";
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, drop, pass));
        assertTrue(response.contains(errorTag),
                "Cannot drop a non-existing column in an empty table");

        String name = "name";
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, add, name));
        String okTag = "[OK]";
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, drop, name));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, add, name));
        assertTrue(response.contains(okTag), oKTagMessage);

        String mark = "mark";
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, add, mark));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, drop, mark));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, add, mark));
        assertTrue(response.contains(okTag), oKTagMessage);

        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, add, pass));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, drop, pass));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, add, pass));
        assertTrue(response.contains(okTag), oKTagMessage);

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "id"),
                "ID column should be added automatically");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, name),
                "name column should have been added");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, mark),
                "name column should have been added");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, pass),
                "pass column should have been added");

        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, drop, name));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, name),
                "name column should have been deleted");

        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, drop, mark));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, mark),
                "mark column should have been deleted");

        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, drop, pass));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, pass),
                "pass column should have been deleted");

        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, drop, "id"));
        assertTrue(response.contains(errorTag), "Cannot drop id column");

        sendCommandToServer("DROP DATABASE " + randomDatabaseName);
    }

}
