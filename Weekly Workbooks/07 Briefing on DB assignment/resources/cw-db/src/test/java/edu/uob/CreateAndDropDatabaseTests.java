package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateAndDropDatabaseTests {
    /*
    <CreateDatabase> ::=  "CREATE DATABASE " [DatabaseName]
    <Drop>           ::=  "DROP DATABASE " [DatabaseName]
     */

    private DBServer server;
    SyntaxConstructor syntaxConstructor;
    String randomDatabaseName;

    @BeforeEach
    public void setup() {
        server = new DBServer();
        syntaxConstructor = new SyntaxConstructor();
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            return server.handleCommand(command); },
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testCreateAndDropDatabase() {
        randomDatabaseName = syntaxConstructor.randomNameGenerator();

        String response = sendCommandToServer(syntaxConstructor.createDataBaseCommand(randomDatabaseName));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.createDataBaseCommand(
                syntaxConstructor.arbitraryCaseGenerator(randomDatabaseName)));
        assertTrue(response.contains("[ERROR]"), "Cannot create a database that already exists");
        response = sendCommandToServer(syntaxConstructor.useCommand(randomDatabaseName));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");

        response = sendCommandToServer(syntaxConstructor.dropDatabaseCommand(randomDatabaseName));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer("DROP DATABASE " + syntaxConstructor.arbitraryCaseGenerator(randomDatabaseName));
        assertTrue(response.contains("[ERROR]"), "Cannot drop a non-existing database");
        response = sendCommandToServer(syntaxConstructor.useCommand(randomDatabaseName));
        assertTrue(response.contains("[ERROR]"), "Cannot use a database that has been dropped");
    }
}
