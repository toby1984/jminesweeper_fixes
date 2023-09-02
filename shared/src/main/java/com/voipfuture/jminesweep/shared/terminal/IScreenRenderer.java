package com.voipfuture.jminesweep.shared.terminal;

/**
 * Renders text to a virtual terminal.
 *
 * <p>The terminal is assumed to have a fixed (but unknown) size
 * and may or may not offer support to apply
 * styling (like different colors etc) to displayed text.</p>
 * <br />
 * <p>The terminal keeps track of a virtual cursor and will
 * print any text starting at the current cursor position.</p>
 * <br />
 * The coordinate system (columns and rows) has its origin at
 * the top-left corner of the screen with the first row/column
 * having index zero.
 *
 * @author tobias.gierke@voipfuture.com
 */
public interface IScreenRenderer
{
    /**
     * Render text starting at the current cursor position.
     *
     * @param s text to print
     * @return this instance (for chaining)
     */
    IScreenRenderer print(String s);

    /**
     * Clears the screen.
     *
     * @return this instance (for chaining)
     */
    IScreenRenderer clearScreen();

    /**
     * Moves the cursor to a given position on the screen.
     *
     * @param x column (first column has index 0)
     * @param y row (first row has index 0)
     * @return this instance (for chaining)
     */
    IScreenRenderer moveCursor(int x, int y);

    /**
     * Set whether the terminal should display a cursor
     * at the current cursor position.
     *
     * @param visible whether to display the cursor
     * @return this instance (for chaining)
     */
    IScreenRenderer setCursorVisible(boolean visible);

    /**
     * Sets the text foreground color.
     *
     * @param color color to use
     * @return this instance (for chaining)
     */
    IScreenRenderer setForegroundColor(ANSIScreenRenderer.Color color);

    /**
     * Sets the text background color.
     *
     * @param color color to use
     * @return this instance (for chaining)
     */
    IScreenRenderer setBackgroundColor(ANSIScreenRenderer.Color color);

    /**
     * Print text left-to-right starting at a given cursor location.
     *
     * Text clipping the right border of the screen may get clipped or wrap
     * to the next line, depending on the actual implementation.
     *
     * @param text text to print
     * @param x (first column has index 0)
     * @param y row (first row has index 0)
     * @return this instance (for chaining)
     */
    IScreenRenderer printTextAt(String text, int x, int y);

    /**
     * Print text left-to-right starting at a given cursor location using
     * a specific foreground color.
     *
     * Text clipping the right border of the screen may get clipped or wrap
     * to the next line, depending on the actual implementation.
     *
     * @param text text to print
     * @param color foreground color
     * @param x (first column has index 0)
     * @param y row (first row has index 0)
     * @return this instance (for chaining)
     */
    IScreenRenderer printTextAtWithColor(String text, ANSIScreenRenderer.Color color, int x, int y);

    /**
     * Print text left-to-right starting at the cursor position using
     * a specific foreground color.
     *
     * Text clipping the right border of the screen may get clipped or wrap
     * to the next line, depending on the actual implementation.
     *
     * @param text text to print
     * @param color foreground color
     * @return this instance (for chaining)
     * @see #moveCursor(int, int)
     */
    IScreenRenderer printTextWithColor(String text, ANSIScreenRenderer.Color color);

    /**
     * Returns this screen's current content.
     * @return screen content, never <code>null</code>
     */
    String getScreenContents();
}
