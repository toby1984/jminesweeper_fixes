package com.voipfuture.jminesweep.server.cell;

public class EmptyCell extends GameCell {
    public EmptyCell() {
        super(EMPTY_CELL_ICON);
    }

    @Override
    void triggerOnFlagEffects(GameBoard board) {
    }

    @Override
    void triggerSelectEffects(GameBoard board) {
        // An empty cell triggers the select effect recursively to its surrounding
        GameCell[][] gameCells = board.getGameCells();
        int[] coordinates = getCoordinates();
        for (var i = -1; i <= 1; ++i) {
            var xCoord = coordinates[0] + i;
            for (var j = -1; j <= 1; ++j) {
                var yCoord = coordinates[1] + j;
                if (xCoord < 0 || xCoord >= gameCells.length ||
                    yCoord < 0 || yCoord >= gameCells[0].length) {
                    continue;
                }
                gameCells[xCoord][yCoord].select(board);
            }
        }
    }
}
