package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

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
        String failedTestComment0 = "Winner should be player ";
        String failedTestComment1 = "Current player should be player ";
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
        OXOPlayer winner = model.getWinner();
        char letter0 = winner.getPlayingLetter();
        assertEquals('A', letter0, failedTestComment0 + 'A');
        int player0 = model.getCurrentPlayerNumber();
        assertEquals(1, player0, failedTestComment1 + 1);
    }
}
