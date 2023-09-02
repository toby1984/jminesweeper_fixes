package com.voipfuture.jminesweep.shared;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.HexFormat;
import org.apache.commons.lang3.Validate;

/**
 * Utility methods.
 *
 * @author tobias.gierke@voipfuture.com
 */
public class Utils
{
    /**
     * Close a {@link Closeable} while swallowing any exceptions.
     *
     * Do NOT use this method if you need to know whether any pending
     * changes could be successfully flushed before actually closing the underlying
     * resource.
     *
     * @param c resource to close, may be <code>null</code>
     */
    public static void closeQuietly(Closeable c) {
        try {
            if ( c != null )
            {
                c.close();
            }
        }
        catch( IOException ignored ) {}
    }

    /**
     * Helper method that converts a byte array into a hex dump.
     *
     * @param data binary data to process
     * @return hexdump with 16 bytes per line
     */
    public static String hexdump(byte[] data)
    {
        Validate.notNull( data, "data must not be null" );

        final int bytesPerRow = 16;
        final HexFormat hexFormat = HexFormat.ofDelimiter(" ");

        final StringBuilder result = new StringBuilder();
        for ( int ptr = 0 ; ptr < data.length ; )
        {
            result.append( hexFormat.toHexDigits( (short) ptr ) ).append(": ");
            final int maxPtr = Math.min( data.length, ptr + bytesPerRow);
            final byte[] bytes = Arrays.copyOfRange( data, ptr, maxPtr );
            ptr += bytes.length;

            result.append( hexFormat.formatHex( bytes ) ).append( " " );
            for ( byte b : bytes ) {
                result.append( ( b >= 32 && b < 127 ) ? (char) (b&0xff) : '?');
            }
        }
        return result.toString();
    }

    /**
     * Converts an integer value into its big-endian byte representation.
     *
     * @param value value to convert
     * @return bytes (big-endian)
     * @see #netToInt(byte[])
     */
    public static byte[] intToNet(int value) {
        final byte[] result = new byte[4];
        result[0] = (byte) (value >>> 24);
        result[1] = (byte) (value >>> 16);
        result[2] = (byte) (value >>>  8);
        result[3] = (byte) (value       );
        return result;
    }

    /**
     * Converts four bytes into a 32-bit integer (assuming big-endian).
     *
     * @param data array with length 4
     * @return integer value
     * @see #intToNet(int)
     */
    public static int netToInt(byte[] data) {
        Validate.notNull( data, "data must not be null" );
        Validate.isTrue( data.length == 4 );
        return (data[0] & 0xff) << 24 |
            (data[1] & 0xff) << 16 |
            (data[2] & 0xff) << 8 |
            (data[3] & 0xff);
    }
}
