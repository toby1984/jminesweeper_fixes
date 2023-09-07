package com.voipfuture.jminesweep.server.cell;

public abstract class GameCell {
    public static final char EMPTY_CELL_ICON = '.';
    public static final char UNKNOWN_CELL_ICON = '?';
    public static final char BOMB_CELL_ICON = 'B';

    protected GameBoard board;
    private char revealedChar;
    private int[] coordinates;
    private CellState cellState = CellState.UNKNOWN;
    public GameCell(char revealedChar, GameBoard board) {
        this.revealedChar = revealedChar;
        this.board = board;
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
    abstract void triggerOnFlagEffects();

    abstract void triggerOnUnflagEffects();
    abstract void triggerSelectEffects();

    public void select() {
        setToRevealedState();
        triggerSelectEffects();
    }

    public String getClientGuiCell() {
        return "[" + getClientGuiChar() + "]";
    }

    public String getRevealedGuiCell() {
        return "[" + getRevealedChar() + "]";
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
