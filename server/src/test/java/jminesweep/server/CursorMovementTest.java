package jminesweep.server;

import com.voipfuture.jminesweep.server.cell.GameBoard;
import com.voipfuture.jminesweep.shared.Difficulty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CursorMovementTest {
    public static final int dimX = 10;
    public static final int dimY = 10;
    final static int initialX = 5;
    final static int initialY = 5;
    GameBoard gameBoard;

    @BeforeEach
    public void setUp() {
        gameBoard = new GameBoard(dimX, dimY, Difficulty.EASY);
        gameBoard.setCursorPosition(new int[]{initialX, initialY});
    }

    @Test
    public void moveCursorUp() {
        {
            var cursorPosition = gameBoard.getCursorPosition();
            gameBoard.moveCursorUp();
            assertEquals(cursorPosition[0], initialX);
            assertEquals(cursorPosition[1], initialY - 1);
        }
        {
            setCursorToTopLeft();
            var cursorPosition = gameBoard.getCursorPosition();
            gameBoard.moveCursorUp();
            assertEquals(cursorPosition[0], 0);
            assertEquals(cursorPosition[1], 0);
        }
    }

    @Test
    public void moveCursorDown() {
        {
            var cursorPosition = gameBoard.getCursorPosition();
            gameBoard.moveCursorDown();
            assertEquals(cursorPosition[0], initialX);
            assertEquals(cursorPosition[1], initialY + 1);
        }
        {
            setCursorToBottomRight();
            var cursorPosition = gameBoard.getCursorPosition();
            gameBoard.moveCursorDown();
            assertEquals(cursorPosition[0], dimX - 1);
            assertEquals(cursorPosition[1], dimY - 1);
        }
    }

    @Test
    public void moveCursorRight() {
        {
            var cursorPosition = gameBoard.getCursorPosition();
            gameBoard.moveCursorRight();
            assertEquals(cursorPosition[0], initialX + 1);
            assertEquals(cursorPosition[1], initialY);
        }
        {
            setCursorToBottomRight();
            var cursorPosition = gameBoard.getCursorPosition();
            gameBoard.moveCursorRight();
            assertEquals(cursorPosition[0], dimX - 1);
            assertEquals(cursorPosition[1], dimY - 1);
        }
    }

    @Test
    public void moveCursorLeft() {
        {
            var cursorPosition = gameBoard.getCursorPosition();
            gameBoard.moveCursorLeft();
            assertEquals(cursorPosition[0], initialX - 1);
            assertEquals(cursorPosition[1], initialY);
        }
        {
            setCursorToTopLeft();
            var cursorPosition = gameBoard.getCursorPosition();
            gameBoard.moveCursorLeft();
            assertEquals(cursorPosition[0], 0);
            assertEquals(cursorPosition[1], 0);
        }
    }

    public void setCursorToTopLeft() {
        gameBoard.setCursorPosition(new int[]{0, 0});
    }

    public void setCursorToBottomRight() {
        gameBoard.setCursorPosition(new int[]{dimX - 1, dimY - 1});
    }

}
