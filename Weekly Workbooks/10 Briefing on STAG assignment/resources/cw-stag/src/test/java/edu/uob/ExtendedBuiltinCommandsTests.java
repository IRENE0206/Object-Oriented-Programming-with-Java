package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static edu.uob.ResponseExamineHelper.*;

public class ExtendedBuiltinCommandsTests {
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
    public void testExtendedBuiltinCommands() {
        String[] locations = {"cabin", "forest", "riverbank"};
        String[] artefacts0 = {"potion", "axe", "coin"};
        String[] furniture0 = {"trapdoor"};
        List<String> path0 = new ArrayList<>();
        path0.add(locations[0]);
        path0.add(locations[1]);

        testGetAndDropArtefacts(path0, artefacts0);
        testFailToGetWrongElements(furniture0);

        sendCommandToServer("simon: goto forest");
        String[] artefacts1 = {"key"};
        String[] furniture1 = {"tree"};
        List<String> path1 = new ArrayList<>();
        path1.add(locations[1]);
        path1.add(locations[0]);
        path1.add(locations[2]);
        testGetAndDropArtefacts(path1, artefacts1);
        testFailToGetWrongElements(furniture1);

        sendCommandToServer("simon: goto riverbank");
        String[] artefacts2 = {"horn"};
        String[] furniture2 = {"river"};
        List<String> path2 = new ArrayList<>();
        path2.add(locations[2]);
        path2.add(locations[1]);
        testGetAndDropArtefacts(path2, artefacts2);
        testFailToGetWrongElements(furniture2);
    }

    private void testGetAndDropArtefacts(List<String> path, String[] artefacts) {
        String response = sendCommandToServer("simon: look").toLowerCase();
        for (String location : path) {
            assertTrue(response.contains(location.toLowerCase()));
        }
        testResponseContains(response, artefacts);
        response = sendCommandToServer("simon: inventory").toLowerCase();
        testResponseNotContains(response, artefacts);
        for (String artefact : artefacts) {
            sendCommandToServer("simon: get " + artefact);
            response = sendCommandToServer("simon: look").toLowerCase();
            assertFalse(response.contains(artefact.toLowerCase()));
            response = sendCommandToServer("simon: inventory").toLowerCase();
            System.out.println(response);
            assertTrue(response.contains(artefact.toLowerCase()));
            sendCommandToServer("simon: drop " + artefact);
            response = sendCommandToServer("simon: look").toLowerCase();
            assertTrue(response.contains(artefact.toLowerCase()));
            response = sendCommandToServer("simon: inventory").toLowerCase();
            assertFalse(response.contains(artefact.toLowerCase()));
        }
    }

    private void testFailToGetWrongElements(String[] elements) {
        for (String element : elements) {
            String response = sendCommandToServer("simon: look").toLowerCase();
            assertTrue(response.contains(element.toLowerCase()));
            response = sendCommandToServer("simon: inventory").toLowerCase();
            assertFalse(response.contains(element.toLowerCase()));
            sendCommandToServer("simon: get " + element);
            response = sendCommandToServer("simon: look");
            assertTrue(response.contains(element.toLowerCase()));
            response = sendCommandToServer("simon: inventory").toLowerCase();
            assertFalse(response.contains(element.toLowerCase()));
        }
    }
}


