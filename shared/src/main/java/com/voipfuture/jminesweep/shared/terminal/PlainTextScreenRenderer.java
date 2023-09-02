package com.voipfuture.jminesweep.shared.terminal;

import java.util.Arrays;

/**
 * A {@link IScreenRenderer} for debugging purposes that renders plain text without any control sequences.
 *
 * @author tobias.gierke@voipfuture.com
 */
public class PlainTextScreenRenderer implements IScreenRenderer
{
    private final int cols;
    private final int rows;
    private final char[] buffer;
    private boolean cursorVisible = true;
    private int cursorX,cursorY;

    private ANSIScreenRenderer.Color foregroundColor = ANSIScreenRenderer.Color.COLOR_WHITE;
    private ANSIScreenRenderer.Color backgroundColor = ANSIScreenRenderer.Color.COLOR_BLACK;

    /**
     * Create instance with a
     * @param cols screen width in columns
     * @param rows screen height in rows
     */
    public PlainTextScreenRenderer(int cols, int rows)
    {
        this.cols = cols;
        this.rows = rows;
        this.buffer = new char[cols*rows];
        clearScreen();
    }

    /**
     * Only overridden to aid in debugging. Use {@link #getScreenContents} otherwise.
     */
    @Override
    public String toString()
    {
        return getScreenContents();
    }

    @Override
    public String getScreenContents()
    {
        final StringBuilder result = new StringBuilder();
        for ( int y = 0 ; y < rows ; y++) {
            for ( int x = 0 ; x < cols ; x++ ) {
                final char c = buffer[x + y * cols];
                if ( x == cursorX && y == cursorY ) {
                    if ( c == ' ' || Character.toLowerCase( c ) == c ) {
                        result.append( 'X' );
                    }
                    else
                    {
                        result.append( Character.toLowerCase( c ) );
                    }
                } else
                {
                    result.append( c );
                }
            }
            if ( (y+1) < rows )
            {
                result.append( "\n" );
            }
        }
        return result.toString();
    }

    @Override
    public IScreenRenderer print(String s)
    {
        return printTextAt( s, cursorX, cursorY, true );
    }

    @Override
    public IScreenRenderer clearScreen()
    {
        Arrays.fill( buffer, '#' );
        return this;
    }

    @Override
    public IScreenRenderer moveCursor(int x, int y)
    {
        cursorX = x;
        cursorY = y;
        return this;
    }

    @Override
    public IScreenRenderer setCursorVisible(boolean visible)
    {
        cursorVisible = visible;
        return this;
    }

    @Override
    public IScreenRenderer setForegroundColor(ANSIScreenRenderer.Color color)
    {
        foregroundColor = color;
        return this;
    }

    @Override
    public IScreenRenderer setBackgroundColor(ANSIScreenRenderer.Color color)
    {
        backgroundColor = color;
        return this;
    }

    private IScreenRenderer printTextAt(String text, int x, int y, boolean updateCursorPosition)
    {
        int ptr = x + y * cols;
        for ( char c : text.toCharArray() ) {
            buffer[ptr++] = c;
        }
        if ( updateCursorPosition ) {
            int newY = ptr / cols;
            int newX = ptr - (newY*cols);
            cursorX = newX;
            cursorY = newY;
        }
        return this;
    }

    @Override
    public IScreenRenderer printTextAt(String text, int x, int y)
    {
        return printTextAt( text, x, y, true );
    }

    @Override
    public IScreenRenderer printTextAtWithColor(String text, ANSIScreenRenderer.Color color, int x, int y)
    {
        setForegroundColor( color );
        return printTextAt( text, x, y, true );
    }

    @Override
    public IScreenRenderer printTextWithColor(String text, ANSIScreenRenderer.Color color)
    {
        setForegroundColor( color );
        return printTextAt( text, cursorX, cursorY, true );
    }
}