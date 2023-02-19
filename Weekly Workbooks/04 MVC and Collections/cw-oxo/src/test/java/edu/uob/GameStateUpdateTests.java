package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class GameStateUpdateTests {
    private OXOModel model;
    private OXOController controller;

    @BeforeEach
    void setup() {
        model = new OXOModel(5, 5, 5);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
    }

    void sendCommandToController(String command) {
        String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
    }

    @Test
    void testInitialState() {
        int playerNumbers = model.getNumberOfPlayers();
        assertEquals(2, playerNumbers, "There should be 2 players but got " + playerNumbers);
        assertFalse(model.isGameDrawn(), "Game didn't start. No drawn yet");
        int currPlayerNumber = model.getCurrentPlayerNumber();
        assertEquals(0, currPlayerNumber, "The player ready to move should be player 0 but got " + currPlayerNumber);
        int rowNumber = model.getNumberOfRows();
        assertEquals(5, rowNumber, "There should be 4 rows but got " + rowNumber);
        int colNumber = model.getNumberOfColumns();
        assertEquals(5, colNumber, "There should be 4 cols but got " + colNumber);
        int threshold = model.getWinThreshold();
        assertEquals(5, threshold, "The threshold should be 4 but got " + threshold);
        char playerLetter = model.getPlayerByNumber(0).getPlayingLetter();
        assertEquals('X', playerLetter, "The first player should be X but got " + playerLetter);
    }

    @Test
    void testSwitchUser() {
        String failedTestComment = "Current player should be player ";
        sendCommandToController("c2");
        int p1 = model.getCurrentPlayerNumber();
        assertEquals(1, p1, failedTestComment + "1" + " not " + p1);
        sendCommandToController("b4");
        int p2 = model.getCurrentPlayerNumber();
        assertEquals(0, p2, failedTestComment + "0" + " not " + p2);
        sendCommandToController("d2");
        int p3 = model.getCurrentPlayerNumber();
        assertEquals(1, p3, failedTestComment + "1" + " not " + p3);
        sendCommandToController("c1");
        int p4 = model.getCurrentPlayerNumber();
        assertEquals(0, p4, failedTestComment + "0" + " not " + p4);

    }

    @Test
    void testHorizontalWinDetection() {
        String failedTestComment = "Winner should be player ";
        sendCommandToController("a1");
        sendCommandToController("b1");
        sendCommandToController("a2");
        sendCommandToController("b2");
        sendCommandToController("a3");
        sendCommandToController("b3");
        sendCommandToController("a4");
        sendCommandToController("b4");
        sendCommandToController("a5");
        OXOPlayer winner = model.getWinner();
        char winnerLetter = winner.getPlayingLetter();
        int currPlayer = model.getCurrentPlayerNumber();
        assertEquals('X', winnerLetter, failedTestComment + "X not " + winnerLetter);
        assertEquals(1, currPlayer, "The next player should be player 1 not " + currPlayer);
        sendCommandToController("b5");
        int nextPlayer = model.getCurrentPlayerNumber();
        assertEquals(currPlayer, nextPlayer, "After a winner is detected, game doesn't exit but stops");
        OXOPlayer currWinner = model.getWinner();
        char currWinnerLetter = currWinner.getPlayingLetter();
        assertEquals(winnerLetter, currWinnerLetter, "Winner should stay the same");
    }

    @Test
    void testVerticalWinDetection() {
        String failedTestComment = "Winner should be player ";
        sendCommandToController("a4");
        sendCommandToController("a1");
        sendCommandToController("b4");
        sendCommandToController("b1");
        sendCommandToController("c4");
        sendCommandToController("c1");
        sendCommandToController("d4");
        sendCommandToController("d1");
        sendCommandToController("e4");
        OXOPlayer winner = model.getWinner();
        char winnerLetter = winner.getPlayingLetter();
        int currPlayer = model.getCurrentPlayerNumber();
        assertEquals('X', winnerLetter, failedTestComment + "X not " + winnerLetter);
        assertEquals(1, currPlayer, "The next player should be player 1 not " + currPlayer);
        sendCommandToController("e1");
        int nextPlayer = model.getCurrentPlayerNumber();
        assertEquals(currPlayer, nextPlayer, "After a winner is detected, game doesn't exit but stops");
        OXOPlayer currWinner = model.getWinner();
        char currWinnerLetter = currWinner.getPlayingLetter();
        assertEquals(winnerLetter, currWinnerLetter, "Winner should stay the same");
    }

    @Test
    // TODO
    void testWinDetection() {
        String failedTestComment = "Winner should be player ";
        sendCommandToController("a4");
        sendCommandToController("a1");
        sendCommandToController("b4");
        sendCommandToController("b1");
        sendCommandToController("c4");
        sendCommandToController("c1");
        sendCommandToController("d4");
        sendCommandToController("d1");
        sendCommandToController("e4");
        OXOPlayer winner = model.getWinner();
        char winnerLetter = winner.getPlayingLetter();
        int currPlayer = model.getCurrentPlayerNumber();
        assertEquals('X', winnerLetter, failedTestComment + "X not " + winnerLetter);
        assertEquals(1, currPlayer, "The next player should be player 1 not " + currPlayer);
        sendCommandToController("e1");
        int nextPlayer = model.getCurrentPlayerNumber();
        assertEquals(currPlayer, nextPlayer, "After a winner is detected, game doesn't exit but stops");
        OXOPlayer currWinner = model.getWinner();
        char currWinnerLetter = currWinner.getPlayingLetter();
        assertEquals(winnerLetter, currWinnerLetter, "Winner should stay the same");
    }
}
