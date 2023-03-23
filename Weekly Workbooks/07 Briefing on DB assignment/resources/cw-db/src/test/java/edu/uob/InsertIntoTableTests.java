package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class InsertIntoTableTests {
    /*
    adds a new record (row) to an existing table
    <Insert> ::=  "INSERT INTO " [TableName] " VALUES(" <ValueList> ")"

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
    public void testInsertIntoTable() {
        String response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Steve', 65, TRUE"));
        assertTrue(response.contains("[ERROR]"), "Cannot insert into a non-existing table");
        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Steve', 65, TRUE"));
        assertTrue(response.contains("ERROR"), "Cannot insert into a table without column names defined");

        sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "name, mark, pass"));

        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Steve', 65, TRUE"));
        String oKTagMessage = "A valid query was made, however an [OK] tag was not returned";
        String okTag = "[OK]";
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Dave', 55, TRUE"));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Bob', 35, FALSE"));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Clive', 20, FALSE"));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        String shouldHaveBeenInserted = "Should have been inserted into table";
        assertTrue(response.contains("Steve"), shouldHaveBeenInserted);
        assertTrue(response.contains("Dave"), shouldHaveBeenInserted);
        assertTrue(response.contains("Bob"), shouldHaveBeenInserted);
        assertTrue(response.contains("Clive"), shouldHaveBeenInserted);

        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "5, 'Kim', 60, TRUE"));
        String wrongNumber = "Cannot insert into table. Wrong number of values";
        assertTrue(response.contains("ERROR"), wrongNumber);
        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "Kim', TRUE"));
        assertTrue(response.contains("ERROR"), wrongNumber);

        sendCommandToServer((syntaxConstructor.dropDatabaseCommand(randomDatabaseName)));
    }
}
