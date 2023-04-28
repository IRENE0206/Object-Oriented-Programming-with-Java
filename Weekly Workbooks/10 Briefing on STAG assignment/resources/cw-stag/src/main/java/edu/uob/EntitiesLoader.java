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
                putEntitiesInLocation(locationSubGraph, l);
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
            fromLocation.addPathToLocation(toLocation);
        }
    }

    private void putEntitiesInLocation(Graph graph, Location location) {
        for (Node node : graph.getNodes(false)) {
            String entityType = getEntityType(graph);
            String entityName = getEntityName(node);
            String entityDescription = getEntityDescription(node);
            if (entityName.isEmpty()) {
                continue;
            }
            if (entityType.equalsIgnoreCase("characters")) {
                location.addCharacter(new Character(entityName, entityDescription));
            } else if (entityType.equalsIgnoreCase("artefacts")) {
                location.addArtefact(new Artefact(entityName, entityDescription));
            } else if (entityType.equalsIgnoreCase("furniture")) {
                location.addFurniture(new Furniture(entityName, entityDescription));
            }
        }
    }

    private String getEntityName(Node node) {
        return node.getId().getId();
    }

    private String getEntityDescription(Node node) {
        return node.getAttribute("description");
    }

    private String getEntityType(Graph graph) {
        return graph.getId().getId();
    }

}
