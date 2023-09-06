package com.voipfuture.jminesweep.server.cell;

public class BombCell extends GameCell {
    public BombCell() {
        super(BOMB_CELL_ICON);
    }

    @Override
    void triggerSelectEffects(GameBoard board) {
        // Game over
    }
}
