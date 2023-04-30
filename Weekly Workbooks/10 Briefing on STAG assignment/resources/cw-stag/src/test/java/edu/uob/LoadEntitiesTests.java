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
        String[] locations = {"cabin", "storeroom", "cellar", "forest"};
        String[] artefacts = {"log", "potion", "key"};
        String[] furniture = {"trapdoor", "tree"};
        String[] characters = {"elf"};
        Location storeroom = basicGameState.getStoreroom();
        assertEquals(0, storeroom.getPathsNumber());
        Location startLocation = basicGameState.getStartLocation();
        assertEquals(locations[0], startLocation.getName());
        assertEquals(locations[1],storeroom.getName());
        assertNotNull(basicGameState.getLocationByName(locations[2]));
        assertNotNull(basicGameState.getLocationByName(locations[3]));
        assertTrue(storeroom.hasArtefact(artefacts[0]));
        assertTrue(startLocation.hasArtefact(artefacts[1]));
        assertTrue(startLocation.hasFurniture(furniture[0]));
        Location cellar = basicGameState.getLocationByName(locations[2]);
        assertTrue(cellar.hasCharacter(characters[0]));
        assertNotNull(cellar.getDestinationByName(locations[0]));
        assertNull(startLocation.getDestinationByName(locations[2]));
        assertNotNull(startLocation.getDestinationByName(locations[3]));
        Location forest = basicGameState.getLocationByName(locations[3]);
        assertNotNull(forest.getDestinationByName(locations[0]));
        assertTrue(forest.hasArtefact(artefacts[2]));
        assertTrue(forest.hasFurniture(furniture[1]));
        testGameStateContainsSubjects(locations, basicGameState);
        testGameStateContainsSubjects(artefacts, basicGameState);
        testGameStateContainsSubjects(furniture, basicGameState);
        testGameStateContainsSubjects(characters, basicGameState);
    }

    @Test
    void testLoadExtendedEntitiesFile() {
        Location storeroom = extendedGameState.getStoreroom();
        assertEquals(0, storeroom.getPathsNumber());
        String[] artefactsInCabin = {"potion", "axe", "coin"};
        String[] artefactsInStore = {"log", "shovel", "gold"};
        String[] artefactsInForest = {"key"};
        String[] artefactsInRiverbank = {"horn"};
        String[] furniture = {"hole", "trapdoor", "tree", "river", "ground"};
        String[] locations = {"cabin", "cellar", "forest", "riverbank", "clearing"};
        String[] characters = {"lumberjack", "elf"};
        for (String artefact : artefactsInStore) {
            assertTrue(storeroom.hasArtefact(artefact));
        }
        storeroom.hasFurniture(furniture[0]);
        storeroom.hasCharacter(characters[0]);
        Location cellar = extendedGameState.getLocationByName(locations[1]);
        assertTrue(cellar.hasCharacter(characters[1]));
        assertNotNull(cellar.getDestinationByName(locations[0]));
        Location cabin = extendedGameState.getLocationByName(locations[0]);
        for (String artefact : artefactsInCabin) {
            assertTrue(cabin.hasArtefact(artefact));
        }
        assertTrue(cabin.hasFurniture(furniture[1]));
        assertNull(cabin.getDestinationByName(locations[1]));
        Location forest = extendedGameState.getLocationByName(locations[2]);
        assertNotNull(forest.getDestinationByName(locations[0]));
        assertNotNull(cabin.getDestinationByName(locations[2]));
        assertNotNull(forest.getDestinationByName(locations[3]));
        assertTrue(forest.hasArtefact(artefactsInForest[0]));
        assertTrue(forest.hasFurniture(furniture[2]));
        Location riverbank = extendedGameState.getLocationByName(locations[3]);
        assertNotNull(riverbank.getDestinationByName(locations[2]));
        assertNull(riverbank.getDestinationByName(locations[4]));
        assertTrue(riverbank.hasArtefact(artefactsInRiverbank[0]));
        assertTrue(riverbank.hasFurniture(furniture[3]));
        Location clearing = extendedGameState.getLocationByName(locations[4]);
        assertNotNull(clearing.getDestinationByName(locations[3]));
        assertTrue(clearing.hasFurniture(furniture[4]));
        testGameStateContainsSubjects(locations, extendedGameState);
        testGameStateContainsSubjects(artefactsInCabin, extendedGameState);
        testGameStateContainsSubjects(artefactsInForest, extendedGameState);
        testGameStateContainsSubjects(artefactsInRiverbank, extendedGameState);
        testGameStateContainsSubjects(artefactsInStore, extendedGameState);
        testGameStateContainsSubjects(furniture, extendedGameState);
        testGameStateContainsSubjects(characters, extendedGameState);
    }

    private void testGameStateContainsSubjects(String[] subjects, GameState gameState) {
        for (String subjectName : subjects) {
            assertNotNull(gameState.getEntityByName(subjectName));
        }
    }
}
