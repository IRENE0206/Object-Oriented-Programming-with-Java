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
    private SyntaxConstructor syntaxConstructor;
    private String randomDatabaseName;
    private String randomTableName;

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
        String failMessage = "Failed to get the right selected result";
        sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "name, mark, pass"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Steve', 65, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Dave', 55, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Bob', 35, FALSE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Clive', 20, FALSE"));

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        String steve = "Steve";
        assertTrue(response.contains(steve), failMessage);
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        String dave = "Dave";
        assertTrue(response.contains(dave), failMessage);
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        String bob = "Bob";
        assertTrue(response.contains(bob), failMessage);
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        String clive = "Clive";
        assertTrue(response.contains(clive), failMessage);

        response = sendCommandToServer(syntaxConstructor.selectCommand("name, pass", randomTableName));
        String name = "name";
        assertTrue(response.contains(name), failMessage);
        String pass = "pass";
        assertTrue(response.contains(pass), failMessage);
        String id = "id";
        assertFalse(response.contains(id), failMessage);
        String mark = "mark";
        assertFalse(response.contains(mark), failMessage);
        assertTrue(response.contains(steve), failMessage);
        assertTrue(response.contains(dave), failMessage);
        assertTrue(response.contains(bob), failMessage);
        assertTrue(response.contains(clive), failMessage);

        response = sendCommandToServer(syntaxConstructor.selectCommand("name, mark", randomTableName, "pass != True"));
        assertTrue(response.contains(name), failMessage);
        assertTrue(response.contains(mark), failMessage);
        assertFalse(response.contains(id), failMessage);
        assertFalse(response.contains(pass), failMessage);
        assertFalse(response.contains(steve), failMessage);
        assertFalse(response.contains(dave), failMessage);
        assertTrue(response.contains(bob), failMessage);
        assertTrue(response.contains(clive), failMessage);

        response = sendCommandToServer(syntaxConstructor.selectCommand(name, randomTableName,
                "pass == True AND name Like 've' AND mark > 60"));
        assertTrue(response.contains(name), failMessage);
        assertFalse(response.contains(mark), failMessage);
        assertFalse(response.contains(id), failMessage);
        assertFalse(response.contains(pass), failMessage);
        assertTrue(response.contains(steve), failMessage);
        assertFalse(response.contains(dave), failMessage);
        assertFalse(response.contains(bob), failMessage);
        assertFalse(response.contains(clive), failMessage);

        sendCommandToServer(syntaxConstructor.dropDatabaseCommand(randomDatabaseName));
    }
}
