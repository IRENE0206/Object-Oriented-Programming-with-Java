package edu.uob;

public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {
        if (gameModel.isWinDetected()) {
            return;
        }
        String com = command.toLowerCase();
        int row = com.charAt(0) - 'a';
        int col = Character.getNumericValue(com.charAt(1)) - 1;
        if (gameModel.getCellOwner(row, col) == null) {
            gameModel.setCellOwner(row, col, gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber()));
            gameModel.setCurrentPlayerNumber(gameModel.getNumberOfPlayers() - 1 - gameModel.getCurrentPlayerNumber());
            if (winDetected()) {
                gameModel.setWinDetected();
            }
        }
    }
    public void addRow() {
        if (!gameModel.isWinDetected()) {
            gameModel.addRow();
        }
    }
    public void removeRow() {
        if (!gameModel.isWinDetected()) {
            gameModel.removeRow();
        }
    }
    public void addColumn() {
        if (!gameModel.isWinDetected()) {
            gameModel.addColumn();
        }
    }
    public void removeColumn() {
        if (!gameModel.isWinDetected()) {
            gameModel.removeColumn();
        }
    }
    public void increaseWinThreshold() {
        if (!gameModel.isWinDetected()) {
            gameModel.setWinThreshold(gameModel.getWinThreshold() + 1);
        }
    }
    public void decreaseWinThreshold() {
        if (!gameModel.isWinDetected()) {
            gameModel.setWinThreshold(gameModel.getWinThreshold() - 1);
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
