package jminesweep.server;


import com.voipfuture.jminesweep.server.cell.*;
import com.voipfuture.jminesweep.shared.Difficulty;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    @Test
    public void testGameWon() {
        // A board with one cell generates one bomb. Flagging it results in a win.
        GameBoard gameBoard = new GameBoard(1, 1, Difficulty.EASY);
        assertEquals(gameBoard.getGameState(), GameState.ONGOING);
        gameBoard.getGameCells()[0][0].toggleFlaggedState();
        assertEquals(gameBoard.getGameState(), GameState.WON);
    }

    @Test
    public void testGameLost() {
        // A board with one cell generates one bomb. Selecting it results in a loss.
        GameBoard gameBoard = new GameBoard(1, 1, Difficulty.EASY);
        assertEquals(gameBoard.getGameState(), GameState.ONGOING);
        gameBoard.getGameCells()[0][0].select();
        assertEquals(gameBoard.getGameState(), GameState.LOST);
    }

    @Test
    public void testOnlyBombsSelectedGrantsVictory() {
        // A board with many cells has many bombs. They need to be the only cells flagged.
        GameBoard gameBoard = new GameBoard(10, 10, Difficulty.EASY);
        assertEquals(gameBoard.getGameState(), GameState.ONGOING);

        // Find a cell that is not a bomb cell and flag it
        GameCell nonBombCell = Arrays.stream(gameBoard.getGameCells())
                .flatMap(Arrays::stream)
                .filter(cell -> !(cell instanceof BombCell))
                .findAny().orElse(null);
        assert nonBombCell != null;
        nonBombCell.toggleFlaggedState();

        // Selecting all bomb cells does not achieve victory because a non-bomb cell is still selected
        Arrays.stream(gameBoard.getGameCells())
                .flatMap(Arrays::stream)
                .filter(cell -> cell instanceof BombCell)
                .forEach(GameCell::toggleFlaggedState);
        assertEquals(gameBoard.getGameState(), GameState.ONGOING);

        // Unflagging the non-bomb cell means only bomb cells are flagged, which means the game is won
        nonBombCell.toggleFlaggedState();
        assertEquals(gameBoard.getGameState(), GameState.WON);
    }

    @Test
    public void testEasyBombCount() {
        testNormalBombCount(Difficulty.EASY, 5);
    }

    @Test
    public void testMediumBombCount() {
        testNormalBombCount(Difficulty.MEDIUM, 25);
    }

    @Test
    public void testHardBombCount() {
        testNormalBombCount(Difficulty.HARD, 45);
    }

    @Test
    public void testBombCountIsAtLeastOne() {
        testSpecificBombCount(1, 1, Difficulty.EASY, 1);
    }

    @Test
    public void testSolveTheGame() {
        GameBoard gameBoard = new GameBoard(100, 100, Difficulty.EASY);

    }

    @Test
    public void testSelectingEmptyCell() {
        EmptyCell emptyCell = null;
        // In case a new board does not always create an instance of an empty cell
        while (emptyCell == null) {
            // Create a board until there is an empty cell
            GameBoard gameBoard = new GameBoard(100, 100, Difficulty.EASY);
            emptyCell = (EmptyCell) Arrays.stream(gameBoard.getGameCells())
                    .flatMap(Arrays::stream)
                    .filter(cell -> cell instanceof EmptyCell)
                    .findAny().orElse(null);
        }
        // The surrounding cells of the empty cell are still unknown
        List<GameCell> surroundingCells = emptyCell.findSurroundingCells();
        surroundingCells.forEach(cell -> assertEquals(cell.getCellState(), GameCell.CellState.UNKNOWN));
        emptyCell.select();
        // The surrounding cells of the empty cell now also revealed
        surroundingCells.forEach(cell -> assertEquals(cell.getCellState(), GameCell.CellState.REVEALED));
    }

    public void testNormalBombCount(Difficulty difficulty, int expectedBombCount) {
        testSpecificBombCount(10, 10, difficulty, expectedBombCount);
    }

    public void testSpecificBombCount(int xSize, int ySize, Difficulty difficulty, int expectedBombCount) {
        GameBoard gameBoard = new GameBoard(xSize, ySize, difficulty);
        long bombCount = Arrays.stream(gameBoard.getGameCells())
                .flatMap(Arrays::stream)
                .filter(cell -> cell instanceof BombCell)
                .count();
        assertEquals(bombCount, expectedBombCount);
    }
}
