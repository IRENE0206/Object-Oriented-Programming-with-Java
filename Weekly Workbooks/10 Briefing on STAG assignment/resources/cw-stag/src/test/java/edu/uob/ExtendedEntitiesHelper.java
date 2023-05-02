package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ExtendedEntitiesHelper {
    private final HashMap<String, HashMap<String, String[]>> locations;
    private final HashMap<String, HashSet<String>> paths;

    public ExtendedEntitiesHelper() {
        this.locations = new HashMap<>();
        String[] locationNames = {"storeroom", "cellar", "cabin", "forest", "riverbank", "clearing"};
        String[] artefacts0 = {"gold", "shovel", "log"};
        String[] furniture0 = {"hole"};
        String[] characters0 = {"lumberjack"};
        populateLocations(locationNames[0], artefacts0, furniture0, characters0);
        String[] artefacts1 = {};
        String[] furniture1 = {};
        String[] characters1 = {"elf"};
        populateLocations(locationNames[1], artefacts1, furniture1, characters1);
        String[] artefacts2 = {"potion", "axe", "coin"};
        String[] furniture2 = {"trapdoor"};
        String[] characters2 = {};
        populateLocations(locationNames[2], artefacts2, furniture2, characters2);
        String[] artefacts3 = {"key"};
        String[] furniture3 = {"tree"};
        String[] characters3 = {};
        populateLocations(locationNames[3], artefacts3, furniture3, characters3);
        String[] artefacts4 = {"horn"};
        String[] furniture4 = {"river"};
        String[] characters4 = {};
        populateLocations(locationNames[4], artefacts4, furniture4, characters4);
        String[] artefacts5 = {};
        String[] furniture5 = {"ground"};
        String[] characters5 = {};
        populateLocations(locationNames[5], artefacts5, furniture5, characters5);
        this.paths = new HashMap<>();
        HashSet<String> cellarPaths = new HashSet<>();
        cellarPaths.add(locationNames[2]);
        this.paths.put(locationNames[1], cellarPaths);
        HashSet<String> cabinPaths = new HashSet<>();
        cabinPaths.add(locationNames[3]);
        this.paths.put(locationNames[2], cabinPaths);
        HashSet<String> forestPaths = new HashSet<>();
        forestPaths.add(locationNames[2]);
        forestPaths.add(locationNames[4]);
        this.paths.put(locationNames[3], forestPaths);
        HashSet<String> riverbankPaths = new HashSet<>();
        riverbankPaths.add(locationNames[3]);
        this.paths.put(locationNames[4], riverbankPaths);
        HashSet<String> clearingPath = new HashSet<>();
        clearingPath.add(locationNames[4]);
        this.paths.put(locationNames[5], clearingPath);
    }

    public Set<String> getLocationNames() {
        return this.paths.keySet();
    }

    public Set<String> getPossiblePathFromLocation(String locationName) {
        return this.paths.get(locationName);
    }

    public String[] getEntityNamesInLocation(String locationName, String entityType) {
        return this.locations.get(locationName).get(entityType);
    }

    private void populateLocations(String locationName, String[] artefacts, String[] furniture, String[] characters) {
        HashMap<String, String[]> location = new HashMap<>();
        location.put("artefacts", artefacts);
        location.put("furniture", furniture);
        location.put("characters", characters);
        this.locations.put(locationName, location);
    }

}
