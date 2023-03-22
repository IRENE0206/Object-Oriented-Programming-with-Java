package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class InvalidAdditionalWhitespaceTests {
    /*
    Symbols with angle brackets <name> denote rules which MAY contain arbitrary additional whitespace
    whereas [name] indicates a rule that cannot contain additional whitespace
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
        randomTableName = syntaxConstructor.randomNameGenerator();
        sendCommandToServer("CREATE DATABASE " + randomDatabaseName + ";");
        sendCommandToServer("USE " + randomDatabaseName + ";");
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            return server.handleCommand(command); },
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testInvalidAdditionalWhiteSpace() {
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        String response = sendCommandToServer(syntaxConstructor.alterCommand("marks", "DROP", "marks . name"));
        assertTrue(response.contains("[ERROR]"), "[AttributeName] cannot have additional whitespace");
        response = sendCommandToServer(syntaxConstructor.alterCommand("marks", "DROP", "marks. mark"));
        assertTrue(response.contains("[ERROR]"), "[AttributeName] cannot have additional whitespace");
        response = sendCommandToServer(syntaxConstructor.alterCommand("marks", "DROP", "marks .pass"));
        assertTrue(response.contains("[ERROR]"), "[AttributeName] cannot have additional whitespace");

        response = sendCommandToServer(syntaxConstructor.insertCommand("marks", "' Dave', 55, TRUE"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", "marks", "name == 'Dave'"));
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "Dave"),
                "White space in single quotes should be preserved");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", "marks", "name == ' Dave'"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Dave"),
                "White space in single quotes should be preserved");

        response = sendCommandToServer(syntaxConstructor.insertCommand("marks", "'Bob ', 35, FALSE"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", "marks", "name == 'Bob'"));
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "Bob"),
                "White space in single quotes should be preserved");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*",
                "marks", "name == 'Bob '"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Bob"),
                "White space in single quotes should be preserved");

        response = sendCommandToServer(syntaxConstructor.insertCommand("marks", "'Clive', + 20, FALSE"));
        assertTrue(response.contains("[ERROR]"), "[IntegerLiteral]  cannot have additional whitespace");
        response = sendCommandToServer(syntaxConstructor.insertCommand("marks", "'Clive', +20, FALSE"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", "marks", "name ! = 'Clive'"));
        assertTrue(response.contains("[ERROR]"), "[Comparator]  cannot have additional whitespace");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", "marks", "name = = 'Clive'"));
        assertTrue(response.contains("[ERROR]"), "[Comparator]  cannot have additional whitespace");

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", "marks", "name !='Clive'"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", "marks", "name!= 'Clive'"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", "marks", "name!='Clive'"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");

        response = sendCommandToServer(syntaxConstructor.insertCommand("marks", "'Irene', + 90.00, TRUE"));
        assertTrue(response.contains("[ERROR]"), "[FloatLiteral]  cannot have additional whitespace");
        response = sendCommandToServer(syntaxConstructor.insertCommand("marks", "'Irene', +90.00, TRUE"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");

        sendCommandToServer("DROP DATABASE " + randomDatabaseName);
    }
}
