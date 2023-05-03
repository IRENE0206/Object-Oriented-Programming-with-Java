package edu.uob;

import java.util.*;

public final class ExtendedEntitiesHelper {
    private static final Map<String, Map<String, HashSet<String>>> locations;
    private static final Map<String, String> descriptions;
    private static final Map<String, Set<String>> paths;

    static {
        locations = new HashMap<>();
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

        paths = new HashMap<>();
        HashSet<String> cellarPaths = new HashSet<>();
        cellarPaths.add(locationNames[2]);
        paths.put(locationNames[1], cellarPaths);
        HashSet<String> cabinPaths = new HashSet<>();
        cabinPaths.add(locationNames[3]);
        paths.put(locationNames[2], cabinPaths);
        HashSet<String> forestPaths = new HashSet<>();
        forestPaths.add(locationNames[2]);
        forestPaths.add(locationNames[4]);
        paths.put(locationNames[3], forestPaths);
        HashSet<String> riverbankPaths = new HashSet<>();
        riverbankPaths.add(locationNames[3]);
        paths.put(locationNames[4], riverbankPaths);
        HashSet<String> clearingPath = new HashSet<>();
        clearingPath.add(locationNames[4]);
        paths.put(locationNames[5], clearingPath);

        descriptions = new HashMap<>();
        descriptions.put(locationNames[0], "Storage for any entities not placed in the game");
        descriptions.put(locationNames[1], "A dusty cellar");
        descriptions.put(locationNames[2], "A log cabin in the woods");
        descriptions.put(locationNames[3], "A deep dark forest");
        descriptions.put(locationNames[4], "A grassy riverbank");
        descriptions.put(locationNames[5], "A clearing in the woods");
        descriptions.put(artefacts0[0], "A big pot of gold");
        descriptions.put(artefacts0[1], "A sturdy shovel");
        descriptions.put(artefacts0[2], "A heavy wooden log");
        descriptions.put(furniture0[0], "A deep hole in the ground");
        descriptions.put(characters0[0], "A burly wood cutter");
        descriptions.put(characters1[0], "An angry looking Elf");
        descriptions.put(artefacts2[0], "A bottle of magic potion");
        descriptions.put(artefacts2[1], "A razor sharp axe");
        descriptions.put(artefacts2[2], "A silver coin");
        descriptions.put(furniture2[0], "A locked wooden trapdoor in the floor");
        descriptions.put(artefacts3[0], "A rusty old key");
        descriptions.put(furniture3[0], "A tall pine tree");
        descriptions.put(artefacts4[0], "An old brass horn");
        descriptions.put(furniture4[0], "A fast flowing river");
        descriptions.put(furniture5[0], "It looks like the soil has been recently disturbed");
    }

    public static Set<String> getLocationNames() {
        return locations.keySet();
    }

    public static Set<String> getPossiblePathFromLocation(String locationName) {
        return paths.get(locationName);
    }

    public static Set<String> getEntityNamesInLocation(String locationName, String entityType) {
        return locations.get(locationName).get(entityType);
    }

    private static void populateLocations(String locationName, String[] artefacts, String[] furniture, String[] characters) {
        HashMap<String, HashSet<String>> location = new HashMap<>();
        location.put("artefacts", new HashSet<>(Arrays.asList(artefacts)));
        location.put("furniture", new HashSet<>(Arrays.asList(furniture)));
        location.put("characters",new HashSet<>(Arrays.asList(characters)));
        locations.put(locationName, location);
    }

    public static Set<String> getArtefactNamesAtLocation(String locationName) {
        return getEntityNamesInLocation(locationName, "artefacts");
    }

    public static Set<String> getFurnitureNamesAtLocation(String locationName) {
        return getEntityNamesInLocation(locationName, "furniture");
    }

    public static Set<String> getCharacterNamesAtLocation(String locationName) {
        return getEntityNamesInLocation(locationName, "characters");
    }

    public static Set<String> getAllEntityNames() {
        Set<String> allEntityNames = new HashSet<>();
        for (String locationName : getLocationNames()) {
            allEntityNames.add(locationName.toLowerCase());
            allEntityNames.addAll(getArtefactNamesAtLocation(locationName));
            allEntityNames.addAll(getFurnitureNamesAtLocation(locationName));
            allEntityNames.addAll(getCharacterNamesAtLocation(locationName));
        }
        return allEntityNames;
    }

    public static String getDescriptionOfEntity(String entityName) {
        return descriptions.get(entityName);
    }
}
