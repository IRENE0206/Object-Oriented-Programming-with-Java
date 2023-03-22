package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.plaf.synth.SynthScrollBarUI;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteRecordsTests {
    /*
    <Delete> ::=  "DELETE FROM " [TableName] " WHERE " [Condition]
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
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testDeleteTableRecordsTest() {
        sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "name, mark, pass"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Steve', 65, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Dave', 55, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Bob', 35, FALSE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Clive', 20, FALSE"));

        String response = sendCommandToServer((syntaxConstructor.deleteCommand(randomTableName, "name == 'Clive'")));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "Clive"), "Records that match the given condition should have been removed");

        response = sendCommandToServer((syntaxConstructor.deleteCommand(randomTableName, "mark < 50")));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName));
        assertTrue(response.contains("Steve"), "Records that are not deleted should persist");
        assertTrue(response.contains("Dave"), "Records that are not deleted should persist");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "Bob"), "Records that match the given condition should have been removed");

        response = sendCommandToServer((syntaxConstructor.deleteCommand(randomTableName, "tuition >= 30000")));
        assertTrue(response.contains("[ERROR]"), "The condition is invalid");

        sendCommandToServer((syntaxConstructor.dropTableCommand(randomTableName)));
        response = sendCommandToServer((syntaxConstructor.deleteCommand(randomTableName, "pass == FALSE")));
        assertTrue(response.contains("[ERROR]"), "Cannot delete from a table that has been dropped");
        sendCommandToServer((syntaxConstructor.dropDatabaseCommand(randomDatabaseName)));
    }
}
