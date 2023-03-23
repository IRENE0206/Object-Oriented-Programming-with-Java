package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateAndDropTableTests {
    /*
    <CreateTable> ::=  "CREATE TABLE " [TableName] | "CREATE TABLE " [TableName] "(" <AttributeList> ")"
    "DROP TABLE " [TableName]
     */

    private DBServer server;
    private SyntaxConstructor syntaxConstructor;
    private String randomDatabaseName;

    @BeforeEach
    public void setup() {
        server = new DBServer();
        syntaxConstructor = new SyntaxConstructor();
        randomDatabaseName = syntaxConstructor.randomNameGenerator();
        sendCommandToServer(syntaxConstructor.createDataBaseCommand(randomDatabaseName));
        sendCommandToServer(syntaxConstructor.useCommand(randomDatabaseName));
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            return server.handleCommand(command); },
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testCreateAndDropDatabase() {
        String randomTableName = syntaxConstructor.randomNameGenerator();

        String response = sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName));
        String oKTagMessage = "A valid query was made, however an [OK] tag was not returned";
        String okTag = "[OK]";
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName));
        String errorTag = "[ERROR]";
        assertTrue(response.contains(errorTag), "Cannot create a table that already exists");
        response = sendCommandToServer(syntaxConstructor.dropTableCommand(randomTableName));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.dropTableCommand(randomTableName));
        assertTrue(response.contains(errorTag), "Cannot drop a table that has been dropped");

        response = sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "name, mark, pass"));
        assertTrue(response.contains(errorTag), "Cannot create a table that already exists");
        response = sendCommandToServer(syntaxConstructor.dropTableCommand(randomTableName));
        assertTrue(response.contains(okTag), oKTagMessage);

        response = sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "id, name, mark, pass"));
        assertTrue(response.contains(errorTag), "ID column should not be manually inserted");

        response = sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "name, mark, pass"));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "id"),
                "ID is not automatically added when creating table");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "name"),
                "Column names are not added when creating table");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "mark"),
                "Column names are not added when creating table");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "pass"),
                "Column names are not added when creating table");
        response = sendCommandToServer(syntaxConstructor.dropTableCommand(randomTableName));
        assertTrue(response.contains(okTag), oKTagMessage);

        sendCommandToServer(syntaxConstructor.dropDatabaseCommand(randomDatabaseName));
    }
}
