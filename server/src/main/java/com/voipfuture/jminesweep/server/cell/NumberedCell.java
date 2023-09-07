package com.voipfuture.jminesweep.server.cell;

public class NumberedCell extends GameCell {

    public NumberedCell(char revealedChar) {
        super(revealedChar);
    }

    @Override
    void triggerOnFlagEffects(GameBoard board) {
    }

    @Override
    void triggerSelectEffects(GameBoard board) {
    }

}
