package com.voipfuture.jminesweep.shared.terminal;

import org.apache.commons.lang3.Validate;

/**
 * An {@link IScreenRenderer} that renders text using ANSI sequences.
 *
 * @author tobias.gierke@voipfuture.com
 */
public final class ANSIScreenRenderer implements IScreenRenderer
{
    /**
     * ANSI colors.
     *
     * @author tobias.gierke@voipfuture.com
     */
    public enum Color
    {
        COLOR_BLACK  (30),
        COLOR_RED    (31),
        COLOR_GREEN  (32),
        COLOR_YELLOW (33),
        COLOR_BLUE   (34),
        COLOR_MAGENTA(35),
        COLOR_CYAN   (36),
        COLOR_WHITE  (37),
        DEFAULT  (30),
        RESET  (0),
        ;

        private final int colorCode;

        Color(int colorCode)
        {
            this.colorCode = colorCode;
        }

        public int getForegroundCode() {
            return colorCode;
        }

        public  int getBackgroundCode() {
            return 10+colorCode;
        }
    }

    enum ANSISequence
    {
        CLEAR_SCREEN("\033[2J"),
        MOVE_CURSOR("\033[%d;%dH"),
        PRINT_TEXT_AT("\033[%d;%dH%s"),
        PRINT_TEXT_AT_WITH_COLOR("\033[%d;%dH\u001B[%dm%s"),
        PRINT_WITH_COLOR("\033[%dm%s"),
        SET_COLOR("\033[%dm"),
        SHOW_CURSOR("\033[?25h"),
        HIDE_CURSOR("\033[?25l"),
        ;

        private final String formatString;

        ANSISequence(String formatString)
        {
            this.formatString = formatString;
        }

        public String format(Object... data) {
            return String.format( formatString, data );
        }
    }

    private final StringBuffer buffer = new StringBuffer();

    @Override
    public IScreenRenderer print(String s)
    {
        buffer.append( s );
        return this;
    }

    @Override
    public String getScreenContents()
    {
        return buffer.toString();
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
    public IScreenRenderer clearScreen()
    {
        return print( ANSISequence.CLEAR_SCREEN.format() );
    }

    @Override
    public IScreenRenderer moveCursor(int x, int y)
    {
        Validate.isTrue( x >= 0 );
        Validate.isTrue( y >= 0 );
        return print( ANSISequence.MOVE_CURSOR.format( y+1, x+1 ) );
    }

    @Override
    public IScreenRenderer setCursorVisible(boolean visible)
    {
        return print( (visible ? ANSISequence.SHOW_CURSOR : ANSISequence.HIDE_CURSOR).format() );
    }

    @Override
    public IScreenRenderer setForegroundColor(Color color)
    {
        return print( ANSISequence.SET_COLOR.format( color.getForegroundCode() ) );
    }

    @Override
    public IScreenRenderer setBackgroundColor(Color color)
    {
        return print( ANSISequence.SET_COLOR.format( color.getBackgroundCode() ) );
    }

    @Override
    public IScreenRenderer printTextAt(String text, int x, int y)
    {
        return print( ANSISequence.PRINT_TEXT_AT.format( y+1, x+1, text ) );
    }

    @Override
    public IScreenRenderer printTextAtWithColor(String text, Color color, int x, int y)
    {
        return print( ANSISequence.PRINT_TEXT_AT_WITH_COLOR.format( y+1, x+1, color.getForegroundCode(), text ) );
    }

    @Override
    public IScreenRenderer printTextWithColor(String text, Color color)
    {
        return print( ANSISequence.SET_COLOR.format( color.getForegroundCode() ) + text );
    }
}
