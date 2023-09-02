package com.voipfuture.jminesweep.client;

import java.io.BufferedReader;
import java.io.Console;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.SegmentScope;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.IntFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.voipfuture.jminesweep.client.nativelinux.termios;
import com.voipfuture.jminesweep.client.nativelinux.termios_h;

/**
 * Helper class that hides the nitty-gritty details of dealing with POSIX termios.h and
 * terminal console emulator quirks.
 *
 * @author tobias.gierke@voipfuture.com
 */
public class ConsoleHelper
{
    private static final Logger LOG = LogManager.getLogger( ConsoleHelper.class );

    private static final int FD_STDIN = 0;

    public enum Key
    {
        W(0x77), // W
        A(0x61), // A
        S(0x73), // S
        D(0x64), // D
        SPACE(0x20), // SPACE
        ENTER(0x0a), // ENTER
        X(0x78), // X
        TAB(0x09), // TAB
        EOF(0x00), // not a key, indicates we received EOF from the console
        ;
        public final int keyCode;

        Key(int keyCode)
        {
            this.keyCode = keyCode;
        }
    }

    /**
     * Read a single key press from the console.
     *
     * @return key
     * @throws IOException
     */
    public static Key readKey() throws IOException
    {
        while ( true )
        {
            final int value;
            if ( System.console() != null ) {
                value = System.console().reader().read();
            } else
            {
                String line = new BufferedReader( new InputStreamReader( System.in ) ).readLine();
                if ( line == null ) {
                    value = -1;
                } else if ( line.length() == 0 ){
                    continue;
                } else {
                    value = line.charAt(0);
                }
            }
            LOG.debug( "Got key code 0x" + Integer.toHexString( value & 0xff ) );
            if ( value == -1 )
            {
                return Key.EOF;
            }
            final Optional<Key> match = Arrays.stream( Key.values() ).filter( x -> x.keyCode == value ).findFirst();
            if (match.isPresent() ) {
                return match.get();
            }
        }
    }

    /**
     * Prints a question to the console and waits for the user to enter a
     * number in a specific range.
     *
     * @param question question to print
     * @param minInclusive min. number the user may enter
     * @param maxInclusive max. number the user may enter
     * @return number within the given range
     * @throws IOException thrown if the user closes the console instead of answering
     */
    public static int askNumber(String question,int minInclusive, int maxInclusive) throws IOException
    {
        while( true )
        {
            System.out.print( "\n"+question );
            final Console con = System.console();
            final String line;
            if ( con != null )
            {
                line = con.readLine();
            } else {
                line = new BufferedReader( new InputStreamReader( System.in ) ).readLine();
            }
            if ( line == null )
            {
                throw new EOFException();
            }
            try
            {
                int result = Integer.parseInt( line.trim() );
                if ( result >= minInclusive && result <= maxInclusive )
                {
                    return result;
                }
            }
            catch( NumberFormatException ignored ) {}
            System.err.println( "Please enter a number in the range ["+minInclusive+","+maxInclusive+"]" );
        }
    }

    /**
     * Switches the console to unbuffered mode and disables local echo.
     */
    public static void adjustTerminalSettings() {
        if ( System.console() != null )
        {
            changeTerminalMode( false );
            changeTerminalEcho( false );
        }
    }

    /**
     * Switches the console to buffered mode and enables local echo.
     */
    public static void restoreDefaultTerminalSettings() {
        if ( System.console() != null )
        {
            changeTerminalMode( true );
            changeTerminalEcho( true );
        }
    }

    private static void changeTerminalMode(boolean buffered) {
        if ( buffered )
        {
            set_c_lflags( termios_h.ICANON() );
        }
        else
        {
            // turning off canonical mode makes input unbuffered
            clear_c_lflags( termios_h.ICANON() );
        }
    }

    private static void changeTerminalEcho(boolean echoEnabled) {
        if ( echoEnabled ) {
            set_c_lflags( termios_h.ECHO() );
        } else {
            clear_c_lflags( termios_h.ECHO() );
        }
    }

    private static void clear_c_lflags(int bitmask) {
        changec_lflag( flags -> flags & ~bitmask );
    }

    private static void set_c_lflags(int bitmask) {
        changec_lflag( flags -> flags | bitmask );
    }

    private static void changec_lflag(IntFunction<Integer> updateFunc) {
        final SegmentAllocator allocator = SegmentAllocator.nativeAllocator( SegmentScope.auto() );
        final MemorySegment segment = allocator.allocate( termios.sizeof() );
        termios_h.tcgetattr( FD_STDIN, segment);

        final int flags = termios.c_lflag$get( segment );
        termios.c_lflag$set( segment, updateFunc.apply( flags ) );

        termios_h.tcsetattr( FD_STDIN, termios_h.TCSANOW(), segment );
    }
}
