package edu.uob;
import java.util.ArrayList;
import java.util.List;

public class OXOModel {

    private List<List<OXOPlayer>> cells;
    private OXOPlayer[] players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;
        cells = new ArrayList<>(numberOfRows);
        for (int i = 0; i < numberOfRows; i++) {
            List<OXOPlayer> row = new ArrayList<>(numberOfColumns);
            for (int j = 0; j < numberOfColumns; j++) {
                row.add(null);
            }
            cells.add(row);
        }
        players = new OXOPlayer[2];
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

    public boolean isGameDrawn() {
        return gameDrawn;
    }

    public void addColumn() {
        for (int i = 0; i < getNumberOfRows(); i++) {
            cells.get(i).add(null);
        }
    }

    public void addRow() {
        int col = getNumberOfColumns();
        List<OXOPlayer> row = new ArrayList<>(col);
        for (int i = 0; i < col; i++) {
            row.add(null);
        }
        cells.add(row);
    }

    public void removeColumn() {
        int colIndex = getNumberOfColumns() - 1;
        for (int i = 0; i < getNumberOfRows(); i++) {
            if (cells.get(i).get(colIndex) != null) {
                return;
            }
        }
        for (int i = 0; i < getNumberOfRows(); i++) {
            cells.get(i).remove( colIndex);
        }
    }

    public void removeRow() {
        int rowIndex = getNumberOfRows() - 1;
        for (int i = 0; i < getNumberOfColumns(); i++) {
            if (cells.get(rowIndex).get(i) != null) {
                return;
            }
        }
        cells.remove(rowIndex);
    }
}
