package com.voipfuture.jminesweep.server.cell;

public abstract class GameCell {
    public static final char EMPTY_CELL_ICON = '.';
    public static final char UNKNOWN_CELL_ICON = '?';
    public static final char BOMB_CELL_ICON = 'B';
    private char revealedChar;
    private int[] coordinates;
    private CellState cellState = CellState.UNKNOWN;
    public GameCell(char revealedChar) {
        this.revealedChar = revealedChar;
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

    public void toggleFlaggedState(GameBoard board) {
        if (this.cellState == CellState.FLAGGED) {
            this.cellState = CellState.UNKNOWN;
            board.unflagCellAsBomb();
        } else if (this.cellState == CellState.UNKNOWN) {
            this.cellState = CellState.FLAGGED;
            board.flagCellAsBomb();
            triggerOnFlagEffects(board);
        }
    }
    abstract void triggerOnFlagEffects(GameBoard board);
    abstract void triggerSelectEffects(GameBoard board);

    public void select(GameBoard board) {
        setToRevealedState();
        triggerSelectEffects(board);
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
