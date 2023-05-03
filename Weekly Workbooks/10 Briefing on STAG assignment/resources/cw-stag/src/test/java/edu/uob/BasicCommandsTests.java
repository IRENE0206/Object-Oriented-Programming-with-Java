package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Set;

import static edu.uob.ExtendedEntitiesHelper.*;
import static edu.uob.CommandGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

public class BasicCommandsTests {
    private GameServer server;

    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        this.server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> server.handleCommand(command),
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testBasicCommands() {
        String playerName0 = generateRandomPlayerName("sion");
        String[] locationNames = {"storeroom", "cellar", "cabin", "forest", "riverbank", "clearing"};
        String invalidCommand = playerName0 + " look:";
        String response = this.sendCommandToServer(invalidCommand);
        assertFalse(response.contains(getDescriptionOfEntity(locationNames[0])), "Invalid format");
        invalidCommand = playerName0 + " :";
        response = this.sendCommandToServer(invalidCommand);
        assertFalse(response.toLowerCase().contains(locationNames[0]), "Invalid incoming command");

        this.sendCommandToServer(validGotoCommand(playerName0, locationNames[0]));
        response = this.sendCommandToServer(validLookCommand(playerName0));
        assertFalse(response.contains(getDescriptionOfEntity(locationNames[0])), "There is no path to storeroom");

        testInvalidGotoCommand(playerName0, locationNames[3]);
        response = this.sendCommandToServer(validGotoCommand(playerName0, locationNames[1]));
        assertFalse(response.contains(getDescriptionOfEntity(locationNames[1])), "Cannot goto " + locationNames[1] + " yet");
        exploreLocation(playerName0, locationNames[2]);
        this.sendCommandToServer(validGotoCommand(playerName0, locationNames[3]));

        testInvalidGotoCommand(playerName0, locationNames[4]);
        exploreLocation(playerName0, locationNames[3]);

        this.sendCommandToServer(validGotoCommand(playerName0, locationNames[4]));
        response = this.sendCommandToServer(validGotoCommand(playerName0, locationNames[5]));
        assertFalse(response.contains(getDescriptionOfEntity(locationNames[5])), "Cannot goto " + locationNames[5] + " yet");
        exploreLocation(playerName0, locationNames[4]);
    }

    private void exploreLocation(String playerName, String currentLocation) {
        String response = this.sendCommandToServer(validLookCommand(playerName));
        assertTrue(response.contains(getDescriptionOfEntity(currentLocation)), "Response should contain description of " + currentLocation);
        testInvalidLookCommand(playerName, currentLocation);
        Set<String> artefactNames = getArtefactNamesAtLocation(currentLocation);
        Set<String> furnitureNames = getFurnitureNamesAtLocation(currentLocation);
        Set<String> characterNames = getCharacterNamesAtLocation(currentLocation);
        Set<String> accessiblePaths = getPossiblePathFromLocation(currentLocation);
        testResponseContainsDescriptions(response, artefactNames);
        testResponseContainsDescriptions(response, furnitureNames);
        testResponseContainsDescriptions(response, characterNames);
        testResponseContainsEntityNames(response, accessiblePaths);
        testGetAndDrop(playerName, artefactNames, true, false);
        testGetAndDrop(playerName, furnitureNames, false, false);
        testGetAndDrop(playerName, characterNames, false, false);
        testGetAndDrop(playerName, accessiblePaths, false, true);
    }

    private void testResponseContainsDescriptions(String response, Set<String> entityNames) {
        if (entityNames != null) {
            for (String entityName : entityNames) {
                assertTrue(response.contains(getDescriptionOfEntity(entityName)), "Response should contain description of " + entityName);
            }
        }
    }

    private void testResponseContainsEntityNames(String response, Set<String> entityNames) {
        if (entityNames != null) {
            for (String entityName : entityNames) {
                assertTrue(response.toLowerCase().contains(entityName), "Response should contain " + entityName);
            }
        }
    }

    private void testGetAndDrop(String playerName, Set<String> entityNames, boolean isArtefact, boolean isLocation) {
        for (String entityName : entityNames) {
            String command = validGetCommand(playerName, entityName);
            // System.out.println(command);
            this.sendCommandToServer(command);
            String lookResponse = this.sendCommandToServer(validLookCommand(playerName));
            String invResponse = this.sendCommandToServer(validInvCommand(playerName));
            String description = getDescriptionOfEntity(entityName);
            if (isArtefact) {
                testInvalidDropCommand(playerName, entityName);
                assertFalse(lookResponse.contains(description), entityName + " should be picked by " + playerName);
                assertTrue(invResponse.contains(description), entityName + " should be picked by " + playerName);
                testInvalidInvCommand(playerName, entityName);
            } else if (isLocation) {
                assertFalse(invResponse.contains(description), entityName + " cannot be picked");
                assertTrue(lookResponse.toLowerCase().contains(entityName), entityName + " cannot be picked");
            } else {
                assertFalse(invResponse.contains(description), entityName + " cannot be picked");
                // System.out.println(lookResponse);
                assertTrue(lookResponse.contains(description), entityName + " cannot be picked");
            }
            this.sendCommandToServer(validDropCommand(playerName, entityName));
            lookResponse = this.sendCommandToServer(validLookCommand(playerName));
            invResponse = this.sendCommandToServer(validInvCommand(playerName));
            testInvalidGetCommand(playerName, entityName);
            assertFalse(invResponse.contains(description), "response should not contain " + description);
            if (isLocation) {
                assertTrue(lookResponse.toLowerCase().contains(entityName), "response should contain " + entityName);
            } else {
                assertTrue(lookResponse.contains(description), "response should contain " + description);
            }
            this.sendCommandToServer(validGetCommand(playerName, entityName));
        }
    }

    private void testInvalidInvCommand(String playerName, String entityName) {
        String command = invalidInvCommand(playerName);
        // System.out.println(command);
        String response = sendCommandToServer(command);
        // System.out.println("InvalidInvCommand" + response);
        assertFalse(response.contains(getDescriptionOfEntity(entityName)), "invalid 'inv' command");
        command = playerName + ": " + entityName + " inv";
        response = sendCommandToServer(command);
        assertFalse(response.contains(getDescriptionOfEntity(entityName)), entityName + " should not come before 'inv'");
        command = playerName + ": " + entityName + " inventory";
        response = sendCommandToServer(command);
        assertFalse(response.contains(getDescriptionOfEntity(entityName)), entityName + " should not come before 'inventory'");

    }

    private void testInvalidLookCommand(String playerName, String locationName) {
        String command = invalidLookCommand(playerName);
        // System.out.println(command);
        String response = sendCommandToServer(command);
        // System.out.println("InvalidLookCommand" + response);
        assertFalse(response.contains(getDescriptionOfEntity(locationName)), "invalid look command");
    }

    private void testInvalidGetCommand(String playerName, String entityName) {
        String command = invalidGetCommand(playerName, entityName);
        sendCommandToServer(command);
        // System.out.println(command);
        command = validInvCommand(playerName);
        String response = sendCommandToServer(command);
        // System.out.println("InvalidGetCommand" + response);
        assertFalse(response.contains(getDescriptionOfEntity(entityName)), "invalid get command");
        command = playerName + ": " + entityName + " get";
        sendCommandToServer(command);
        command = validInvCommand(playerName);
        response = sendCommandToServer(command);
        assertFalse(response.contains(getDescriptionOfEntity(entityName)), entityName + " should not come before 'get'");

    }

    private void testInvalidGotoCommand(String playerName, String locationName) {
        String command = invalidGotoCommand(playerName, locationName);
        // System.out.println(command);
        String response = sendCommandToServer(command);
        // System.out.println("InvalidGotoCommand" + response);
        assertFalse(response.contains(getDescriptionOfEntity(locationName)), "invalid goto command");
        command = playerName + ": " + locationName + " goto";
        response = sendCommandToServer(command);
        assertFalse(response.contains(getDescriptionOfEntity(locationName)), locationName + " should not come before 'goto'");
    }

    private void testInvalidDropCommand(String playerName, String entityName) {
        String command = invalidDropCommand(playerName, entityName);
        // System.out.println(command);
        sendCommandToServer(command);
        command = validLookCommand(playerName);
        String response = sendCommandToServer(command);
        // System.out.println("InvalidDropCommand" + response);
        assertFalse(response.contains(getDescriptionOfEntity(entityName)), "invalid drop command");
        command = playerName + ": " + entityName + " drop";
        sendCommandToServer(command);
        command = validLookCommand(playerName);
        response = sendCommandToServer(command);
        assertFalse(response.contains(getDescriptionOfEntity(entityName)), entityName + " should not come before 'drop'");
    }
}
