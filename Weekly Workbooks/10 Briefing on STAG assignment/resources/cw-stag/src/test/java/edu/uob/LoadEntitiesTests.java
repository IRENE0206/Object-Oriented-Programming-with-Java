package edu.uob;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class LoadEntitiesTests {
    private GameState basicGameState;
    private GameState extendedGameState;

    @BeforeEach
    void setUp() {
        basicGameState = loadFile("basic-entities.dot");
        extendedGameState = loadFile("extended-entities.dot");
    }

    private GameState loadFile(String fileName) {
        File file = Paths.get("config" + File.separator + fileName).toAbsolutePath().toFile();
        GameState gameState = new GameState();
        EntitiesLoader entitiesLoader = new EntitiesLoader(gameState, file);
        try {
            entitiesLoader.loadEntities();
        } catch (FileNotFoundException fileNotFoundException) {
            fail("FileNotFoundException was thrown when attempting to read " + fileName);
        } catch (ParseException parseException) {
            fail("ParseException was thrown when attempting to read " + fileName);
        }
        return gameState;
    }

    @Test
    void testLoadBasicEntitiesFile() {
        Location storeroom = basicGameState.getStoreroom();
        assertEquals(0, storeroom.getPathsToLocations().size());
        Location startLocation = basicGameState.getStartLocation();
        assertEquals("cabin", startLocation.getName());
        assertEquals("storeroom",storeroom.getName());
        assertTrue(basicGameState.hasLocation("cellar"));
        assertTrue(basicGameState.hasLocation("forest"));
        assertTrue(storeroom.hasArtefact("log"));
        assertTrue(startLocation.hasArtefact("potion"));
        assertTrue(startLocation.hasFurniture("trapdoor"));
        Location cellar = basicGameState.getLocationByName("cellar");
        assertTrue(cellar.hasCharacter("elf"));
        assertTrue(cellar.hasPathToLocation("cabin"));
        assertFalse(startLocation.hasPathToLocation("cellar"));
        assertTrue(startLocation.hasPathToLocation("forest"));
        Location forest = basicGameState.getLocationByName("forest");
        assertTrue(forest.hasPathToLocation("cabin"));
        assertTrue(forest.hasArtefact("key"));
        assertTrue(forest.hasFurniture("tree"));
    }

    @Test
    void testLoadExtendedEntitiesFile() {
        Location storeroom = extendedGameState.getStoreroom();
        assertEquals(0, storeroom.getPathsToLocations().size());
        String[] artefactsInStore = {"log", "shovel", "gold"};
        for (String artefact : artefactsInStore) {
            assertTrue(storeroom.hasArtefact(artefact));
        }
        storeroom.hasFurniture("hole");
        storeroom.hasCharacter("lumberjack");
        Location cellar = extendedGameState.getLocationByName("cellar");
        assertTrue(cellar.hasCharacter("elf"));
        assertTrue(cellar.hasPathToLocation("cabin"));
        Location cabin = extendedGameState.getLocationByName("cabin");
        String[] artefactsInCabin = {"potion", "axe", "coin"};
        for (String artefact : artefactsInCabin) {
            assertTrue(cabin.hasArtefact(artefact));
        }
        assertTrue(cabin.hasFurniture("trapdoor"));
        assertFalse(cabin.hasPathToLocation("cellar"));
        Location forest = extendedGameState.getLocationByName("forest");
        assertTrue(forest.hasPathToLocation("cabin"));
        assertTrue(forest.hasPathToLocation("riverbank"));
        assertTrue(forest.hasArtefact("key"));
        assertTrue(forest.hasFurniture("tree"));
        Location riverbank = extendedGameState.getLocationByName("riverbank");
        assertTrue(riverbank.hasPathToLocation("forest"));
        assertFalse(riverbank.hasPathToLocation("clearing"));
        assertTrue(riverbank.hasArtefact("horn"));
        assertTrue(riverbank.hasFurniture("river"));
        Location clearing = extendedGameState.getLocationByName("clearing");
        assertTrue(clearing.hasPathToLocation("riverbank"));
        assertTrue(clearing.hasFurniture("ground"));
    }
}
