package edu.uob;
import java.util.ArrayList;
import java.util.List;

public class OXOModel {

    private List<List<OXOPlayer>> cells;
    private OXOPlayer[] players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private boolean winDetected;
    private int winThreshold;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        this.winThreshold = winThresh;
        this.cells = new ArrayList<>(numberOfRows);
        for (int i = 0; i < numberOfRows; i++) {
            List<OXOPlayer> row = new ArrayList<>(numberOfColumns);
            for (int j = 0; j < numberOfColumns; j++) {
                row.add(null);
            }
            cells.add(row);
        }
        this.currentPlayerNumber = 0;
        this.players = new OXOPlayer[2];
        this.gameDrawn = false;
        this.winner = null;
        this.winDetected = false;
    }

    public int getNumberOfPlayers() {
        return players.length;
    }

    public void addPlayer(OXOPlayer player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = player;
                return;
            }
        }
    }

    public OXOPlayer getPlayerByNumber(int number) {
        return players[number];
    }

    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() {
        return cells.size();
    }

    public int getNumberOfColumns() {
        return cells.get(0).size();
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        cells.get(rowNumber).set(colNumber, player);
    }

    public void setWinThreshold(int winThresh) {
        winThreshold = winThresh;
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn() {
        gameDrawn = true;
    }

    public void cancelGameDrawn() {
        gameDrawn = false;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }

    public void addColumn() {
        if (getNumberOfColumns() >= 9) {
            return;
        }
        for (int i = 0; i < getNumberOfRows(); i++) {
            cells.get(i).add(null);
        }
        cancelGameDrawn();
    }

    public void addRow() {
        if (getNumberOfRows() >= 9) {
            return;
        }
        int col = getNumberOfColumns();
        List<OXOPlayer> row = new ArrayList<>(col);
        for (int i = 0; i < col; i++) {
            row.add(null);
        }
        cells.add(row);
        cancelGameDrawn();
    }

    public void removeColumn() {
        int colIndex = getNumberOfColumns() - 1;
        if (colIndex <= 0) {
            return;
        }
        for (int i = 0; i < getNumberOfRows(); i++) {
            if (cells.get(i).get(colIndex) != null) {
                return;
            }
        }
        for (int i = 0; i < getNumberOfRows(); i++) {
            cells.get(i).remove(colIndex);
        }
    }

    public void removeRow() {
        int rowIndex = getNumberOfRows() - 1;
        if (rowIndex <= 0) {
            return;
        }
        for (int i = 0; i < getNumberOfColumns(); i++) {
            if (cells.get(rowIndex).get(i) != null) {
                return;
            }
        }
        cells.remove(rowIndex);
    }

    public void setWinDetected() {
        winDetected = true;
    }

    public boolean isWinDetected() {
        return winDetected;
    }

    public void cancelWinDetected() {
        winDetected = true;
    }
}
