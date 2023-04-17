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
    private SyntaxConstructor syntaxConstructor;
    private String randomDatabaseName;
    private String randomTableName;

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
        sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName, "name, mark, pass"));
        String drop = "DROP";
        String response =
                sendCommandToServer(
                        syntaxConstructor.alterCommand(randomTableName, drop,
                                randomTableName + " . name"));
        String cannotHaveAdditionalSpaces = "[AttributeName] cannot have additional whitespace";
        String errorTag = "[ERROR]";
        assertTrue(response.contains(errorTag), cannotHaveAdditionalSpaces);
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, drop,
                randomTableName + ". mark"));
        assertTrue(response.contains(errorTag), cannotHaveAdditionalSpaces);
        response = sendCommandToServer(syntaxConstructor.alterCommand(randomTableName, drop,
                randomTableName + " .pass"));
        assertTrue(response.contains(errorTag), cannotHaveAdditionalSpaces);

        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "' Dave', 55, TRUE"));
        String oKTagMessage = "A valid query was made, however an [OK] tag was not returned";
        String okTag = "[OK]";
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name == 'Dave'"));
        String shouldBePreserved = "White space in single quotes should be preserved";
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "Dave"), shouldBePreserved);
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name == ' Dave'"));
        assertTrue(response.contains(okTag), oKTagMessage);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Dave"), shouldBePreserved);

        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Bob ', 35, FALSE"));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name == 'Bob'"));
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "Bob"), shouldBePreserved);
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name == 'Bob '"));
        assertTrue(response.contains(okTag), oKTagMessage);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Bob"), shouldBePreserved);

        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Clive', + 20, FALSE"));
        assertTrue(response.contains(errorTag), "[IntegerLiteral]  cannot have additional whitespace");
        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Clive', +20, FALSE"));
        assertTrue(response.contains(okTag), oKTagMessage);

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name ! = 'Clive'"));
        assertTrue(response.contains(errorTag), "[Comparator]  cannot have additional whitespace");
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name = = 'Clive'"));
        assertTrue(response.contains(errorTag), "[Comparator]  cannot have additional whitespace");

        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name !='Clive'"));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name!= 'Clive'"));
        assertTrue(response.contains(okTag), oKTagMessage);
        response = sendCommandToServer(syntaxConstructor.selectCommand("*", randomTableName, "name!='Clive'"));
        assertTrue(response.contains(okTag), oKTagMessage);

        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Irene', + 90.00, TRUE"));
        assertTrue(response.contains(errorTag), "[FloatLiteral]  cannot have additional whitespace");
        response = sendCommandToServer(syntaxConstructor.insertCommand(randomTableName, "'Irene', +90.00, TRUE"));
        assertTrue(response.contains(okTag), oKTagMessage);

        sendCommandToServer("DROP DATABASE " + randomDatabaseName);
    }
}
