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
    public void testInappropriateComparison() {
        sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "name, mark, pass"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Steve', 65, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Dave', 55, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Bob', 35, FALSE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Clive', 20, FALSE"));

        String response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name >= 50"));
        String errorsShouldNotBeReturned = "Errors should NOT be returned";
        String okTag = "[OK]";
        assertTrue(response.contains(okTag), errorsShouldNotBeReturned);
        String shouldReturnAnEmptyTable = "Should return an empty table";
        String steve = "Steve";
        assertFalse(response.contains(steve), shouldReturnAnEmptyTable);
        String dave = "Dave";
        assertFalse(response.contains(dave), shouldReturnAnEmptyTable);
        String bob = "Bob";
        assertFalse(response.contains(bob), shouldReturnAnEmptyTable);
        String clive = "Clive";
        assertFalse(response.contains(clive), shouldReturnAnEmptyTable);

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name LIKE 50"));
        assertTrue(response.contains(okTag), errorsShouldNotBeReturned);
        assertTrue(response.contains(okTag), errorsShouldNotBeReturned);
        assertFalse(response.contains(steve), shouldReturnAnEmptyTable);
        assertFalse(response.contains(dave), shouldReturnAnEmptyTable);
        assertFalse(response.contains(bob), shouldReturnAnEmptyTable);
        assertFalse(response.contains(clive), shouldReturnAnEmptyTable);

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "mark == TRUE"));
        assertTrue(response.contains(okTag), errorsShouldNotBeReturned);
        assertTrue(response.contains(okTag), errorsShouldNotBeReturned);
        assertTrue(response.contains(okTag), errorsShouldNotBeReturned);
        assertFalse(response.contains(steve), shouldReturnAnEmptyTable);
        assertFalse(response.contains(dave), shouldReturnAnEmptyTable);
        assertFalse(response.contains(bob), shouldReturnAnEmptyTable);
        assertFalse(response.contains(clive), shouldReturnAnEmptyTable);

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "pass > FALSE"));
        assertTrue(response.contains(okTag), errorsShouldNotBeReturned);
        assertTrue(response.contains(okTag), errorsShouldNotBeReturned);
        assertTrue(response.contains(okTag), errorsShouldNotBeReturned);
        assertFalse(response.contains(steve), shouldReturnAnEmptyTable);
        assertFalse(response.contains(dave), shouldReturnAnEmptyTable);
        assertFalse(response.contains(bob), shouldReturnAnEmptyTable);
        assertFalse(response.contains(clive), shouldReturnAnEmptyTable);

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name > NULL"));
        assertTrue(response.contains(okTag), errorsShouldNotBeReturned);
        assertTrue(response.contains(okTag), errorsShouldNotBeReturned);
        assertTrue(response.contains(okTag), errorsShouldNotBeReturned);
        assertFalse(response.contains(steve), shouldReturnAnEmptyTable);
        assertFalse(response.contains(dave), shouldReturnAnEmptyTable);
        assertFalse(response.contains(bob), shouldReturnAnEmptyTable);
        assertFalse(response.contains(clive), shouldReturnAnEmptyTable);

        sendCommandToServer(syntaxConstructor.dropDatabaseCommand(randomDatabaseName));
    }
}
