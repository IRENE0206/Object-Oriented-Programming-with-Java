package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

public class ChangeGameSettingTests {
    private OXOModel model;
    private OXOController controller;

    @BeforeEach
    void setup() {
        model = new OXOModel(3, 3, 3);
        model.addPlayer(new OXOPlayer('0'));
        model.addPlayer(new OXOPlayer('X'));
        controller = new OXOController(model);
    }

    void sendCommandToController(String command) {
        String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
        assertTimeoutPreemptively(Duration.ofMillis(1000), () -> controller.handleIncomingCommand(command), timeoutComment);
    }

    @Test
    void testGameDrawn() {
        sendCommandToController("a1");
        sendCommandToController("a2");
        sendCommandToController("b2");
        sendCommandToController("c3");
        sendCommandToController("b1");
        sendCommandToController("c1");
        sendCommandToController("a3");
        sendCommandToController("b3");
        sendCommandToController("c2");
        assertTrue(model.isGameDrawn(), "Game should be drawn");
        String failedTestComment0 = "The grid row number should be ";
        String failedTestComment1 = "The grid col number should be ";
        controller.addRow();
        controller.addColumn();
        int rowNum0 = model.getNumberOfRows();
        assertEquals(4, rowNum0, failedTestComment0 + "2 not " + rowNum0);
        int colNum0 = model.getNumberOfColumns();
        assertEquals(4, colNum0, failedTestComment1 + "2 not " + colNum0);
    }

    @Test
    void testChangeThreshold() {
        String failedTestComment = "Threshold should be ";
        int threshold0 = model.getWinThreshold();
        assertEquals(3, threshold0, failedTestComment + "3 not " + threshold0);
        controller.decreaseWinThreshold();
        int threshold1 = model.getWinThreshold();
        assertEquals(3, threshold1, failedTestComment + "3 not " + threshold1);
        controller.increaseWinThreshold();
        int threshold2 = model.getWinThreshold();
        assertEquals(4, threshold2, failedTestComment + "4 not " + threshold2);
        controller.increaseWinThreshold();
        int threshold3 = model.getWinThreshold();
        assertEquals(5, threshold3, failedTestComment + "5 not " + threshold3);
        controller.decreaseWinThreshold();
        int threshold4 = model.getWinThreshold();
        assertEquals(4, threshold4, failedTestComment + "4 not " + threshold4);
        controller.decreaseWinThreshold();
        int threshold5 = model.getWinThreshold();
        assertEquals(3, threshold5, failedTestComment + "3 not " + threshold5);
        sendCommandToController("a1");
        controller.increaseWinThreshold();
        int threshold6 = model.getWinThreshold();
        assertEquals(4, threshold6, failedTestComment + "4 not " + threshold6);
        sendCommandToController("a3");
        controller.decreaseWinThreshold();
        int threshold7 = model.getWinThreshold();
        assertEquals(threshold6, threshold7, failedTestComment + threshold6 + " not " + threshold7);
        sendCommandToController("b2");
        sendCommandToController("a2");
        sendCommandToController("c3");
        sendCommandToController("b1");
        controller.addRow();
        controller.addColumn();
        sendCommandToController("d4");
        controller.increaseWinThreshold();
        int threshold8 = model.getWinThreshold();
        assertEquals(5, threshold8, failedTestComment + "5 not " + threshold8);
        controller.reset();
        int threshold9 = model.getWinThreshold();
        assertEquals(threshold8, threshold9, failedTestComment + threshold8 + " not " + threshold9);
    }

    @Test
    void testChangeEmptyGrid() {
        String failedTestComment0 = "The grid row number should be ";
        String failedTestComment1 = "The grid col number should be ";
        int rowNum0 = model.getNumberOfRows();
        assertEquals(3, rowNum0, failedTestComment0 + "3 not " + rowNum0);
        int colNum0 = model.getNumberOfColumns();
        assertEquals(3, colNum0, failedTestComment1 + "3 not " + colNum0);
        controller.addColumn();
        int colNum1 = model.getNumberOfColumns();
        assertEquals(4, colNum1, failedTestComment1 + "4 not " + colNum1);
        controller.removeColumn();
        int colNum2 = model.getNumberOfColumns();
        assertEquals(3, colNum2, failedTestComment1 + "3 not " + colNum2);
        controller.addRow();
        int rowNum1 = model.getNumberOfRows();
        assertEquals(4, rowNum1, failedTestComment0 + "4 not " + rowNum1);
        controller.removeRow();
        int rowNum2 = model.getNumberOfRows();
        assertEquals(3, rowNum2, failedTestComment0 + "3 not " + rowNum2);
        controller.removeRow();
        controller.removeRow();
        int rowNum3 = model.getNumberOfRows();
        assertEquals(1, rowNum3, failedTestComment0 + "1 not " + rowNum3);
        controller.removeRow();
        int rowNum4 = model.getNumberOfRows();
        assertEquals(1, rowNum4, failedTestComment0 + "1 not " + rowNum4);
        controller.removeColumn();
        controller.removeColumn();
        int colNum3 = model.getNumberOfColumns();
        assertEquals(1, colNum3, failedTestComment1 + "1 not " + colNum3);
        controller.removeColumn();
        int colNum4 = model.getNumberOfColumns();
        assertEquals(1, colNum4, failedTestComment1 + "1 not " + colNum4);
        for (int i = 0; i < 8; i++) {
            controller.addRow();
            controller.addColumn();
        }
        int rowNum5 = model.getNumberOfRows();
        int colNum5 = model.getNumberOfColumns();
        assertEquals(9, rowNum5, failedTestComment0 + "9 not " + rowNum5);
        assertEquals(9, colNum5, failedTestComment1 + "9 not " + colNum5);
        controller.addRow();
        int rowNum6 = model.getNumberOfRows();
        assertEquals(9, rowNum6, failedTestComment0 + "9 not " + rowNum6);
        controller.addColumn();
        int colNum6 = model.getNumberOfColumns();
        assertEquals(9, colNum6, failedTestComment1 + "9 not " + colNum6);
    }

    @Test
    void testChangePartiallyFilledGrid() {
        sendCommandToController("b2");
        controller.removeRow();
        controller.removeColumn();
        String failedTestComment0 = "The grid row number should be ";
        String failedTestComment1 = "The grid col number should be ";
        int rowNum0 = model.getNumberOfRows();
        assertEquals(2, rowNum0, failedTestComment0 + "2 not " + rowNum0);
        int colNum0 = model.getNumberOfColumns();
        assertEquals(2, colNum0, failedTestComment1 + "2 not " + colNum0);
        controller.removeRow();
        controller.removeColumn();
        int rowNum1 = model.getNumberOfRows();
        assertEquals(2, rowNum1, failedTestComment0 + "2 not " + rowNum1);
        int colNum1 = model.getNumberOfColumns();
        assertEquals(2, colNum1, failedTestComment1 + "2 not " + colNum1);
    }

    @Test
    void testChangeWonGrid() {
        sendCommandToController("a1");
        sendCommandToController("b1");
        sendCommandToController("a2");
        sendCommandToController("b2");
        sendCommandToController("a3");
        String failedTestComment0 = "The grid row number should be ";
        String failedTestComment1 = "The grid col number should be ";
        controller.addRow();
        controller.addColumn();
        int rowNum0 = model.getNumberOfRows();
        assertEquals(4, rowNum0, failedTestComment0 + "4 not " + rowNum0);
        int colNum0 = model.getNumberOfColumns();
        assertEquals(4, colNum0, failedTestComment1 + "4 not " + colNum0);
        controller.removeRow();
        controller.removeColumn();
        int rowNum1 = model.getNumberOfRows();
        assertEquals(3, rowNum1, failedTestComment0 + "3 not " + rowNum1);
        int colNum1 = model.getNumberOfColumns();
        assertEquals(3, colNum1, failedTestComment1 + "3 not " + colNum1);
        controller.removeRow();
        controller.removeColumn();
        int rowNum2 = model.getNumberOfRows();
        assertEquals(2, rowNum2, failedTestComment0 + "2 not " + rowNum2);
        int colNum2 = model.getNumberOfColumns();
        assertEquals(3, colNum2, failedTestComment1 + "3 not " + colNum2);
    }

    @Test
    void testChangeGridCauseDrawn() {
        String failedTestComment0 = "Removing row should have caused game drawn";
        String failedTestComment1 = "Removing col should have caused game drawn";
        String failedTestComment2 = "Adding row should have cancelled game drawn";
        String failedTestComment3 = "Adding col should have cancelled game drawn";
        sendCommandToController("a1");
        sendCommandToController("a2");
        sendCommandToController("a3");
        controller.removeRow();
        controller.removeRow();
        assertTrue(model.isGameDrawn(), failedTestComment0);
        controller.addRow();
        assertFalse(model.isGameDrawn(), failedTestComment2);
        controller.addColumn();
        controller.removeRow();
        controller.removeColumn();
        assertTrue(model.isGameDrawn(), failedTestComment1);
        controller.addColumn();
        assertFalse(model.isGameDrawn(), failedTestComment3);
    }

    @Test
    void testMorePlayers() {
        String failedTestComment0 = "The number of players should be ";
        String failedTestComment1 = "Current player should be player ";
        String failedTestComment2 = "Cell owner should be player ";
        int playerNum0 = model.getNumberOfPlayers();
        assertEquals(2, playerNum0, failedTestComment0 + "2 not " + playerNum0);
        controller.addPlayer('A');
        int playerNum1 = model.getNumberOfPlayers();
        assertEquals(3, playerNum1, failedTestComment0 + "3 not " + playerNum1);
        sendCommandToController("a1");
        sendCommandToController("a2");
        int playerNum2 = model.getCurrentPlayerNumber();
        assertEquals(2, playerNum2, failedTestComment1 + "2 not " + playerNum2);
        char letter0 = model.getPlayerByNumber(playerNum2).getPlayingLetter();
        assertEquals('A', letter0, failedTestComment1 + "'A' not " + letter0);
        sendCommandToController("a3");
        char letter1 = model.getCellOwner(0, 2).getPlayingLetter();
        assertEquals(letter0, letter1, failedTestComment2 + letter0 + " not " + letter0);
        int playerNum3 = model.getCurrentPlayerNumber();
        assertEquals(0, playerNum3, failedTestComment1 + "0 not " + playerNum2);
        sendCommandToController("b1");
        sendCommandToController("b2");
        sendCommandToController("b3");
        controller.addPlayer('B');
        int playerNum4 = model.getNumberOfPlayers();
        assertEquals(4, playerNum4, failedTestComment0 + "4 not " + playerNum4);
        int playerNum5 = model.getCurrentPlayerNumber();
        assertEquals(3, playerNum5, failedTestComment1 + "3 not " + playerNum5);
        sendCommandToController("c1");
        char letter2 = model.getCellOwner(2, 0).getPlayingLetter();
        char letter3 = model.getPlayerByNumber(3).getPlayingLetter();
        assertEquals(letter3, letter2, failedTestComment2 + letter3 + " not " + letter2);
        int playerNum6 = model.getCurrentPlayerNumber();
        assertEquals(0, playerNum6, failedTestComment1 + "0 not " + playerNum6);
    }
}
