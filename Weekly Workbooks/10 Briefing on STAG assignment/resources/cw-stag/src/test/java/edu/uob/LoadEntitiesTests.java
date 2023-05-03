package edu.uob;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static edu.uob.ExtendedEntitiesHelper.*;

public class LoadEntitiesTests {
    private GameState extendedGameState;

    @BeforeEach
    void setUp() {
        String fileName = "extended-entities.dot";
        this.extendedGameState = new GameState();
        File file = Paths.get("config" + File.separator + fileName).toAbsolutePath().toFile();
        EntitiesLoader entitiesLoader = new EntitiesLoader(this.extendedGameState, file);
        try {
            entitiesLoader.loadEntities();
        } catch (FileNotFoundException fileNotFoundException) {
            fail("FileNotFoundException was thrown when attempting to read " + fileName);
        } catch (ParseException parseException) {
            fail("ParseException was thrown when attempting to read " + fileName);
        }
    }

    @Test
    void testLoadExtendedEntitiesFile() {
        Location storeroom = this.extendedGameState.getStoreroom();
        assertNotNull(storeroom);
        Set<String> allLocationNames = getLocationNames();
        for (String locationName : allLocationNames) {
            Location location = this.extendedGameState.getLocationByName(locationName);
            assertNotNull(location, location + " should not be null");
            assertEquals(location, this.extendedGameState.getEntityByName(locationName), "Should be the same location " + location);
            if (locationName.equals("storeroom")) {
                assertEquals(location, storeroom, location + "should be storeroom");
            } else {
                assertNull(location.getDestinationByName(locationName), location + " should have paths to " + locationName);
            }
            if (locationName.equals("cabin")) {
                assertEquals(location, this.extendedGameState.getStartLocation(), "Should be the same location " + location);
            }
            this.testEntityLoaded(location, locationName);
            Set<String> possibleDestinations = getPossiblePathFromLocation(locationName);
            if (possibleDestinations != null) {
                this.testHasPathTo(location, possibleDestinations, allLocationNames);
            }
        }
    }

    private void testEntityLoaded(Location location, String locationName) {
        String[] informationType = {"artefacts", "furniture", "characters"};
        for (int i = 0; i < informationType.length; i++) {
            Set<String> entityNames = getEntityNamesInLocation(locationName, informationType[i]);
            for (String entityName : entityNames) {
                GameEntity entity = this.extendedGameState.getEntityByName(entityName);
                assertEquals(location, entity.getCurrentLocation(), "Should be the same location as " + location);
                if (i == 0) {
                    Artefact artefact = location.getArtefactByName(entityName);
                    assertNotNull(artefact, artefact + " should not be null");
                    assertEquals(entity, artefact, entity + " should be the same as " + artefact);
                } else if (i == 1) {
                    Furniture furniture = location.getFurnitureByName(entityName);
                    assertNotNull(furniture, furniture + " should not be null");
                    assertEquals(entity, furniture, furniture + " should be the same as " + entity);
                } else {
                    Character character = location.getCharacterByName(entityName);
                    assertNotNull(character, character + " should not be null");
                    assertEquals(entity, character, character + " should be the same as " + entity);
                }
            }
        }
    }

    private void testHasPathTo(Location location, Set<String> possibleDestinations, Set<String> allLocations) {
        for (String locationName : allLocations) {
            Location destination = location.getDestinationByName(locationName);
            if (possibleDestinations.contains(locationName)) {
                assertNotNull(destination, destination + " should not be null");
                assertEquals(destination, this.extendedGameState.getLocationByName(locationName), "Should be the same location " + destination);
            } else {
                assertNull(destination, location + " should not have path to " + locationName);
            }
        }
    }
}
