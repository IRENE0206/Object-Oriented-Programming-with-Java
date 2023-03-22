package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReservedKeywordsTests {
    String[] reservedKeywords = {"USE", "CREATE", "DATABASE", "TABLE", "DROP", "ALTER", "INSERT", "INTO", "VALUES",
            "SELECT", "FROM", "UPDATE", "SET", "WHERE", "DELETE", "JOIN", "ON", "AND", "ADD", "TRUE", "FALSE", "OR", "LIKE"};

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
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testReservedKeywords() {
        for (String keyword : reservedKeywords) {
            String response = sendCommandToServer(syntaxConstructor.createDataBaseCommand(keyword));
            assertTrue(response.contains("[ERROR]"), "should not allow reserved words to be used as database/table/attribute names");
            sendCommandToServer(syntaxConstructor.useCommand(randomDatabaseName));
            response = sendCommandToServer(syntaxConstructor.createTableCommand(keyword));
            assertTrue(response.contains("[ERROR]"), "should not allow reserved words to be used as database/table/attribute names");
            response = sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, keyword));
            assertTrue(response.contains("[ERROR]"), "should not allow reserved words to be used as database/table/attribute names");
            sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName));
            response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, "ADD", keyword));
            assertTrue(response.contains("[ERROR]"), "should not allow reserved words to be used as database/table/attribute names");
            sendCommandToServer(syntaxConstructor.dropTableCommand(randomTableName));
        }
        sendCommandToServer(syntaxConstructor.dropDatabaseCommand(randomDatabaseName));
    }
}
