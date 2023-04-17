package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseDatabaseTests {

    private DBServer server;
    private SyntaxConstructor syntaxConstructor;
    private String randomDatabaseName1;
    private String randomDatabaseName2;
    private String randomTableName;

    @BeforeEach
    public void setup() {
        server = new DBServer();
        syntaxConstructor = new SyntaxConstructor();
        randomDatabaseName1 = syntaxConstructor.randomNameGenerator();
        randomDatabaseName2 = syntaxConstructor.randomNameGenerator();
        randomTableName = syntaxConstructor.randomNameGenerator();
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            return server.handleCommand(command); },
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testUseDatabase() {
        String oKTagMessage = "A valid query was made, however an [OK] tag was not returned";
        String response = sendCommandToServer(syntaxConstructor.useCommand(randomDatabaseName1));
        String nonExistingDatabase = "Cannot use a non-existing database";
        assertTrue(response.contains("[ERROR]"), nonExistingDatabase);
        sendCommandToServer(syntaxConstructor.createDataBaseCommand(randomDatabaseName1));
        response = sendCommandToServer(syntaxConstructor.useCommand(randomDatabaseName1));
        assertTrue(response.contains("[OK]"), oKTagMessage);

        response = sendCommandToServer(syntaxConstructor.useCommand(randomDatabaseName2));
        assertTrue(response.contains("[ERROR]"), nonExistingDatabase);
        sendCommandToServer(syntaxConstructor.createDataBaseCommand(randomDatabaseName2));
        response = sendCommandToServer(syntaxConstructor.useCommand(randomDatabaseName2));
        assertTrue(response.contains("[OK]"), oKTagMessage);

        sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName));
        response = sendCommandToServer(syntaxConstructor.useCommand(randomDatabaseName1));
        assertTrue(response.contains("[OK]"), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.dropTableCommand(randomTableName));
        assertTrue(response.contains("[ERROR]"), "Created table is not in the current database in use");

        sendCommandToServer(syntaxConstructor.dropDatabaseCommand(randomDatabaseName1));
        sendCommandToServer(syntaxConstructor.dropDatabaseCommand(randomDatabaseName2));
    }
}
