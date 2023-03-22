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
    public void testInsertIntoTable() {
        String response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Steve', 65, TRUE"));
        assertTrue(response.contains("[ERROR]"), "Cannot insert into a non-existing table");
        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Steve', 65, TRUE"));
        assertTrue(response.contains("ERROR"), "Cannot insert into a table without column names defined");

        sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "name, mark, pass"));

        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Steve', 65, TRUE"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Dave', 55, TRUE"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Bob', 35, FALSE"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Clive', 20, FALSE"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertTrue(response.contains("Steve"), "Should have been inserted into table");
        assertTrue(response.contains("Dave"), "Should have been inserted into table");
        assertTrue(response.contains("Bob"), "Should have been inserted into table");
        assertTrue(response.contains("Clive"), "Should have been inserted into table");

        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "5, 'Kim', 60, TRUE"));
        assertTrue(response.contains("ERROR"), "Cannot insert into table. Wrong number of values");
        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "Kim', TRUE"));
        assertTrue(response.contains("ERROR"), "Cannot insert into table. Wrong number of values");

        sendCommandToServer((syntaxConstructor.dropDatabaseCommand(randomDatabaseName)));
    }
}
