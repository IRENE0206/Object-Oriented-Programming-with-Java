package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static edu.uob.ResponseExamineHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class BasicBuiltinCommandsTests {

    private GameServer server;

    @BeforeEach
    public void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> server.handleCommand(command),
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testBasicBuiltinCommands() {
        String response = sendCommandToServer("simon: look");
        String[] keywords0 = {"log cabin", "Magic potion", "Wooden trapdoor", "forest"};
        testResponseContains(response.toLowerCase(), keywords0);

        sendCommandToServer("simon: forest goto");
        response = sendCommandToServer("simon: look");
        testResponseContains(response.toLowerCase(), keywords0);
        // a duplication of the built-in command and as such doesn't make sense
        response = sendCommandToServer("simon: look look");
        testResponseNotContains(response.toLowerCase(), keywords0);
        // look potion has extraneous entities ( potion) so should be rejected
        response = sendCommandToServer("simon: look potion");
        testResponseNotContains(response.toLowerCase(), keywords0);
        sendCommandToServer("simon: goto");
        response = sendCommandToServer("simon: look");
        testResponseContains(response.toLowerCase(), keywords0);

        sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: look");
        String[] keywords1 = {"dark forest", "Brass key", "big tree", "cabin"};
        testResponseContains(response, keywords1);
        String[] keywords2 = {"key"};
        response = sendCommandToServer("simon: inv");
        testResponseNotContains(response, keywords2);
        sendCommandToServer("simon: key get");
        response = sendCommandToServer("simon: look");
        testResponseContains(response, keywords1);
        response = sendCommandToServer("simon: inventory");
        testResponseNotContains(response, keywords2);
        // forest is a known entity, it breaks the command
        // (since only one entity is required for a "get" command)
        sendCommandToServer("simon: get key from forest");
        response = sendCommandToServer("simon: look");
        testResponseContains(response, keywords1);
        response = sendCommandToServer("simon: inventory");
        testResponseNotContains(response, keywords2);
        // a composite command referencing two different entities, so just return the warning
        response = sendCommandToServer("simon: get key and potion");
        response = sendCommandToServer("simon: look");
        testResponseContains(response, keywords1);
        response = sendCommandToServer("simon: inventory");
        testResponseNotContains(response, keywords2);

        sendCommandToServer("simon: get key");
        response = sendCommandToServer("simon: look");
        testResponseNotContains(response, keywords2);
        response = sendCommandToServer("simon: inv");
        testResponseContains(response, keywords2);

        sendCommandToServer("simon: drop brass key");
        response = sendCommandToServer("simon: inventory");
        testResponseNotContains(response, keywords2);
        response = sendCommandToServer("simon: look");
        testResponseContains(response, keywords2);

        sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: look");
        testResponseContains(response, keywords0);
        sendCommandToServer("simon: get magic potion");
        String[] keywords3 = {"potion"};
        response = sendCommandToServer("simon: inv");
        testResponseContains(response, keywords3);
        response = sendCommandToServer("simon: invs");
        testResponseNotContains(response, keywords3);
        //  valid decorated commands
        response = sendCommandToServer("simon: please look");
        testResponseNotContains(response, keywords3);
        sendCommandToServer("simon: drop potion");
        response = sendCommandToServer("simon: inventory");
        testResponseNotContains(response, keywords3);
        response = sendCommandToServer("simon: please look");
        testResponseContains(response, keywords3);
    }

}
