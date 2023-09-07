package com.voipfuture.jminesweep.server.cell;

public class BombCell extends GameCell {
    public BombCell() {
        super(BOMB_CELL_ICON);
    }

    @Override
    void triggerOnFlagEffects(GameBoard board) {
        board.updateGameStateIfWon();
    }

    @Override
    void triggerSelectEffects(GameBoard board) {
        board.setGameState(GameBoard.GameState.LOST);
    }
}
