package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class LoadActionsTests {
    private GameState extendedGameState;
    private ExtendedActionsHelper extendedActionsHelper;

    @BeforeEach
    void setUp() {
        this.extendedGameState = new GameState();
        String fileName = "extended-actions.xml";
        File file = Paths.get("config" + File.separator + fileName).toAbsolutePath().toFile();
        ActionsLoader actionsLoader = new ActionsLoader(extendedGameState, file);
        try {
            actionsLoader.loadActions();
        } catch (ParserConfigurationException parserConfigurationException) {
            fail("ParserConfigurationException was thrown when attempting to read " + fileName);
        } catch (IOException ioException) {
            fail("IOException was thrown when attempting to read " + fileName);
        } catch (SAXException saxException) {
            fail("SAXException was thrown when attempting to read " + fileName);
        }
        this.extendedActionsHelper = new ExtendedActionsHelper();
    }

    @Test
    void testLoadExtendedActionsFile() {
        for (int i = 0; i < this.extendedActionsHelper.getActionsCount(); i++) {
            String[] triggers = this.extendedActionsHelper.getActionAttribute(i, "triggers");
            GameAction action = this.testTriggersLoadedAndGetAction(triggers);
            String[] subjects = this.extendedActionsHelper.getActionAttribute(i, "subjects");
            this.testSubjectsLoaded(action, subjects);
            String[] consumed = this.extendedActionsHelper.getActionAttribute(i, "consumed");
            this.testConsumedLoaded(action, consumed);
            String[] produced = this.extendedActionsHelper.getActionAttribute(i, "produced");
            this.testProducedLoaded(action, produced);
            String[] narration = this.extendedActionsHelper.getActionAttribute(i, "narration");
            this.testNarrationLoaded(action, narration);
        }
    }

    private GameAction testTriggersLoadedAndGetAction(String[] triggers) {
        assertTrue(triggers.length >= 1);
        GameAction firstActionMatchedWithFirstTrigger = (GameAction) this.extendedGameState.getPossibleActions(triggers[0]).toArray()[0];
        for (String trigger : triggers) {
            HashSet<GameAction> possibleActions = this.extendedGameState.getPossibleActions(trigger);
            assertNotNull(possibleActions);
            assertEquals(1, possibleActions.size());
            GameAction actionMatched = (GameAction) possibleActions.toArray()[0];
            assertEquals(firstActionMatchedWithFirstTrigger, actionMatched);
        }
        return firstActionMatchedWithFirstTrigger;
    }

    private void testSubjectsLoaded(GameAction gameAction, String[] subjects) {
        assertTrue(subjects.length >= 1);
        for (String subject : subjects) {
            assertTrue(gameAction.isSubjectEntityName(subject));
        }
    }

    private void testConsumedLoaded(GameAction gameAction, String[] consumed) {
        for (String c : consumed) {
            assertTrue(gameAction.isConsumedEntityName(c));
        }
    }

    private void testProducedLoaded(GameAction gameAction, String[] produced) {
        for (String p : produced) {
            assertTrue(gameAction.isProducedEntityName(p));
        }
    }

    private void testNarrationLoaded(GameAction gameAction, String[] narration) {
        assertEquals(1, narration.length);
        assertEquals(narration[0], gameAction.getNarration());
    }

}
