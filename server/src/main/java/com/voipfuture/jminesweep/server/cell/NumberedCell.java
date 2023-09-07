package com.voipfuture.jminesweep.server.cell;

public class NumberedCell extends GameCell {

    public NumberedCell(char revealedChar, GameBoard board) {
        super(revealedChar, board);
    }

    @Override
    void triggerOnFlagEffects() {
    }

    @Override
    void triggerOnUnflagEffects() {
        board.updateGameStateIfWon();
    }

    @Override
    void triggerSelectEffects() {
    }

}
