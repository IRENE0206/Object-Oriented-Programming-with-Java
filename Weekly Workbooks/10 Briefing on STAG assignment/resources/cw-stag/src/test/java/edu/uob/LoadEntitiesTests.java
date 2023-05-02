package edu.uob;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LoadEntitiesTests {
    private GameState extendedGameState;
    private ExtendedEntitiesHelper extendedEntitiesHelper;

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
        this.extendedEntitiesHelper = new ExtendedEntitiesHelper();
    }

    @Test
    void testLoadExtendedEntitiesFile() {
        Location storeroom = this.extendedGameState.getStoreroom();
        assertNotNull(storeroom);
        Set<String> allLocationNames = this.extendedEntitiesHelper.getLocationNames();
        for (String locationName : allLocationNames) {
            Location location = this.extendedGameState.getLocationByName(locationName);
            assertNotNull(location);
            assertEquals(location, this.extendedGameState.getEntityByName(locationName));
            if (locationName.equals("storeroom")) {
                assertEquals(location, storeroom);
            } else {
                assertNull(location.getDestinationByName(locationName));
            }
            if (locationName.equals("cabin")) {
                assertEquals(location, this.extendedGameState.getStartLocation());
            }
            this.testEntityLoaded(location, locationName);
            Set<String> possibleDestinations = this.extendedEntitiesHelper.getPossiblePathFromLocation(locationName);
            this.testHasPathTo(location, possibleDestinations, allLocationNames);
        }
    }

    private void testEntityLoaded(Location location, String locationName) {
        String[] informationType = {"artefacts", "furniture", "characters"};
        for (int i = 0; i < informationType.length; i++) {
            String[] entityNames = this.extendedEntitiesHelper.getEntityNamesInLocation(locationName, informationType[i]);
            for (String entityName : entityNames) {
                GameEntity entity = this.extendedGameState.getEntityByName(entityName);
                assertEquals(location, entity.getCurrentLocation());
                if (i == 0) {
                    Artefact artefact = location.getArtefactByName(entityName);
                    assertNotNull(artefact);
                    assertEquals(entity, artefact);
                } else if (i == 1) {
                    Furniture furniture = location.getFurnitureByName(entityName);
                    assertNotNull(furniture);
                    assertEquals(entity, furniture);
                } else {
                    Character character = location.getCharacterByName(entityName);
                    assertNotNull(character);
                    assertEquals(entity, character);
                }
            }
        }
    }

    private void testHasPathTo(Location location, Set<String> possibleDestinations, Set<String> allLocations) {
        for (String locationName : allLocations) {
            Location destination = location.getDestinationByName(locationName);
            if (possibleDestinations.contains(locationName)) {
                assertNotNull(destination);
                assertEquals(destination, this.extendedGameState.getLocationByName(locationName));
            } else {
                assertNull(destination);
            }
        }
    }
}
