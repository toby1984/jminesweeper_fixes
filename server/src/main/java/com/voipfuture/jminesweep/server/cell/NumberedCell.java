package com.voipfuture.jminesweep.server.cell;

public class NumberedCell extends GameCell {

    public NumberedCell(char revealedChar) {
        super(revealedChar);
    }

    @Override
    void triggerSelectEffects(GameBoard board) {
    }

}
