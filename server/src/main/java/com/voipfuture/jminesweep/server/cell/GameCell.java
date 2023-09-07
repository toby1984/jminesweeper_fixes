package com.voipfuture.jminesweep.server.cell;

import java.util.ArrayList;
import java.util.List;

public abstract class GameCell {
    public static final char EMPTY_CELL_ICON = '.';
    public static final char UNKNOWN_CELL_ICON = '?';
    public static final char BOMB_CELL_ICON = 'B';

    protected GameBoard board;
    private char revealedChar;
    private int[] coordinates;
    private CellState cellState = CellState.UNKNOWN;
    public GameCell(char revealedChar, int[] coordinates, GameBoard board) {
        this.revealedChar = revealedChar;
        this.board = board;
        this.coordinates = coordinates;
    }

    public char getRevealedChar() {
        return revealedChar;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(int[] coordinates) {
        this.coordinates = coordinates;
    }

    public CellState getCellState() {
        return cellState;
    }

    public void setCellState(CellState cellState) {
        this.cellState = cellState;
    }


    public char getClientGuiChar() {
        return cellState.getGuiChar(this);
    }

    public void setRevealedChar(char revealedChar) {
        this.revealedChar = revealedChar;
    }

    public void setToRevealedState() {
        if (this.cellState == CellState.UNKNOWN) {
            this.cellState = CellState.REVEALED;
        }
    }

    public void toggleFlaggedState() {
        if (this.cellState == CellState.FLAGGED) {
            this.cellState = CellState.UNKNOWN;
            this.board.unflagCellAsBomb();
            triggerOnUnflagEffects();

        } else if (this.cellState == CellState.UNKNOWN) {
            this.cellState = CellState.FLAGGED;
            this.board.flagCellAsBomb();
            triggerOnFlagEffects();
        }
    }
    public abstract void triggerOnFlagEffects();

    public abstract void triggerOnUnflagEffects();
    public abstract void triggerSelectEffects();

    public void select() {
        if (cellState == CellState.UNKNOWN) {
            setToRevealedState();
            triggerSelectEffects();
        }
    }

    public String getClientGuiCell() {
        return "[" + getClientGuiChar() + "]";
    }

    public String getRevealedGuiCell() {
        return "[" + getRevealedChar() + "]";
    }

    public List<GameCell> findSurroundingCells() {
        GameCell[][] gameCells = this.board.getGameCells();
        int[] coordinates = getCoordinates();
        return findSurroundingCells(coordinates, gameCells);
    }

    public static List<GameCell> findSurroundingCells(int[] coordinates, GameCell[][] gameCells) {
        List<GameCell> surroundingCells = new ArrayList<>();
        for (var i = -1; i <= 1; ++i) {
            var xCoord = coordinates[0] + i;
            for (var j = -1; j <= 1; ++j) {
                var yCoord = coordinates[1] + j;
                if (xCoord < 0 || xCoord >= gameCells.length ||
                        yCoord < 0 || yCoord >= gameCells[0].length) {
                    continue;
                }
                surroundingCells.add(gameCells[xCoord][yCoord]);
            }
        }
        return surroundingCells;
    }

    public enum CellState implements CellStateCharProvider {
        UNKNOWN {
            public char getGuiChar(GameCell cell) {
                return UNKNOWN_CELL_ICON;
            }
        },
        FLAGGED {
            public char getGuiChar(GameCell cell) {
                return BOMB_CELL_ICON;
            }
        },
        REVEALED {
            public char getGuiChar(GameCell cell) {
                return cell.getClientGuiChar();
            }
        }
    }

    public interface CellStateCharProvider {
        char getGuiChar(GameCell cell);
    }
}
