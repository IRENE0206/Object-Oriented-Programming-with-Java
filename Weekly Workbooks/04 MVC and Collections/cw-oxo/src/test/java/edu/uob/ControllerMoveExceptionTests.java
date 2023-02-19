package edu.uob;
import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class ControllerMoveExceptionTests {
    private OXOModel model;
    private OXOController controller;

    @BeforeEach
    void setup() {
        model = new OXOModel(4, 4, 4);
        model.addPlayer(new OXOPlayer('X'));
        model.addPlayer(new OXOPlayer('O'));
        controller = new OXOController(model);
    }

    void sendCommandToController(String command) {
        String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
        assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
    }

    @Test
    void testOutsideCellRangeException() throws OXOMoveException {
        String failedTestComment = "Controller failed to throw an OutsideCellRangeException for command ";
        assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("a0"), failedTestComment + "'a0'");
        assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("a5"), failedTestComment + "'a5'");
        assertThrows(OutsideCellRangeException.class, ()-> sendCommandToController("e0"), failedTestComment + "'e0'");
    }

    @Test
    void testInvalidIdentifierLengthException() throws OXOMoveException {
        String failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command ";
        assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("a10"), failedTestComment + "'a10'");
        assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("d-1"), failedTestComment + "'d-1'");
        assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("~b0"), failedTestComment + "'~b0'");
        assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("11a"), failedTestComment + "'11a'");
        assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController(""), failedTestComment + "''");
        assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("a"), failedTestComment + "'a'");
    }

    @Test
    void testInvalidIdentifierCharacterException() throws OXOMoveException {
        String failedTestComment = "Controller failed to throw an InvalidIdentifierCharacterException for command ";
        assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("@1"), failedTestComment + "'@1'");
        assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("-4"), failedTestComment + "'-4'");
        assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("a%"), failedTestComment + "'a%'");
        assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("12"), failedTestComment + "'12'");
        assertThrows(InvalidIdentifierCharacterException.class, ()-> sendCommandToController("cb"), failedTestComment + "'cb'");
    }

    @Test
    void testCellAlreadyTakenException() throws OXOMoveException {
        String failedTestComment = "Controller failed to throw an CellAlreadyTakenException for command ";
        sendCommandToController("a1");
        assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("a1"), failedTestComment + "'a1'");
        sendCommandToController("d4");
        assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("d4"), failedTestComment + "'d4'");
        sendCommandToController("a4");
        assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("a4"), failedTestComment + "'a4'");
        sendCommandToController("d1");
        assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("d1"), failedTestComment + "'d1'");
    }

    @Test
    void testCaseInsensitive() throws OXOMoveException {
        String failedTestComment = "Cell identifier failed to be case-insensitive for command ";
        sendCommandToController("a1");
        assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("A1"), failedTestComment + "'A1'");
        sendCommandToController("D4");
        assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("d4"), failedTestComment + "'d4'");
        sendCommandToController("A4");
        assertThrows(CellAlreadyTakenException.class, ()-> sendCommandToController("A4"), failedTestComment + "'A4'");
    }
}
