package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ExtendedActionsTests {
    private GameServer server;

    @BeforeEach
    public void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> server.handleCommand(command),
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    // The ordering of the words in a command should not affect the server's ability to find appropriate matching actions
    @Test
    public void testRandomOrderBasicActions() {
        String response = sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        response = sendCommandToServer("simon: look").toLowerCase();
        System.out.println(response);
        assertTrue(response.contains("tree"));
        assertFalse(response.contains("log"));
        response = sendCommandToServer("simon: axe chop tree");
        System.out.println(response);
        assertEquals("You cut down the tree with the axe", response);
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("tree"));
        assertTrue(response.contains("log"));
    }

    // Partial Commands
    // omit some of the subjects from a command,
    // whilst still providing enough information for the correct action to be identified
    @Test
    public void testShortenedBasicActions() {
        sendCommandToServer("simon: get axe");
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
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        // Trigger keywords can't be used decorations
        // A message that contains multiple action commands
        // (i.e. triggers phrases from multiple actions) is not acceptable and should be rejected
        // (irrespective of whether those actions are matched or performable)
        String response = sendCommandToServer("simon: cut tree drink");
        assertNotEquals("You cut down the tree with the axe", response);
    }

    //  Extraneous Entities i.e. entities that are in the incoming command, but not specified in the action file
    @Test
    public void testInvalidTriggerPhrase() {
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        // forest is an extraneous entity (assuming it isn't specified in the action) so it should be rejected
        String response = sendCommandToServer("simon: cut tree from forest");
        assertNotEquals("You cut down the tree with the axe", response);
        response = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(response.contains("tree"));
        assertFalse(response.contains("log"));
    }

    @Test
    public void testDuplicateTriggerPhrase() {
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        // (contains two triggers from the same action chop and cut, but still makes sense)
        String response = sendCommandToServer("simon: chop tree with axe to cut it down");
        assertEquals("You cut down the tree with the axe", response);
        response = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(response.contains("tree"));
        assertTrue(response.contains("log"));
    }

    @Test
    public void testDuplicateSubjects() {
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        String response = sendCommandToServer("simon: cut tree and cut tree");

        assertEquals("You cut down the tree with the axe", response);
        response = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(response.contains("tree"));
        assertTrue(response.contains("log"));
    }

    // Decorated Commands
    @Test
    public void testDecoratedCommand() {
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        String response = sendCommandToServer("simon: please chop the tree using the axe");
        assertEquals("You cut down the tree with the axe", response);
    }

    // Case Insensitivity


    // each incoming command MUST contain a trigger phrase and at least one subject.


    // Ambiguous Commands
    // more than one action matches a particular command
    // NO action should be performed

    // Composite Commands
    // A single command can only be used to perform a single built-in command or single game action.
}
