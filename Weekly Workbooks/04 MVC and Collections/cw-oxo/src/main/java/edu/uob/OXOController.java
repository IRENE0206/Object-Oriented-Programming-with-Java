package edu.uob;

public class OXOController {
    OXOModel gameModel;
    private boolean winDetected;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        if (winDetected) {
            return;
        }
        String com = command.toLowerCase();
        int row = com.charAt(0) - 'a';
        int col = Character.getNumericValue(com.charAt(1)) - 1;
        if (gameModel.getCellOwner(row, col) == null) {
            gameModel.setCellOwner(row, col, gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber()));
            gameModel.setCurrentPlayerNumber(1 - gameModel.getCurrentPlayerNumber());
            if (winDetected()) {
                winDetected = true;
            }
        }
    }
    public void addRow() {
        if (!winDetected) {
            gameModel.addRow();
        }
    }
    public void removeRow() {
        gameModel.removeRow();
    }
    public void addColumn() {
        if (!winDetected) {
            gameModel.addColumn();
        }
    }
    public void removeColumn() {
        gameModel.removeColumn();
    }
    public void increaseWinThreshold() {}
    public void decreaseWinThreshold() {}
    public void reset() {
        gameModel.setWinner(null);
        gameModel.cancelGameDrawn();
        winDetected = false;
        for (int i = 0; i < gameModel.getNumberOfRows(); i++) {
            for (int j = 0; j < gameModel.getNumberOfColumns(); j++) {
                gameModel.setCellOwner(i, j, null);
            }
        }
    }

    private boolean winDetected() {
        int rows = gameModel.getNumberOfRows();
        int cols = gameModel.getNumberOfColumns();
        boolean allFilled = true;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (gameModel.getCellOwner(i, j) == null) {
                    allFilled = false;
                }
                if (checkHorizontal(i, j, cols) || checkVertical(i, j, rows) || checkDiagonal(i, j, rows, cols)) {
                    return true;
                }
            }
        }
        if (allFilled) {
            gameModel.setGameDrawn();
        }
        return false;
    }

    private boolean checkHorizontal(int rowIndex, int colIndex, int colTotal) {
        if (colIndex + 3 > colTotal) {
            return false;
        }
        return sameOwner(rowIndex, colIndex, rowIndex, colIndex + 1, rowIndex, colIndex + 2);
    }

    private boolean checkVertical(int rowIndex, int colIndex, int rowTotal) {
        if (rowIndex + 3 > rowTotal) {
            return false;
        }
        return sameOwner(rowIndex, colIndex, rowIndex + 1, colIndex, rowIndex + 2, colIndex);
    }

    private boolean checkDiagonal(int rowIndex, int colIndex, int rowTotal, int colTotal) {
        if (rowIndex + 3 > rowTotal) {
            return false;
        }
        boolean goLeft = false;
        boolean goRight = false;
        if (colIndex - 2 >= 0) {
            goLeft = sameOwner(rowIndex, colIndex, rowIndex + 1, colIndex - 1, rowIndex + 2, colIndex - 2);
        }
        if (colIndex + 3 <= colTotal) {
            goRight = sameOwner(rowIndex, colIndex, rowIndex + 1, colIndex + 1, rowIndex + 2, colIndex + 2);
        }
        return goLeft || goRight;
    }

    private boolean sameOwner(int row0, int col0, int row1, int col1, int row2, int col2) {
        OXOPlayer p0 = gameModel.getCellOwner(row0, col0);
        OXOPlayer p1 = gameModel.getCellOwner(row1, col1);
        OXOPlayer p2 = gameModel.getCellOwner(row2, col2);
        if (p0 == null || p1 == null || p2 == null) {
            return false;
        }
        char c0 = p0.getPlayingLetter();
        char c1 = p1.getPlayingLetter();
        char c2 = p2.getPlayingLetter();
        if (c0 == c1 && c1 == c2) {
            gameModel.setWinner(p0);
            return true;
        }
        return false;
    }
}
