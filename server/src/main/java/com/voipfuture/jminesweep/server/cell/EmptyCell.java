package com.voipfuture.jminesweep.server.cell;

import java.util.ArrayList;
import java.util.List;

public class EmptyCell extends GameCell {
    public EmptyCell(GameBoard board, int[] coordinates) {
        super(EMPTY_CELL_ICON, coordinates, board);
    }

    @Override
    public void triggerOnFlagEffects() {
    }

    @Override
    public void triggerOnUnflagEffects() {
        this.board.updateGameStateIfWon();
    }

    @Override
    public void triggerSelectEffects() {
        // An empty cell triggers the select effect recursively to its surrounding
        findSurroundingCells().forEach(GameCell::select);
    }

}
