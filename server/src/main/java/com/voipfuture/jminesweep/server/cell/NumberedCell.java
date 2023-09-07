package com.voipfuture.jminesweep.server.cell;

public class NumberedCell extends GameCell {

    public NumberedCell(char revealedChar, GameBoard board, int[] coordinates) {
        super(revealedChar, coordinates, board);
    }

    @Override
    public void triggerOnFlagEffects() {
    }

    @Override
    public void triggerOnUnflagEffects() {
        board.updateGameStateIfWon();
    }

    @Override
    public void triggerSelectEffects() {
    }

}
