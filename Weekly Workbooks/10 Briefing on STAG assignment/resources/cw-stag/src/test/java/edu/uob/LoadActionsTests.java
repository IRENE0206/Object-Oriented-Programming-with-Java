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
    private GameState basicGameState;
    private GameState extendedGameState;
    @BeforeEach
    void setUp() {
        basicGameState = loadFile("basic-actions.xml");
        extendedGameState = loadFile("extended-actions.xml");
    }

    private GameState loadFile(String fileName) {
        File file = Paths.get("config" + File.separator + fileName).toAbsolutePath().toFile();
        GameState gameState = new GameState();
        ActionsLoader actionsLoader = new ActionsLoader(gameState, file);
        try {
            actionsLoader.loadActions();
        } catch (ParserConfigurationException parserConfigurationException) {
            fail("ParserConfigurationException was thrown when attempting to read " + fileName);
        } catch (IOException ioException) {
            fail("IOException was thrown when attempting to read " + fileName);
        } catch (SAXException saxException) {
            fail("SAXException was thrown when attempting to read " + fileName);
        }
        return gameState;
    }

    @Test
    void testLoadBasicActionsFile() {
        String[] triggerPhrases0 = {"open", "unlock"};
        String[] subjects0 = {"trapdoor", "key"};
        String[] consumed0 = {"key"};
        String[] produced0 = {"cellar"};
        GameAction gameAction0 = triggerPhrasesLoadedBasic(triggerPhrases0);
        subjectsLoaded(gameAction0, subjects0);
        consumedLoaded(gameAction0, consumed0);
        producedLoaded(gameAction0, produced0);
        String[] triggerPhrases1 = {"chop", "cut", "cutDown"};
        String[] subjects1 = {"tree", "axe"};
        String[] consumed1 = {"tree"};
        String[] produced1 = {"log"};
        GameAction gameAction1 = triggerPhrasesLoadedBasic(triggerPhrases1);
        subjectsLoaded(gameAction1, subjects1);
        consumedLoaded(gameAction1, consumed1);
        producedLoaded(gameAction1, produced1);
        String[] triggerPhrases2 = {"drink"};
        String[] subjects2 = {"potion"};
        String[] consumed2 = {"potion"};
        String[] produced2 = {"health"};
        GameAction gameAction2 = triggerPhrasesLoadedBasic(triggerPhrases2);
        subjectsLoaded(gameAction2, subjects2);
        consumedLoaded(gameAction2, consumed2);
        producedLoaded(gameAction2, produced2);
        String[] triggerPhrases3 = {"fight", "hit", "attack"};
        String[] subjects3 = {"elf"};
        String[] consumed3 = {"health"};
        GameAction gameAction3 = triggerPhrasesLoadedBasic(triggerPhrases3);
        subjectsLoaded(gameAction3, subjects3);
        consumedLoaded(gameAction3, consumed3);
    }

    private GameAction triggerPhrasesLoadedBasic(String[] triggerPhrases) {
        GameAction gameAction = null;
        for (int i = 0; i < triggerPhrases.length; i++) {
            String phrase = triggerPhrases[i].toLowerCase();
            assertTrue(this.basicGameState.hasTriggerPhrase(phrase));
            HashSet<GameAction> actions = this.basicGameState.getPossibleActions(phrase);
            assertEquals(1, actions.size());
            if (i == 0) {
                gameAction = (GameAction) actions.toArray()[0];
            } else {
                GameAction a = (GameAction) actions.toArray()[0];
                assertSame(gameAction, a);
            }
        }
        assertNotNull(gameAction);
        return gameAction;
    }

    private void subjectsLoaded(GameAction gameAction, String[] subjects) {
        for (String subject : subjects) {
            assertTrue(gameAction.hasSubjectEntity(subject));
        }
    }

    private void consumedLoaded(GameAction gameAction, String[] consumed) {
        for (String c : consumed) {
            assertTrue(gameAction.hasConsumedEntity(c));
        }
    }

    private void producedLoaded(GameAction gameAction, String[] produced) {
        for (String p : produced) {
            assertTrue(gameAction.hasProducedEntity(p));
        }
    }

    @Test
    void testLoadExtendedActionsFile() {
        assertTrue(this.extendedGameState.hasTriggerPhrase("cut down"));
        assertFalse(this.extendedGameState.hasTriggerPhrase("cutDown"));
        GameAction payAction = (GameAction) this.extendedGameState.getPossibleActions("pay").toArray()[0];
        String[] subjects0 = {"elf", "coin"};
        subjectsLoaded(payAction, subjects0);
        String[] consumed0 = {"coin"};
        consumedLoaded(payAction, consumed0);
        String[] produced0 = {"shovel"};
        producedLoaded(payAction, produced0);
        GameAction blowAction = (GameAction) this.extendedGameState.getPossibleActions("blow").toArray()[0];
        String[] subjects1 = {"horn"};
        subjectsLoaded(blowAction, subjects1);
        String[] produced1 = {"lumberjack"};
        producedLoaded(blowAction, produced1);
        GameAction bridgeAction = (GameAction) this.extendedGameState.getPossibleActions("bridge").toArray()[0];
        String[] subjects2 = {"log", "river"};
        subjectsLoaded(bridgeAction, subjects2);
        String[] consumed2 = {"log"};
        consumedLoaded(bridgeAction, consumed2);
        String[] produced2 = {"clearing"};
        producedLoaded(bridgeAction, produced2);
        GameAction digAction = (GameAction) this.extendedGameState.getPossibleActions("dig").toArray()[0];
        String[] subjects3 = {"ground", "shovel"};
        subjectsLoaded(digAction, subjects3);
        String[] consumed3 = {"ground"};
        consumedLoaded(digAction, consumed3);
        String[] produced3 = {"hole", "gold"};
        producedLoaded(digAction, produced3);
    }


}
