package com.voipfuture.jminesweep.server.cell;

public class BombCell extends GameCell {
    public BombCell(GameBoard board, int[] coordinates) {
        super(BOMB_CELL_ICON, coordinates, board);
    }

    @Override
    public void triggerOnFlagEffects() {
        this.board.updateGameStateIfWon();
    }

    @Override
    public void triggerOnUnflagEffects() {
    }

    @Override
    public void triggerSelectEffects() {
        this.board.setGameState(GameState.LOST);
    }
}
