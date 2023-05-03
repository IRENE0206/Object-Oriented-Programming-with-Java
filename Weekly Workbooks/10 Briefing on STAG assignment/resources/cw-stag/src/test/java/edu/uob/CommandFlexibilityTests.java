package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static edu.uob.CommandGenerator.*;
import static edu.uob.ExtendedEntitiesHelper.*;
import static edu.uob.ExtendedActionsHelper.*;

public class CommandFlexibilityTests {
    private GameServer server;

    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }


    String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> server.handleCommand(command),
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void test() {
        String[] artefacts = {"potion", "axe", "shovel", "key", "coin", "log", "horn", "gold"};

        String playerName0 = generateRandomPlayerName("simon");

        String forest = "forest";
        this.sendCommandToServer(validGotoCommand(playerName0, forest));
        this.testGetArtefactInLocation(playerName0, artefacts[3]);

        String cabin = "cabin";
        this.sendCommandToServer(validGotoCommand(playerName0, cabin));
        this.testGetArtefactInLocation(playerName0, artefacts[4]);
        testValidActionCommand(playerName0, 0);

        String cellar = "cellar";
        this.sendCommandToServer(validGotoCommand(playerName0, cellar));
        this.testValidActionCommand(playerName0, 4);
        this.testGetArtefactInLocation(playerName0, artefacts[2]);

        this.sendCommandToServer(validGotoCommand(playerName0, cabin));
        this.testGetArtefactInLocation(playerName0, artefacts[1]);

        this.sendCommandToServer(validGotoCommand(playerName0, forest));
        this.testValidActionCommand(playerName0, 1);
        this.testGetArtefactInLocation(playerName0, artefacts[5]);

        String riverbank = "riverbank";
        this.sendCommandToServer(validGotoCommand(playerName0, riverbank));
        testGetArtefactInLocation(playerName0, artefacts[6]);
        testValidActionCommand(playerName0, 7);
        testValidActionCommand(playerName0, 5);

        String clearing = "clearing";
        this.sendCommandToServer(validGotoCommand(playerName0, clearing));
        testValidActionCommand(playerName0, 6);
        testGetArtefactInLocation(playerName0, artefacts[7]);
        testValidActionCommand(playerName0, 7);

        this.sendCommandToServer(validGotoCommand(playerName0, riverbank));
        this.sendCommandToServer(validGotoCommand(playerName0, forest));
        this.sendCommandToServer(validGotoCommand(playerName0, cabin));


        String playerName1 = generateRandomPlayerName("pete");

        String response = this.sendCommandToServer(validLookCommand(playerName1));
        testResponseContainsDescriptions(response, getFurnitureNamesAtLocation(cabin));
        assertTrue(response.contains(getDescriptionOfEntity(artefacts[0])), playerName0 + " didn't pick up potion");
        this.testGetArtefactInLocation(playerName1, artefacts[0]);
        assertTrue(response.toLowerCase().contains(cellar), "Path from " + cabin + " to " + cellar + " should be available");
        assertTrue(response.toLowerCase().contains(playerName0.toLowerCase()), playerName1 + " should be able to see " + playerName0 + " at the " + cabin);
        response = this.sendCommandToServer(validLookCommand(playerName0));
        assertTrue(response.toLowerCase().contains(playerName1.toLowerCase()), playerName0 + " should be able to see " + playerName1 + " at the " + cabin);

        this.sendCommandToServer(validGotoCommand(playerName1, cellar));
        this.testValidActionCommand(playerName1, 3);
        this.testHealth(playerName1, 2);
        this.testValidActionCommand(playerName1, 3);
        this.testHealth(playerName1, 1);
        this.testValidActionCommand(playerName1, 3);
        this.testHealth(playerName1, 3);
        response = this.sendCommandToServer(validLookCommand(playerName1));
        testResponseContainsDescriptions(response, getFurnitureNamesAtLocation(cabin));

        this.testDropArtefactAtLocation(playerName0, artefacts[1]);
        this.testGetArtefactInLocation(playerName1, artefacts[1]);
        this.sendCommandToServer(validGotoCommand(playerName1, cellar));
        this.testValidActionCommand(playerName1, 9);
        this.testHealth(playerName1, 2);
        this.testValidActionCommand(playerName1, 2);
        this.testHealth(playerName1, 3);

        this.sendCommandToServer(validGotoCommand(playerName1, cabin));
        this.testDropArtefactAtLocation(playerName0, artefacts[2]);
        this.testGetArtefactInLocation(playerName1, artefacts[2]);

        this.sendCommandToServer(validGotoCommand(playerName1, forest));
        response = this.sendCommandToServer(validLookCommand(playerName1));
        testResponseNotContainDescriptions(response, getFurnitureNamesAtLocation(forest));
        testResponseNotContainDescriptions(response, getArtefactNamesAtLocation(forest));

        this.sendCommandToServer(validGotoCommand(playerName1, riverbank));
        response = this.sendCommandToServer(validLookCommand(playerName1));
        testResponseContainsDescriptions(response, getFurnitureNamesAtLocation(riverbank));
        testResponseNotContainDescriptions(response, getArtefactNamesAtLocation(riverbank));

        this.sendCommandToServer(validGotoCommand(playerName1, clearing));
        response = this.sendCommandToServer(validLookCommand(playerName1));
        String hole = "hole";
        assertTrue(response.contains(getDescriptionOfEntity(hole)), playerName0 + " should have dug a hole here");
        this.testValidActionCommand(playerName1, 10);
    }

    private void testGetArtefactInLocation(String playerName, String artefactName) {
        String response = sendCommandToServer(validLookCommand(playerName));
        assertTrue(response.contains(getDescriptionOfEntity(artefactName)), artefactName + " has not been picked by " + playerName);
        this.sendCommandToServer(validGetCommand(playerName, artefactName));
        response = sendCommandToServer(validLookCommand(playerName));
        assertFalse(response.contains(getDescriptionOfEntity(artefactName)), artefactName + " should have been picked by " + playerName);
    }

    private void testDropArtefactAtLocation(String playerName, String artefactName) {
        String response = this.sendCommandToServer(validLookCommand(playerName));
        assertFalse(response.contains(getDescriptionOfEntity(artefactName)), playerName + " have not dropped " + artefactName);
        this.sendCommandToServer(validDropCommand(playerName, artefactName));
        response = this.sendCommandToServer(validLookCommand(playerName));
        assertTrue(response.contains(getDescriptionOfEntity(artefactName)), playerName + " should have dropped " + artefactName);
    }

    private void testHealth(String playerName, int expectedHealth) {
        String command = validHealthCommand(playerName);
        String response = sendCommandToServer(command).toLowerCase();
        assertTrue(response.contains(String.valueOf(expectedHealth)), "Expected health should be " + expectedHealth + " not " + response);
    }

    private void testResponseContainsDescriptions(String response, Set<String> entityNames) {
        if (entityNames.isEmpty()) {
            return;
        }
        for (String entityName : entityNames) {
            if (!entityName.equalsIgnoreCase("health")) {
                assertTrue(response.contains(getDescriptionOfEntity(entityName)), "Response should contain description of " + entityName);
            }
        }
    }

    private void testResponseNotContainDescriptions(String response, Set<String> entityNames) {
        if (entityNames.isEmpty()) {
            return;
        }
        for (String entityName : entityNames) {
            if (!entityName.equalsIgnoreCase("health")) {
                assertFalse(response.contains(getDescriptionOfEntity(entityName)), "Response should not contain description of " + entityName);
            }
        }
    }

    private void testValidActionCommand(String playerName, int actionIndex) {
        Set<String> consumed = getActionConsumed(actionIndex);
        Set<String> produced = getActionProduced(actionIndex);
        String command = generateValidActionCommand(playerName, actionIndex);
        String response = sendCommandToServer(command);
        String narration = getActionNarration(actionIndex);
        assertTrue(response.contains(narration), "Response should contain action narration");

        response = sendCommandToServer(validLookCommand(playerName));
        testResponseNotContainDescriptions(response, consumed);
        response = sendCommandToServer(validInvCommand(playerName));
        testResponseNotContainDescriptions(response, consumed);

        response = sendCommandToServer(validLookCommand(playerName));
        for (String entityName : produced) {
            if (getLocationNames().contains(entityName)) {
                assertTrue(response.toLowerCase().contains(entityName), entityName + " should be produced");
            } else if (!entityName.equalsIgnoreCase("health")){
                assertTrue(response.contains(getDescriptionOfEntity(entityName)), entityName + " should be produced");
            }
        }
    }

}


