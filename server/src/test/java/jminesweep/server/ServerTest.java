package jminesweep.server;


import com.voipfuture.jminesweep.server.cell.BombCell;
import com.voipfuture.jminesweep.server.cell.GameBoard;
import com.voipfuture.jminesweep.shared.Difficulty;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class ServerTest {
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

    public void testNormalBombCount(Difficulty difficulty, int expectedBombCount) {
        GameBoard gameBoard = new GameBoard(10, 10, difficulty);
        long bombCount = Arrays.stream(gameBoard.getGameCells())
                .flatMap(Arrays::stream)
                .filter(cell -> cell instanceof BombCell)
                .count();
        assertEquals(bombCount, expectedBombCount);
    }
}
