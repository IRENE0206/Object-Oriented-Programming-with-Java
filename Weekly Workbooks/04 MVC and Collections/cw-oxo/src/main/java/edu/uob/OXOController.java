package edu.uob;
import edu.uob.OXOMoveException.*;

public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        if (gameModel.isWinDetected()) {
            return;
        }
        validateMove(command);
        gameModel.setGameStarted();
        if (winDetected()) {
            gameModel.setWinDetected();
        }
    }

    private void validateMove(String command) throws OXOMoveException {
        int length = command.length();
        if (length != 2) {
            throw new InvalidIdentifierLengthException(length);
        }
        char rowChar = command.charAt(0);
        if (!Character.isLetter(rowChar)) {
            throw new InvalidIdentifierCharacterException(RowOrColumn.ROW, rowChar);
        }
        char colChar = command.charAt(1);
        if (!Character.isDigit(colChar)) {
            throw new InvalidIdentifierCharacterException(RowOrColumn.COLUMN, colChar);
        }
        int row = Character.toLowerCase(rowChar) - 'a';
        if (row < 0 || row >= gameModel.getNumberOfRows()) {
            throw new OutsideCellRangeException(RowOrColumn.ROW, row);
        }
        int col = Character.getNumericValue(colChar);
        if (col <= 0 || col > gameModel.getNumberOfColumns()) {
            throw new OutsideCellRangeException(RowOrColumn.COLUMN, col);
        }
        if (gameModel.getCellOwner(row, col - 1) != null) {
            throw new CellAlreadyTakenException(row, col);
        }
        markCell(row, col - 1);
        setNextPlayer();
    }

    private void markCell(int row, int col) {
        int currPlayer = gameModel.getCurrentPlayerNumber();
        gameModel.setCellOwner(row, col, gameModel.getPlayerByNumber(currPlayer));
    }

    private void setNextPlayer() {
        int currPlayerNum = gameModel.getCurrentPlayerNumber();
        int playerNumbers = gameModel.getNumberOfPlayers();
        assert currPlayerNum < playerNumbers;
        if (currPlayerNum < playerNumbers - 1) {
            gameModel.setCurrentPlayerNumber(currPlayerNum + 1);
        } else {
            gameModel.setCurrentPlayerNumber(0);
        }
    }

    public void addRow() {
        gameModel.addRow();
    }

    public void removeRow() {
        gameModel.removeRow();
        if (drawnDetected()) {
            gameModel.setGameDrawn();
        }
    }

    public void addColumn() {
        gameModel.addColumn();
    }

    public void removeColumn() {
        gameModel.removeColumn();
        if (drawnDetected()) {
            gameModel.setGameDrawn();
        }
    }

    private boolean drawnDetected() {
        if (gameModel.isWinDetected()) {
            return false;
        }
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                if (gameModel.getCellOwner(i, j) == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void increaseWinThreshold() {
        gameModel.setWinThreshold(gameModel.getWinThreshold() + 1);
    }

    public void decreaseWinThreshold() {
        if (!gameModel.isWinDetected() && !gameModel.isGameStarted()) {
            int threshold = gameModel.getWinThreshold();
            if (threshold > 3) {
                gameModel.setWinThreshold(gameModel.getWinThreshold() - 1);
            }
        }
    }

    public void reset() {
        gameModel.setWinner(null);
        gameModel.cancelGameDrawn();
        gameModel.setCurrentPlayerNumber(0);
        gameModel.cancelWinDetected();
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                gameModel.setCellOwner(i, j, null);
            }
        }
        gameModel.cancelGameStarted();
    }

    private boolean winDetected() {
        int rowTotal = gameModel.getNumberOfRows();
        int colTotal = gameModel.getNumberOfColumns();
        boolean allFilled = true;
        for (int i = 0; i < rowTotal; i++) {
            for (int j = 0; j < colTotal; j++) {
                OXOPlayer player = gameModel.getCellOwner(i, j);
                if (player != null) {
                    boolean horizontal = checkHorizontal(i, j, rowTotal, colTotal);
                    boolean vertical = checkVertical(i, j, rowTotal, colTotal);
                    boolean diagonal = checkDiagonal(i, j, rowTotal, colTotal);
                    if (horizontal || vertical || diagonal) {
                        gameModel.setWinner(player);
                        return true;
                    }
                } else {
                    allFilled = false;
                }
            }
        }
        if (allFilled) {
            gameModel.setGameDrawn();
        }
        return false;
    }

    private boolean checkHorizontal(int row0, int col0, int rowTotal, int colTotal) {
        return checkDir(row0, col0, 0, 1, rowTotal, colTotal);
    }

    private boolean checkVertical(int row0, int col0, int rowTotal, int colTotal) {
        return checkDir(row0, col0, 1, 0, rowTotal, colTotal);
    }

    private boolean checkDiagonal(int row0, int col0, int rowTotal, int colTotal) {
        boolean diagonalGoDownLeft = checkDiagonalGoDownLeft(row0, col0, rowTotal, colTotal);
        boolean diagonalGoDownRight = checkDiagonalGoDownRight(row0, col0, rowTotal, colTotal);
        return diagonalGoDownLeft || diagonalGoDownRight;
    }
    private boolean checkDiagonalGoDownLeft(int row0, int col0, int rowTotal, int colTotal) {
        return checkDir(row0, col0, 1, -1, rowTotal, colTotal);
    }

    private boolean checkDiagonalGoDownRight(int row0, int col0, int rowTotal, int colTotal) {
        return checkDir(row0, col0, 1, 1, rowTotal, colTotal);
    }

    private boolean checkDir(int row0, int col0, int rowFactor, int colFactor, int rowTotal, int colTotal) {
        OXOPlayer player0 = gameModel.getCellOwner(row0, col0);
        assert player0 != null;
        int threshold = gameModel.getWinThreshold();
        boolean rowOutOfRange = outOfRange(row0, rowTotal - 1, rowFactor, threshold);
        boolean colOutOfRange = outOfRange(col0, colTotal - 1, colFactor, threshold);
        if (rowOutOfRange || colOutOfRange) {
            return false;
        }
        char letter0 = player0.getPlayingLetter();
        for (int i = 1; i < threshold; i++) {
            int rowI = row0 + i * rowFactor;
            int colI = col0 + i * colFactor;
            OXOPlayer currOwner = gameModel.getCellOwner(rowI, colI);
            if (currOwner == null || currOwner.getPlayingLetter() != letter0) {
                return false;
            }
        }
        return true;
    }

    private boolean outOfRange(int startIndex, int maxIndex, int factor, int threshold) {
        int endIndex = startIndex + factor * (threshold - 1);
        return endIndex < 0 || endIndex > maxIndex;
    }
}
