package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static edu.uob.ExtendedEntitiesHelper.*;
import static edu.uob.ExtendedActionsHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class ExtendedCommandsTests {
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
    public void testCommandWithoutRandomise() {
        String playerName = "simon: ";

        String cabin = "cabin";
        String potion = "potion";
        String axe = "axe";
        String get = "get ";
        String inv = "inv";
        String goTo = "goto ";
        String look = "look";
        String coin = "coin";
        testStandardLookGetDrop(playerName, cabin);
        String command = playerName + get + axe + " and " + potion;
        this.sendCommandToServer(command);
        command = playerName + look;
        String response = this.sendCommandToServer(command);
        assertTrue(response.contains(getDescriptionOfEntity(axe)), "composite command referencing two different entities");
        assertTrue(response.contains(getDescriptionOfEntity(potion)), "composite command referencing two different entities");

        String forest = "forest";
        String key = "key";
        command = playerName + goTo + "dark " + forest;
        this.sendCommandToServer(command);
        testStandardLookGetDrop(playerName, forest);

        command = playerName + key + " " + "get ";
        this.sendCommandToServer(command);
        command = playerName + inv;
        response = this.sendCommandToServer(command);
        String keyDescription = getDescriptionOfEntity(key);
        command = playerName + get + key;
        this.sendCommandToServer(command);
        assertFalse(response.contains(keyDescription), "input must have command first and then subject entity");
        command = playerName + inv;
        response = this.sendCommandToServer(command);
        keyDescription = getDescriptionOfEntity(key);
        assertTrue(response.contains(keyDescription), key + " was picked by " + playerName);
        command = playerName + look;
        response = this.sendCommandToServer(command);
        assertFalse(response.contains(keyDescription), key + " was picked by " + playerName);

        command = playerName + cabin + " " + goTo;
        this.sendCommandToServer(command);
        command = playerName + look;
        response = this.sendCommandToServer(command);
        assertFalse(response.contains(getDescriptionOfEntity(cabin)), "input must have command first and then subject entity");
        command = playerName + goTo + cabin;
        this.sendCommandToServer(command);
        command = playerName + get + coin;
        this.sendCommandToServer(command);
        command = playerName + inv + " please";
        response = this.sendCommandToServer(command);
        assertTrue(response.contains(getDescriptionOfEntity(coin)), playerName + " picked up a " + coin);
        command = playerName + "unlock trapdoor with " + key;
        response = this.sendCommandToServer(command);
        testPerformAction(0, playerName);
        String narrationError = "action narration should be presented";
        assertTrue(response.contains(getActionNarration(0)), narrationError);

        String cellar = "cellar";
        command = playerName + goTo + cellar;
        this.sendCommandToServer(command);
        command = playerName + look;
        response = this.sendCommandToServer(command);
        assertTrue(response.contains(getDescriptionOfEntity(cellar)), playerName + " has moved to " + cellar);
        command = playerName + look + cellar;
        response = this.sendCommandToServer(command);
        assertFalse(response.contains(getDescriptionOfEntity(cellar)), cellar + " is extraneous entity to " + look);

        String elf = "elf";
        command = playerName + "attack " + elf;
        response = this.sendCommandToServer(command);
        testPerformAction(3, playerName);
        assertTrue(response.contains(getActionNarration(3)), narrationError);
        String health = "health";
        command = playerName + get + health;
        response = this.sendCommandToServer(command);
        assertNotEquals("2", response, playerName + "'s health should be reduced by 1");
        command = playerName + health;
        response = this.sendCommandToServer(command);
        assertEquals("2", response, "'health' is a built-in command, so is a reserved word and can’t be used by an action");
        command = playerName + " pay " + coin;
        response = this.sendCommandToServer(command);
        testPerformAction(4, playerName);
        assertTrue(response.contains(getActionNarration(4)), narrationError);
        String shovel = "shovel";
        command = playerName + get + shovel + " and " + coin;
        this.sendCommandToServer(command);
        command = playerName + inv;
        response = this.sendCommandToServer(command);
        assertFalse(response.contains(getDescriptionOfEntity(shovel)), "Composite commands should NOT be supported");
        command = playerName + get + shovel;
        this.sendCommandToServer(command);
        command = playerName + inv;
        response = this.sendCommandToServer(command);
        assertTrue(response.contains(getDescriptionOfEntity(shovel)), playerName + " picked up the " + shovel);

        command = playerName + goTo + cabin;
        this.sendCommandToServer(command);
        command = playerName + "drink " + potion;
        response = this.sendCommandToServer(command);
        testPerformAction(2, playerName);
        assertTrue(response.contains(getActionNarration(2)), narrationError);
        command = playerName + health;
        response = this.sendCommandToServer(command);
        assertEquals("3", response, playerName + "'s health should be increased by 1");
        command = playerName + get + axe;
        this.sendCommandToServer(command);
        command = playerName + inv;
        response = this.sendCommandToServer(command);
        assertTrue(response.contains(getDescriptionOfEntity(axe)), playerName + " picked up the " + axe);

        command = playerName + goTo + cellar;
        this.sendCommandToServer(command);
        command = playerName + look;
        response = this.sendCommandToServer(command);
        assertTrue(response.contains(getDescriptionOfEntity(cellar)), playerName + " has moved to " + cellar);
        assertTrue(response.contains(getDescriptionOfEntity(elf)), elf + " should still be in cellar");
        command = playerName + "kill " + elf;
        response = this.sendCommandToServer(command);
        testPerformAction(9, playerName);
        assertTrue(response.contains(getActionNarration(9)), narrationError);
        command = playerName + look;
        response = this.sendCommandToServer(command);
        assertFalse(response.contains(getDescriptionOfEntity(elf)), elf + " should have been removed from " + cellar);
        command = playerName + inv;
        response = this.sendCommandToServer(command);
        assertTrue(response.contains(getDescriptionOfEntity(axe)), axe + " should not be consumed");
        command = playerName + health;
        response = this.sendCommandToServer(command);
        assertEquals("2", response, playerName + "'s health should be reduced by 1");

        command = playerName + goTo + cabin;
        this.sendCommandToServer(command);
        String trapdoor = "trapdoor";
        command = playerName + look;
        response = this.sendCommandToServer(command);
        assertTrue(response.contains(getDescriptionOfEntity(trapdoor)), cabin + " should have " + trapdoor);
        assertTrue(response.toLowerCase().contains(cellar), cabin + " should have path to " + cellar);
        command = playerName + "lock ";
        this.sendCommandToServer(command);
        command = playerName + look;
        response = this.sendCommandToServer(command);
        assertTrue(response.contains(getDescriptionOfEntity(trapdoor)), "action lacks subject so should not perform");
        assertTrue(response.toLowerCase().contains(cellar), "action lacks subject so should not perform");
        command = playerName + "shut down " + trapdoor;
        response = this.sendCommandToServer(command);
        testPerformAction(8, playerName);
        assertTrue(response.contains(getActionNarration(8)), narrationError);
        command = playerName + look;
        response = this.sendCommandToServer(command);
        assertTrue(response.contains(getDescriptionOfEntity(trapdoor)), trapdoor + " should not be consumed");
        assertFalse(response.toLowerCase().contains(cellar), " path to " + cellar + " should be consumed");

        command = playerName + goTo + forest;
        this.sendCommandToServer(command);
        command = playerName + "use the " + axe + " to chop the tree";
        response = this.sendCommandToServer(command);
        testPerformAction(1, playerName);
        assertTrue(response.contains(getActionNarration(1)), narrationError);
        String log = "log";
        command = playerName + get + log;
        this.sendCommandToServer(command);
        command = playerName + inv;
        response = this.sendCommandToServer(command);
        assertTrue(response.contains(getDescriptionOfEntity(log)), playerName + " picked up the " + log);

        String riverbank = "riverbank";
        command = playerName + goTo + riverbank;
        this.sendCommandToServer(command);
        testStandardLookGetDrop(playerName, riverbank);
        String horn = "horn";
        command = playerName + get + horn;
        this.sendCommandToServer(command);
        command = playerName + inv;
        response = this.sendCommandToServer(command);
        assertTrue(response.contains(getDescriptionOfEntity(horn)), playerName + " picked up the " + horn);

        String blow = "blow ";
        String lumberjack = "lumberjack";
        command = playerName + blow + horn + " to " + lumberjack;
        response = this.sendCommandToServer(command);
        assertFalse(response.contains(getActionNarration(7)), "command includes an entity that is not a subject, it is extraneous");
        command = playerName + blow + horn;
        response = this.sendCommandToServer(command);
        testPerformAction(7, playerName);
        assertTrue(response.contains(getActionNarration(7)), narrationError);
        command = playerName + look;
        response = this.sendCommandToServer(command);
        assertTrue(response.contains(getDescriptionOfEntity(lumberjack)), lumberjack + " should be produced");
        String clearing = "clearing";
        assertFalse(response.toLowerCase().contains(clearing), "bridge hasn't been built");
        command = playerName + "bridge " + log;
        response = this.sendCommandToServer(command);
        testPerformAction(5, playerName);
        assertTrue(response.contains(getActionNarration(5)), narrationError);

        command = playerName + goTo + clearing;
        this.sendCommandToServer(command);
        String hole = "hole";
        command = playerName + look;
        response = this.sendCommandToServer(command);
        assertFalse(response.contains(getDescriptionOfEntity(hole)), "you haven't yet dig the " + hole);
        command = playerName + "dig with " + shovel;
        response = this.sendCommandToServer(command);
        testPerformAction(6, playerName);
        assertTrue(response.contains(getActionNarration(6)), narrationError);
        command = playerName + "bury " + lumberjack + " in the " + hole;
        response = this.sendCommandToServer(command);
        assertFalse(response.contains(getActionNarration(10)), lumberjack + " isn't here so cannot be buried");
        command = playerName + blow + horn;
        response = this.sendCommandToServer(command);
        testPerformAction(7, playerName);
        assertTrue(response.contains(getActionNarration(7)), narrationError);
        command = playerName + "kill";
        response = this.sendCommandToServer(command);
        assertFalse(response.contains(getActionNarration(10)), "cannot kill without a subject specified");
        command = playerName + "kill " + lumberjack + " with the " + axe;
        response = this.sendCommandToServer(command);
        assertFalse(response.contains(getActionNarration(10)), axe + " is not a subject in the action");
        command = playerName + "kill " + lumberjack + " with the " + shovel + " and bury him in the " + hole;
        response = this.sendCommandToServer(command);
        testPerformAction(10, playerName);
        assertTrue(response.contains(getActionNarration(10)), narrationError);
        command = playerName + look;
        response = this.sendCommandToServer(command);
        assertFalse(response.contains(getDescriptionOfEntity(hole)), hole + " should have been consumed");
        assertFalse(response.contains(getDescriptionOfEntity(lumberjack)), lumberjack + " should have been consumed");
    }

    private void testPerformAction(int actionIndex, String playerName) {
        String command = playerName + "look";
        String response0 = this.sendCommandToServer(command);
        command = playerName + "inventory";
        String response1 = this.sendCommandToServer(command);
        for (String consumed : getActionConsumed(actionIndex)) {
            if (consumed.equalsIgnoreCase("health")) {
                continue;
            }
            String description = getDescriptionOfEntity(consumed);
            assertFalse(response0.contains(description), consumed + " should have been consumed");
            assertFalse(response1.contains(description), consumed + " should have been consumed");
            if (isLocationName(consumed)) {
                assertFalse(response0.toLowerCase().contains(consumed), "path to " + consumed + " should have been removed");
            }
        }
        for (String produced : getActionProduced(actionIndex)) {
            String description = getDescriptionOfEntity(produced);
            if (!produced.equalsIgnoreCase("health")) {
                if (isLocationName(produced)) {
                    assertTrue(response0.toLowerCase().contains(produced), "path to " + produced + " should be available");
                } else {
                    assertTrue(response0.contains(description), produced + " should have been produced");
                    assertFalse(response1.contains(description), produced + " should not be added to inventory automatically");
                }
            }
        }
    }

    private boolean isLocationName(String entityName) {
        return getLocationNames().contains(entityName);
    }

    private void testStandardLookGetDrop(String playerName, String locationName) {
        String command;
        String response;
        String errorMessage = "Response should contain ";
        for (String artefactName : getArtefactNamesAtLocation(locationName)) {
            command = playerName + "look";
            response = this.sendCommandToServer(command);
            String description = getDescriptionOfEntity(artefactName);
            assertTrue(response.contains(description), errorMessage + "description of " + artefactName);
            command = playerName + "inv";
            response = this.sendCommandToServer(command);
            assertFalse(response.contains(description), artefactName + " is not picked up by player yet");
            command = playerName + "get " + artefactName;
            this.sendCommandToServer(command);
            command = playerName + "look";
            response = this.sendCommandToServer(command);
            assertFalse(response.contains(description), artefactName + " is removed from " + locationName);
            command = playerName + "inventory";
            response = this.sendCommandToServer(command);
            assertTrue(response.contains(description), artefactName + " is put into player's inventory");
            command = playerName + "drop " + artefactName;
            this.sendCommandToServer(command);
            command = playerName + "inv";
            response = this.sendCommandToServer(command);
            assertFalse(response.contains(description), artefactName + " is dropped down by user");
        }
        command = playerName + "look";
        response = this.sendCommandToServer(command);
        for (String furnitureName : getFurnitureNamesAtLocation(locationName)) {
            assertTrue(response.contains(getDescriptionOfEntity(furnitureName)), errorMessage + "description of " + furnitureName);
        }
        for (String characterName : getCharacterNamesAtLocation(locationName)) {
            assertTrue(response.contains(getDescriptionOfEntity(characterName)), errorMessage + "description of " + characterName);
        }
        for (String pathName : getPossiblePathFromLocation(locationName)) {
            assertTrue(response.toLowerCase().contains(pathName.toLowerCase()), errorMessage + pathName);
        }
    }
}
