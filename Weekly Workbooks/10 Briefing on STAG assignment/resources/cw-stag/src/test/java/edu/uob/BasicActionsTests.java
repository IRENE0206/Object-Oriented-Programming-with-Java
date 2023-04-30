package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class BasicActionsTests {
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
    public void testRandomOrderBasicActions() {
        sendCommandToServer("simon: goto forest");
        String response = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(response.contains("tree"));
        assertFalse(response.contains("log"));
        response = sendCommandToServer("simon: axe chop tree");
        assertEquals("You cut down the tree with the axe", response);
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("tree"));
        assertTrue(response.contains("log"));
    }

    @Test
    public void testShortBasicActions() {
        sendCommandToServer("simon: goto forest");
        String response = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(response.contains("tree"));
        assertFalse(response.contains("log"));
        response = sendCommandToServer("simon: chop tree");
        assertEquals("You cut down the tree with the axe", response);
        response = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(response.contains("tree"));
        assertTrue(response.contains("log"));
    }

    // Composite Commands (commands involving more than one activity)
    // should NOT be supported
    // an incoming command cannot contain more than one action,
    // no matter the validity or perform-ability of the actions
    @Test
    public void testCompositeBasicActions() {
        sendCommandToServer("simon: goto forest");
        // Trigger keywords can't be used decorations
        String response = sendCommandToServer("simon: cut tree drink");
        assertNotEquals("You cut down the tree with the axe", response);
    }

    //  trigger phrases must appear exactly as written in the action file (no words in between)
    @Test
    public void testInvalidTriggerPhrase() {
        sendCommandToServer("simon: goto forest");
        String response = sendCommandToServer("simon: cut violently down tree");
        assertNotEquals("You cut down the tree with the axe", response);
        response = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(response.contains("tree"));
        assertFalse(response.contains("log"));
    }

    @Test
    public void testDuplicateTriggerPhrase() {
        sendCommandToServer("simon: goto forest");
        String response = sendCommandToServer("simon: chop tree with axe to cut it down");
        assertNotEquals("You cut down the tree with the axe", response);
        response = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(response.contains("tree"));
        assertTrue(response.contains("log"));
    }

    @Test
    public void testDuplicateSubjects() {
        sendCommandToServer("simon: goto forest");
        String response = sendCommandToServer("simon: cut tree and cut tree");
        assertNotEquals("You cut down the tree with the axe", response);
        response = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(response.contains("tree"));
        assertTrue(response.contains("log"));
    }
}
