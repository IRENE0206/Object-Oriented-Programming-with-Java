package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class MorePlayersTests {
    private OXOModel model;
    private OXOController controller;

    @BeforeEach
    void setup() {
        model = new OXOModel(4, 4, 4);
        model.addPlayer(new OXOPlayer('A'));
        model.addPlayer(new OXOPlayer('B'));
        model.addPlayer(new OXOPlayer('C'));
        controller = new OXOController(model);
    }

    void sendCommandToController(String command) {
        // Try to send a command to the server - call will time out if it takes too long (in case the server enters an infinite loop)
        // Note: this is ugly code and includes syntax that you haven't encountered yet
        String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
    }

    @Test
    void testPlayerNumber() {
        String failedTestComment0 = "The number of players should be ";
        String failedTestComment1 = "Current player should be player ";
        String failedTestComment2 = "Cell owner should be player ";
        int playerNum0 = model.getNumberOfPlayers();
        assertEquals(3, playerNum0, failedTestComment0 + "3 not " + playerNum0);
        sendCommandToController("a1");
        sendCommandToController("a2");
        int playerNum1 = model.getCurrentPlayerNumber();
        assertEquals(2, playerNum1, failedTestComment1 + "2 not " + playerNum1);
        char letter0 = model.getPlayerByNumber(playerNum1).getPlayingLetter();
        assertEquals('C', letter0, failedTestComment1 + "'C' not " + letter0);
        sendCommandToController("a3");
        char letter1 = model.getCellOwner(0, 2).getPlayingLetter();
        assertEquals(letter0, letter1, failedTestComment2 + letter0 + " not " + letter1);
        int playerNum2 = model.getCurrentPlayerNumber();
        assertEquals(0, playerNum2, failedTestComment1 + "0 not " + playerNum2);
        char letter2 = model.getPlayerByNumber(playerNum2).getPlayingLetter();
        assertEquals('A', letter2, failedTestComment1 + "'A' not " + letter2);
    }

    @Test
    void testWinDetection() {
        String failedTestComment = "Winner should be player ";
        sendCommandToController("a1");
        sendCommandToController("b1");
        sendCommandToController("c1");
        sendCommandToController("a2");
        sendCommandToController("b2");
        sendCommandToController("c2");
        sendCommandToController("a3");
        sendCommandToController("b3");
        sendCommandToController("c3");
        sendCommandToController("a4");
    }
}
