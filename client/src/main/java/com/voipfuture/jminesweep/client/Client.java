package com.voipfuture.jminesweep.client;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import com.voipfuture.jminesweep.shared.Constants;
import com.voipfuture.jminesweep.shared.Difficulty;
import com.voipfuture.jminesweep.shared.Direction;
import com.voipfuture.jminesweep.shared.NetworkPacketType;
import com.voipfuture.jminesweep.shared.Utils;
import com.voipfuture.jminesweep.shared.terminal.ANSIScreenRenderer;

/**
 * VT100 console game client.
 *
 * Note that this client only works on POSIX-compliant systems
 * (more specifically Linux,tested on Ubuntu 22.04 LTS) as it
 * relies on native code (POSIX termios.h) to switch
 * the console into unbuffered mode (single key presses can be read instead of whole lines).
 * <b>This may or may NOT work on MacOS, since I don't own a Mac I don't know</b>.
 *
 * @author tobias.gierke@voipfuture.com
 */
public class Client
{
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger( Client.class );

    private final PrintWriter consoleWriter;

    private InetAddress serverAddress;

    private int cols = -1;
    private int rows = -1;
    private Difficulty difficulty;

    private Socket socket;
    private InputStream input;
    private OutputStream output;

    private Client()
    {
        if ( System.console() != null )
        {
            this.consoleWriter = System.console().writer();
        } else {
            this.consoleWriter = new PrintWriter( System.out );
        }
    }

    private static void printHelpAndExit() {
        System.err.println("Invalid command line. Usage: [-d] [-l <difficulty, 1,2 or 3>] [-r <rows>] [-c <columns>] <host>");
        System.exit(1);
    }

    public static void main(String[] arguments) throws IOException
    {
        final Iterator<String> it = Arrays.asList( arguments ).iterator();
        if ( ! it.hasNext() ) {
            printHelpAndExit();
        }
        final Client client = new Client();
        while ( it.hasNext() )
        {
            final String arg = it.next();
            switch( arg ) {
                case "-help", "--help" -> printHelpAndExit();
                case "-l", "--level" -> client.difficulty = numberToDifficulty( Integer.parseInt( it.next() ) );
                case "-r", "--rows" -> client.rows = Integer.parseInt( it.next() );
                case "-c", "--columns" -> client.cols = Integer.parseInt( it.next() );
                default -> {
                    if ( client.serverAddress != null ) {
                        printHelpAndExit();
                    }
                    client.serverAddress = InetAddress.getByName( arg );
                }
            }
        }
        client.start();
    }

    private static Difficulty numberToDifficulty(int number) {
        return switch( number) {
            case 1 -> Difficulty.EASY;
            case 2 -> Difficulty.MEDIUM;
            case 3 -> Difficulty.HARD;
            default -> throw new IllegalArgumentException( "Invalid difficulty "+number );
        };
    }

    public void start() throws IOException
    {
        if ( difficulty == null )
        {
            difficulty = numberToDifficulty( ConsoleHelper.askNumber( "Please choose a difficulty( 1 = easy, 2 = medium, 3 = hard): ", 1, 3 ) );
        }
        if (  cols == -1 )
        {
            cols = ConsoleHelper.askNumber( "Number of playing field columns: ", 1, 255 );
        }
        if ( rows == -1 )
        {
            rows = ConsoleHelper.askNumber( "Number of playing field columns: ", 1, 255 );
        }

        ConsoleHelper.adjustTerminalSettings();
        Runtime.getRuntime().addShutdownHook( new Thread( ConsoleHelper::restoreDefaultTerminalSettings ) );

        connect();
        sendStartPacket( cols, rows, difficulty );

        // main loop
        try
        {
outer:
            while (true)
            {
                final ConsoleHelper.Key key = ConsoleHelper.readKey();
                LOG.debug( "GOT: " + key );
                switch( key )
                {
                    case EOF -> {
                        break outer;
                    }
                    case X -> toggleDebugMode();
                    case W -> move( Direction.UP );
                    case A -> move( Direction.LEFT );
                    case S -> move( Direction.DOWN );
                    case D -> move( Direction.RIGHT );
                    case SPACE -> toggleBombMark();
                    case ENTER -> reveal();
                    case TAB ->
                    {
                        LOG.info( "User wants to quit." );
                        break outer;
                    }
                }
            }
        }
        finally
        {
            disconnect();
        }
    }

    private void reveal() throws IOException {
        sendAndRenderScreen( NetworkPacketType.REVEAL );
    }

    private void toggleBombMark() throws IOException {
        sendAndRenderScreen( NetworkPacketType.TOGGLE_BOMB_MARK );
    }

    private void toggleDebugMode() throws IOException {
        sendAndRenderScreen( NetworkPacketType.TOGGLE_DEBUG_MODE );
    }

    private void move(Direction direction) throws IOException
    {
        sendAndRenderScreen( switch(direction) {
            case LEFT -> NetworkPacketType.MOVE_LEFT;
            case RIGHT -> NetworkPacketType.MOVE_RIGHT;
            case UP -> NetworkPacketType.MOVE_UP;
            case DOWN -> NetworkPacketType.MOVE_DOWN;
        } );
    }

    private void sendStartPacket(int cols, int rows, Difficulty diff) throws IOException
    {
        LOG.debug("Game settings: cols = "+cols+" , rows="+rows+", difficulty="+diff);
        final ByteArrayOutputStream pkt = new ByteArrayOutputStream();
        pkt.write( NetworkPacketType.START_GAME.id & 0xff );
        pkt.write( Utils.intToNet( 3 ) );
        pkt.write( cols  );
        pkt.write( rows );
        pkt.write( switch( diff ) {
            case EASY -> 1;
            case MEDIUM -> 2;
            case HARD -> 3;
        } );
        sendAndRenderScreen( pkt.toByteArray() );
    }

    private void disconnect()
    {
        if ( System.console() != null ) {
            final PrintWriter writer = System.console().writer();
            writer.write( new ANSIScreenRenderer().setCursorVisible( true ).getScreenContents() );
            writer.flush();
        }
        if ( socket != null && socket.isConnected() && ! socket.isClosed() && output != null) {
            try
            {
                output.write( NetworkPacketType.QUIT.id & 0xff );
            } catch(IOException ignored) {}
        }
        Utils.closeQuietly( socket );
        Utils.closeQuietly( input );
        Utils.closeQuietly( output );
        socket = null;
        input = null;
        output = null;
    }

    private void connect() throws IOException {

        System.out.println("Connecting to "+serverAddress+", port "+Constants.SERVER_TCP_PORT +"/tcp");
        boolean success = false;
        try
        {
            socket = new Socket( serverAddress, Constants.SERVER_TCP_PORT );
            input = socket.getInputStream();
            output = socket.getOutputStream();
            System.out.println("Connection successful.");
            success = true;
        }
        finally
        {
            if ( !success )
            {
                disconnect();
            }
        }
    }

    private void sendAndRenderScreen(NetworkPacketType packetType) throws IOException {
        sendAndRenderScreen(new byte[] {(byte) packetType.id } );
    }

    private void sendAndRenderScreen(byte[] data) throws IOException
    {
        output.write( data );

        int typeId = input.read();
        if ( typeId == -1 ) {
            throw new EOFException();
        }
        final NetworkPacketType packetType = NetworkPacketType.fromID( typeId & 0xff );
        if ( packetType != NetworkPacketType.SCREEN_CONTENT )  {
            LOG.error( "Server responded with packet type " + packetType + ", expected SCREEN_CONTENT" );
        }
        // read length
        final byte[] payloadLen = new byte[4];
        int read = input.read( payloadLen );
        if ( read != 4 ) {
            throw new EOFException( "Expected to read 4 bytes but got " + read );
        }
        final byte[] payload = new byte[Utils.netToInt( payloadLen )];
        if ( payload.length > 0 ) {
            read = input.read( payload );
            if ( read != payload.length ) {
                throw new EOFException( "Expected to read "+payload.length+" bytes but got " + read );
            }
        }
        final String screenContent = new String( payload, StandardCharsets.UTF_8 );
        consoleWriter.write( screenContent );
        consoleWriter.flush();
    }
}