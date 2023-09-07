package com.voipfuture.jminesweep.server.cell;

public class BombCell extends GameCell {
    public BombCell(GameBoard board) {
        super(BOMB_CELL_ICON, board);
    }

    @Override
    void triggerOnFlagEffects() {
        this.board.updateGameStateIfWon();
    }

    @Override
    void triggerOnUnflagEffects() {
    }

    @Override
    void triggerSelectEffects() {
        this.board.setGameState(GameState.LOST);
    }
}
