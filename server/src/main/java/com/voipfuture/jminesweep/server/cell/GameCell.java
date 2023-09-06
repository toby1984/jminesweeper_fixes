package com.voipfuture.jminesweep.server.cell;

public abstract class GameCell {
    public GameCell(char revealedChar) {
        this.revealedChar = revealedChar;
    }

    private char revealedChar;
    private int[] coordinates;

    private CellState cellState = CellState.UNKNOWN;
    public static final char UNKNOWN_CELL_ICON = '?';
    public static final char BOMB_CELL_ICON = 'B';

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

    public static final char EMPTY_CELL_ICON = '.';


    public char getRevealedChar() {
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
        } else if (this.cellState == CellState.UNKNOWN) {
            this.cellState = CellState.FLAGGED;
        }
    }

    abstract void triggerSelectEffects(GameBoard board);

    public void select(GameBoard board) {
        setToRevealedState();
        triggerSelectEffects(board);
    }


    enum CellState implements CellStateCharProvider {
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
                return cell.getRevealedChar();
            }
        }
    }

    public interface CellStateCharProvider {
        char getGuiChar(GameCell cell);
    }

    @Override
    public String toString() {
        return "[" + getRevealedChar() + "]";
    }
}
