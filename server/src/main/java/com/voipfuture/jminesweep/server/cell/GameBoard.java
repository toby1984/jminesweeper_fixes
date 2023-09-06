package com.voipfuture.jminesweep.server.cell;

import com.voipfuture.jminesweep.shared.Difficulty;

import java.util.Random;

public class GameBoard {
    private int[] cursorPosition = new int[]{0, 0};
    private GameCell[][] gameCells;
    public GameBoard(int xSize, int ySize, Difficulty difficulty) {
        float bombDensity = difficultyToBombDensity(difficulty);
        final int numberOfBombs = (int) Math.ceil(xSize * ySize * bombDensity);
        generateInitialBoard(xSize, ySize, numberOfBombs);
    }

    public boolean isGameWon() {
        for (GameCell[] gameCell : gameCells) {
            for (GameCell cell : gameCells[0]) {
                if (cell.getCellState() == GameCell.CellState.UNKNOWN) {
                    return false;
                }
            }
        }
        return true;
    }

    public String render() {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < gameCells.length; ++i) {
            for (int j = 0; j < gameCells[0].length; ++j) {
                builder.append(gameCells[i][j].toString());
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private void generateInitialBoard(int xSize, int ySize, int numberOfBombs) {
        gameCells = new GameCell[xSize][ySize];
        for (int i = 0; i < xSize; ++i) {
            for (int j = 0; j < ySize; ++j) {
                if ((i * xSize) + j + 1 <= numberOfBombs) {
                    gameCells[i][j] = new BombCell();
                } else {
                    gameCells[i][j] = null;
                }
            }
        }
        // We placed all the bombs at the beginning of the array
        // Then we distribute them at random
        shuffle(gameCells);
        for (int i = 0; i < xSize; ++i) {
            for (int j = 0; j < ySize; ++j) {
                GameCell cell = gameCells[i][j];
                if (null == cell) {
                    int bombCount = getSurroundingBombsCount(gameCells, i, j);
                    if (bombCount == 0) {
                        gameCells[i][j] = new EmptyCell();
                    } else {
                        gameCells[i][j] = new NumberedCell(Character.forDigit(bombCount, 10));
                    }
                }
            }
        }
    }

    private int getSurroundingBombsCount(GameCell[][] cells, int xCoord, int yCoord) {
        int nbOfBombs = 0;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int comparedX = xCoord + i;
                int comparedY = yCoord + j;
                if (comparedX > 0 && comparedX < cells.length &&
                    comparedY > 0 && comparedY < cells[0].length) {
                    GameCell comparedCell = cells[comparedX][comparedY];
                    if (null != comparedCell) {
                        nbOfBombs++;
                    }
                }
            }
        }
        return nbOfBombs;
    }
    private GameCell[][] shuffle(GameCell[][] a) {
        Random random = new Random();
        for (int i = a.length - 1; i > 0; i--) {
            for (int j = a[i].length - 1; j > 0; j--) {
                int m = random.nextInt(i + 1);
                int n = random.nextInt(j + 1);

                GameCell temp = a[i][j];
                a[i][j] = a[m][n];
                a[m][n] = temp;
            }
        }
        return a;
    }

    private float difficultyToBombDensity(Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 0.05f;
            case MEDIUM -> 0.25f;
            case HARD -> 0.45f;
        };
    }

    public int[] getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(int[] cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    public GameCell[][] getGameCells() {
        return gameCells;
    }

    public void setGameCells(GameCell[][] gameCells) {
        this.gameCells = gameCells;
    }

    public void moveCursorUp() {
        int nextY = Math.min(cursorPosition[1] - 1, 0);
        cursorPosition[1] = nextY;
    }

    public void moveCursorDown() {
        int nextY = Math.max(cursorPosition[1] + 1, gameCells[0].length);
        cursorPosition[1] = nextY;
    }

    public void moveCursorRight() {
        int nextY = Math.min(cursorPosition[0] - 1, 0);
        cursorPosition[1] = nextY;
    }

    public void moveCursorLeft() {
        int nextY = Math.max(cursorPosition[0] + 1, gameCells.length);
        cursorPosition[1] = nextY;
    }

    public void toggleBombMark() {
        GameCell cursorCell = getCursorCell();
        cursorCell.toggleFlaggedState();
    }

    public void reveal() {
        GameCell cursorCell = getCursorCell();
        cursorCell.setToRevealedState();
    }

    private GameCell getCursorCell() {
        return gameCells[cursorPosition[0]][cursorPosition[1]];
    }
}
