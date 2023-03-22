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
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testCreateAndDropDatabase() {
        randomTableName = syntaxConstructor.randomNameGenerator();

        String response = sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName));
        assertTrue(response.contains("[ERROR]"), "Cannot create a table that already exists");
        response = sendCommandToServer(syntaxConstructor.dropTableCommand(randomTableName));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.dropTableCommand(randomTableName));
        assertTrue(response.contains("[ERROR]"), "Cannot drop a table that has been dropped");

        response = sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "name, mark, pass"));
        assertTrue(response.contains("[ERROR]"), "Cannot create a table that already exists");
        response = sendCommandToServer(syntaxConstructor.dropTableCommand(randomTableName));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");

        response = sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "id, name, mark, pass"));
        assertTrue(response.contains("[ERROR]"), "ID column should not be manually inserted");

        response = sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "name, mark, pass"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "id"), "ID is not automatically added when creating table");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "name"), "Column names are not added when creating table");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "mark"), "Column names are not added when creating table");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "pass"), "Column names are not added when creating table");
        response = sendCommandToServer(syntaxConstructor.dropTableCommand(randomTableName));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");

        sendCommandToServer(syntaxConstructor.dropDatabaseCommand(randomDatabaseName));
    }
}
