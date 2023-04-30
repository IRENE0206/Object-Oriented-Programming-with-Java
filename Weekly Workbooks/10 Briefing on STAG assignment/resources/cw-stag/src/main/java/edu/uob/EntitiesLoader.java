package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

// all entity files used during marking are in a valid format
public class EntitiesLoader {
    private final GameState gameState;
    private final File entitiesFile;

    public EntitiesLoader(GameState gameState, File entitiesFile) {
        this.gameState = gameState;
        this.entitiesFile = entitiesFile;
    }

    public void loadEntities() throws FileNotFoundException, ParseException {
        Parser parser = new Parser();
        FileReader reader = new FileReader(this.entitiesFile);
        parser.parse(reader);
        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> sections = wholeDocument.getSubgraphs();
        ArrayList<Graph> locations = sections.get(0).getSubgraphs();
        // locations subgraph will always be first in the entities file
        for (int i = 0; i < locations.size(); i++) {
            Graph location = locations.get(i);
            Node node = location.getNodes(false).get(0);
            String locationName = getEntityName(node);
            String locationDescription = getEntityDescription(node);
            if (locationName.isEmpty()) {
                continue;
            }
            Location l = new Location(locationName, locationDescription);
            ArrayList<Graph> locationSubGraphs = location.getSubgraphs();
            for (Graph locationSubGraph : locationSubGraphs) {
                putSubjectsIntoLocation(locationSubGraph, l);
            }
            if (i == 0) {
                // the start location can be called anything we like,
                // however it will always be the first location that appears in the "entities" file.
                this.gameState.setStartLocation(l);
            } else if (l.getName().equalsIgnoreCase("storeroom")){
                this.gameState.setStoreroom(l);
            } else {
                this.gameState.addLocation(l);
            }
        }
        // paths subgraph will always appear after the locations
        ArrayList<Edge> paths = sections.get(1).getEdges();
        addPaths(paths);
    }

    private void addPaths(ArrayList<Edge> paths) {
        for (Edge path : paths) {
            Node fromNode = path.getSource().getNode();
            String fromName = getEntityName(fromNode);
            if (fromName.isEmpty()) {
                continue;
            }
            Location fromLocation = this.gameState.getLocationByName(fromName);
            Node toNode = path.getTarget().getNode();
            String toName = getEntityName(toNode);
            if (toName.isEmpty()) {
                continue;
            }
            Location toLocation = this.gameState.getLocationByName(toName);
            fromLocation.addEntity(toLocation);
        }
    }

    private void putSubjectsIntoLocation(Graph graph, Location location) {
        for (Node node : graph.getNodes(false)) {
            String entityType = getEntityType(graph);
            String entityName = getEntityName(node);
            String entityDescription = getEntityDescription(node);
            if (entityName.isEmpty()) {
                continue;
            }
            if (entityType.equalsIgnoreCase("characters")) {
                Character character = new Character(entityName, entityDescription);
                location.addEntity(character);
                this.gameState.addEntity(character);
            } else if (entityType.equalsIgnoreCase("artefacts")) {
                Artefact artefact = new Artefact(entityName, entityDescription);
                location.addEntity(artefact);
                this.gameState.addEntity(artefact);
            } else if (entityType.equalsIgnoreCase("furniture")) {
                Furniture furniture = new Furniture(entityName, entityDescription);
                location.addEntity(furniture);
                this.gameState.addEntity(furniture);
            }
        }
    }

    private String getEntityName(Node node) {
        return node.getId().getId().toLowerCase();
    }

    private String getEntityDescription(Node node) {
        return node.getAttribute("description");
    }

    private String getEntityType(Graph graph) {
        return graph.getId().getId().toLowerCase();
    }

}
