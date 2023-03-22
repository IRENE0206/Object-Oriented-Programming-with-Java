package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateTableTests {
    /*
    the id of a record should NOT change at any time during the operation of the system
    <Update> ::=  "UPDATE " [TableName] " SET " <NameValueList> " WHERE " <Condition>

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
    public void testUpdateRecords() {
        String response = sendCommandToServer(
                syntaxConstructor.updateCommand(
                        randomTableName, "mark = 50", "name == 'Bob'"));
        assertTrue(response.contains("[ERROR]"), "Cannot update a non-existing table");
        sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "name, mark, pass"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Steve', 65, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Dave', 55, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Bob', 35, FALSE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Clive', 20, FALSE"));

        response = sendCommandToServer(
                syntaxConstructor.updateCommand(
                        randomTableName, "mark = 50, pass= TRUE", "name == 'Bob'"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.selectCommand("mark", randomTableName, "name == 'Bob'"));
        assertTrue(response.contains("50"), "Bob's mark should have been updated");
        response = sendCommandToServer(syntaxConstructor.selectCommand("pass", randomTableName, "name == 'Bob'"));
        assertTrue(response.contains("TRUE"), "Bob's pass result should have been updated");
        response = sendCommandToServer(
                syntaxConstructor.updateCommand(
                        randomTableName, "tutor = Steve", "name == 'Bob'"));
        assertTrue(response.contains("[ERROR]"), "Invalid NameValuePair");
        response = sendCommandToServer(
                syntaxConstructor.updateCommand(
                        randomTableName, "pass =False", "name == 'Anas'"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(
                syntaxConstructor.updateCommand(
                        randomTableName, "pass =False", "tutor == 'Anas'"));
        assertTrue(response.contains("[ERROR]"), "Invalid condition");

        response = sendCommandToServer(
                syntaxConstructor.updateCommand(
                        randomTableName, "id=3, mark=40", "name LIKE 'ive'"));
        assertTrue(response.contains("[ERROR]"), "Cannot change id manually");

        sendCommandToServer(syntaxConstructor.dropDatabaseCommand(randomDatabaseName));
    }
}
