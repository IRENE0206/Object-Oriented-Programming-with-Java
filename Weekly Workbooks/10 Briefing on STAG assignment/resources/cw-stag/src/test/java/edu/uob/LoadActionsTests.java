package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static edu.uob.ExtendedActionsHelper.*;

public class LoadActionsTests {
    private GameState extendedGameState;

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
    }

    @Test
    void testLoadExtendedActionsFile() {
        int actionCount = getActionsCount();
        for (int i = 0; i < actionCount; i++) {
            Set<String> triggers = getActionTriggers(i);
            if (i == actionCount - 2) {
                assertEquals(1, triggers.size(), "The second last action only has one trigger 'kill'");
                continue;
            }
            if (i == actionCount - 1) {
                assertTrue(triggers.size() > 1 && triggers.remove("kill"), "The last action added has 'kill' as one of its two triggers");
                HashSet<GameAction> possibleActions = this.extendedGameState.getPossibleActions("kill");
                assertEquals(2, possibleActions.size(), "There should be two actions defined with 'kill' as one of their triggers");
            }
            GameAction action = this.testTriggersLoadedAndGetAction(triggers);
            Set<String> subjects = getActionSubjects(i);
            this.testSubjectsLoaded(action, subjects);
            Set<String> consumed = getActionConsumed(i);
            this.testConsumedLoaded(action, consumed);
            Set<String> produced = getActionProduced(i);
            this.testProducedLoaded(action, produced);
            String narration = getActionNarration(i);
            this.testNarrationLoaded(action, narration);
        }

    }

    private GameAction testTriggersLoadedAndGetAction(Set<String> triggers) {
        assertTrue(triggers.size() >= 1, "Triggers cannot be empty");
        GameAction firstActionMatchedWithFirstTrigger = null;
        for (String trigger : triggers) {
            HashSet<GameAction> possibleActions = this.extendedGameState.getPossibleActions(trigger);
            assertNotNull(possibleActions, "Each trigger should match with at least one action");
            assertEquals(1, possibleActions.size(), "Apart from the last two actions added, each trigger matches with one action");
            GameAction actionMatched = (GameAction) possibleActions.toArray()[0];
            if (firstActionMatchedWithFirstTrigger == null) {
                firstActionMatchedWithFirstTrigger = actionMatched;
            } else {
                assertEquals(firstActionMatchedWithFirstTrigger, actionMatched, "Given triggers should match with the same action");
            }
        }
        return firstActionMatchedWithFirstTrigger;
    }

    private void testSubjectsLoaded(GameAction gameAction, Set<String> subjects) {
        assertTrue(subjects.size() >= 1, "Each action should have at least one subject");
        for (String subject : subjects) {
            assertTrue(gameAction.isSubjectEntityName(subject), subject + " should be loaded into the action");
        }
    }

    private void testConsumedLoaded(GameAction gameAction, Set<String> consumed) {
        for (String c : consumed) {
            assertTrue(gameAction.isConsumedEntityName(c), c + " should be loaded into the action");
        }
    }

    private void testProducedLoaded(GameAction gameAction, Set<String> produced) {
        for (String p : produced) {
            assertTrue(gameAction.isProducedEntityName(p), p + " should be loaded into the action");
        }
    }

    private void testNarrationLoaded(GameAction gameAction, String narration) {
        assertEquals(narration, gameAction.getNarration(), "Narration should be loaded into the action");
    }

}
