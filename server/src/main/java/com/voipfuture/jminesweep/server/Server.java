package com.voipfuture.jminesweep.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import com.voipfuture.jminesweep.shared.Constants;
import com.voipfuture.jminesweep.shared.NetworkPacketType;
import com.voipfuture.jminesweep.shared.Utils;

public class Server
{
    public static void main(String[] args) throws IOException
    {
        final ServerSocket socket = new ServerSocket( Constants.SERVER_TCP_PORT );
        // enable binding to the port even though its still in state TIME_WAIT
        // from a previous program run
        socket.setReuseAddress( true );
        while( true ) {
            final Socket clientSocket = socket.accept();
            try ( var out = clientSocket.getOutputStream() ) {
                out.write( NetworkPacketType.SCREEN_CONTENT.id );
                final byte[] screenContent = "*** Server not implemented yet. ***".getBytes( StandardCharsets.UTF_8 );
                out.write( Utils.intToNet( screenContent.length ) );
                out.write( screenContent );
            }
        }
    }
}
