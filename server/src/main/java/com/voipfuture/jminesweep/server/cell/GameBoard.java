package com.voipfuture.jminesweep.server.cell;

import com.voipfuture.jminesweep.shared.Difficulty;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class GameBoard {
    private int[] cursorPosition = new int[]{0, 0};
    private int numberOfBombs;
    private GameCell[][] gameCells;
    private GameState gameState = GameState.ONGOING;

    public GameBoard(int xSize, int ySize, Difficulty difficulty) {
        float bombDensity = difficultyToBombDensity(difficulty);
        // There should at least be one bomb or else it's not a game anymore
        final int numberOfBombs = (int) Math.max(Math.ceil(xSize * ySize * bombDensity), 1);
        this.numberOfBombs = numberOfBombs;
        generateInitialBoard(xSize, ySize, numberOfBombs);
    }

    public void updateGameStateIfWon() {
        if (isGameWon()) {
            setGameState(GameState.WON);
        }
    }

    public boolean isGameWon() {
        // The user flagged only bombs and all the bombs
        return numberOfBombs == 0 && Arrays.stream(getGameCells())
                .flatMap(Arrays::stream)
                .filter(cell -> cell instanceof BombCell)
                .allMatch(cell -> cell.getCellState() == GameCell.CellState.FLAGGED);
    }

    public String render() {
        return switch (gameState) {
            case ONGOING -> renderOngoingGame();
            case WON -> renderVictoryScreen();
            case LOST -> renderDefeatScreen();
        };
    }
    private String renderOngoingGame() {
        final StringBuilder builder = new StringBuilder();

        for (GameCell[] gameCell : gameCells) {
            for (int j = 0; j < gameCells[0].length; ++j) {
                builder.append(gameCell[j].getClientGuiCell());
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    private String renderVictoryScreen() {
        final StringBuilder builder = renderRevealed();
        builder.append("\n");
        builder.append("GAME WON");
        return builder.toString();
    }

    private String renderDefeatScreen() {
        final StringBuilder builder = renderRevealed();
        builder.append("\n");
        builder.append("GAME LOST");
        return builder.toString();
    }

    private StringBuilder renderRevealed() {
        final StringBuilder builder = new StringBuilder();

        for (GameCell[] gameCell : gameCells) {
            for (int j = 0; j < gameCells[0].length; ++j) {
                builder.append(gameCell[j].getRevealedGuiCell());
            }
            builder.append("\n");
        }
        return builder;
    }

    private void generateInitialBoard(int xSize, int ySize, int numberOfBombs) {
        gameCells = new GameCell[xSize][ySize];
        for (int i = 0; i < xSize; ++i) {
            for (int j = 0; j < ySize; ++j) {
                if ((i * xSize) + j + 1 <= numberOfBombs) {
                    gameCells[i][j] = new BombCell(this, new int[]{i, j});
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
                int[] coordinates = new int[]{i, j};
                if (null == cell) {
                    int bombCount = getSurroundingBombsCount(gameCells, coordinates);
                    if (bombCount == 0) {
                        gameCells[i][j] = new EmptyCell(this, coordinates);
                    } else {
                        gameCells[i][j] = new NumberedCell(Character.forDigit(bombCount, 10), this, coordinates);
                    }
                }
            }
        }
    }

    private int getSurroundingBombsCount(GameCell[][] cells, int[] coordinates) {
        return (int) GameCell.findSurroundingCells(coordinates, cells)
                .stream()
                .filter(Objects::nonNull)
                .filter(cell -> cell instanceof BombCell)
                .count();
    }

    private void shuffle(GameCell[][] a) {
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
    }

    public void flagCellAsBomb() {
        this.numberOfBombs--;
    }

    public void unflagCellAsBomb() {
        this.numberOfBombs++;
    }

    private float difficultyToBombDensity(Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 0.05f;
            case MEDIUM -> 0.25f;
            case HARD -> 0.45f;
        };
    }

    public int getNumberOfBombs() {
        return numberOfBombs;
    }

    public void setNumberOfBombs(int numberOfBombs) {
        this.numberOfBombs = numberOfBombs;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
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
        int nextY = Math.max(cursorPosition[1] - 1, 0);
        cursorPosition[1] = nextY;
    }

    public void moveCursorDown() {
        int nextY = Math.min(cursorPosition[1] + 1, gameCells[0].length - 1);
        cursorPosition[1] = nextY;
    }

    public void moveCursorRight() {
        int nextX = Math.min(cursorPosition[0] + 1, gameCells.length - 1);
        cursorPosition[0] = nextX;
    }

    public void moveCursorLeft() {
        int nextX = Math.max(cursorPosition[0] - 1, 0);
        cursorPosition[0] = nextX;
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
