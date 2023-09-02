package com.voipfuture.jminesweep.shared;

import java.util.Arrays;

/**
 * Network packet types.
 *
 * Network packets come in two flavors, with and without payload data.
 *
 * <br /><br /><p>1.) Network packets that never carry any payload data consist
 * of only a single byte indicating the packet's type</p>
 * <br />
 * <table style="border:1px solid black">
 *     <thead>
 *     <tr>
 *         <th>Offset</th>
 *         <th>Length in bytes</th>
 *         <th>Description</th>
 *     </tr>
 *     </thead>
 *     <tbody>
 *     <tr>
 *         <td>0000</td>
 *         <td>1</td>
 *         <td>{@link NetworkPacketType#id}</td>
 *     </tr>
 *     </tbody>
 * </table>
 *
 * <br /><br /><p>2.) Network packets that may carry additional payload data consist
 * of a single byte indicating the packet's type followed by a 32-byte value (big endian)
 * indicating the number of payload bytes that follow (with a length of zero being a valid value).</p>
 * <br />
 *
 * <table style="border:1px solid black">
 *     <thead>
 *     <tr>
 *         <th>Offset</th>
 *         <th>Length in bytes</th>
 *         <th>Description</th>
 *     </tr>
 *     </thead>
 *     <tbody>
 *     <tr>
 *         <td>0000</td>
 *         <td>1</td>
 *         <td>{@link NetworkPacketType#id}</td>
 *     </tr>
 *     <tr>
 *         <td>0001</td>
 *         <td>4</td>
 *         <td>Payload length (big-endian), may be zero</td>
 *     </tr>
 *     <tr>
 *         <td>0005</td>
 *         <td>variable</td>
 *         <td>Payload data</td>
 *     </tr>
 *     </tbody>
 * </table>
 *
 * @author tobias.gierke@voipfuture.com
 */
public enum NetworkPacketType
{
    /*
     * Sent from client to server to move the cursor left by 1 cell.
     * Server must always respond with a {@link #SCREEN_CONTENT} packet.
     */
    MOVE_LEFT( 1, false ),
    /*
     * Sent from client to server to move the cursor right by 1 cell.
     * Server must always respond with a {@link #SCREEN_CONTENT} packet.
     */
    MOVE_RIGHT( 2, false ),
    /*
     * Sent from client to server to move the cursor up by 1 cell.
     * Server must always respond with a {@link #SCREEN_CONTENT} packet.
     */
    MOVE_UP( 3, false ),
    /*
     * Sent from client to server to move the cursor down by 1 cell.
     * Server must always respond with a {@link #SCREEN_CONTENT} packet.
     */
    MOVE_DOWN( 4, false ),
    /* Reveal the cell at the current cursor position.
     * Server must always respond with a {@link #SCREEN_CONTENT} packet.
     */
    REVEAL( 5, false ),
    /*
     * Toggle cell marking at the current cursor position
     * Server must always respond with a {@link #SCREEN_CONTENT} packet.
     */
    TOGGLE_BOMB_MARK( 6, false ),
    /**
     * Start a new game.
     *
     * Payload: 3 bytes, fixed length
     * <ul>
     *   <li>Game field columns (byte)</li>
     *   <li>Game field rows (byte)</li>
     *   <li>Game difficulty (1 =easy / 2 = medium / 3 = hard) (byte)</li>
     * </ul>
     * Server must always respond with a {@link #SCREEN_CONTENT} packet.
     */
    START_GAME( 7, true ),
    /**
     * Quit game and disconnect from the server.
     */
    QUIT( 8, false ),
    /**
     * Screen content to render (send from server to client only).
     * PAYLOAD: variable length
     * <ul>
     *   <li>screen contents to display (UTF-8)</li>
     * </ul>
     */
    SCREEN_CONTENT( 9, true ),
    /*
     * Optional packet type to toggle server-side debug mode.
     * The server may choose to do nothing when receiving a packet of this type
     * <b>but</b> must always respond with a {@link #SCREEN_CONTENT} packet.
     *
     * Server must always respond with a {@link #SCREEN_CONTENT} packet.
     */
    TOGGLE_DEBUG_MODE( 10, false );

    /**
     * Network packet ID (transmitted as a single byte on the wire).
     */
    public final int id;

    /**
     * Whether network packets of this type may have additional payload data.
     */
    public final boolean hasPayload;

    NetworkPacketType(int id, boolean hasPayload)
    {
        this.id = id;
        this.hasPayload = hasPayload;
    }

    /**
     * Convert numerical ID to network packet type object.
     * @param id ID
     * @return packet type
     * @throws IllegalArgumentException if ID is unknown
     */
    public static NetworkPacketType fromID(int id)
    {
        return Arrays.stream( NetworkPacketType.values() ).filter( x -> x.id == (id & 0xff) ).findFirst()
            .orElseThrow( () -> new IllegalArgumentException( "Unknown packet ID " + id ) );
    }
}
