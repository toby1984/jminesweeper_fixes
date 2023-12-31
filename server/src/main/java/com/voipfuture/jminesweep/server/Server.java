package com.voipfuture.jminesweep.server;

import com.voipfuture.jminesweep.server.cell.GameBoard;
import com.voipfuture.jminesweep.server.cell.GameState;
import com.voipfuture.jminesweep.shared.Constants;
import com.voipfuture.jminesweep.shared.Difficulty;
import com.voipfuture.jminesweep.shared.NetworkPacketType;
import com.voipfuture.jminesweep.shared.Utils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    private static GameBoard board;
    private static OutputStream out;
    private static InputStream in;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(Constants.SERVER_TCP_PORT);
        // enable binding to the port even though it's still in state TIME_WAIT
        // from a previous program run
        serverSocket.setReuseAddress(true);

        boolean hasProcessedFirstPayload = false;
outer:
        while (true) {
            clientSocket = serverSocket.accept();
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();

            while (true) {
                // FIXME Ugly hack because something is going wrong when reading the InputStream
                // The payload varies in length only the first one is length = 8
                executePacket(hasProcessedFirstPayload ? in.readNBytes(1) : in.readNBytes(8));
                GameState gameState = board.getGameState();
                final byte[] gameGUI = board.render().getBytes(StandardCharsets.UTF_8);
                out.write(NetworkPacketType.SCREEN_CONTENT.id);
                out.write( Utils.intToNet( gameGUI.length ) );
                out.write(gameGUI);
                out.flush();
                hasProcessedFirstPayload = true;
                if (gameState != GameState.ONGOING) {
                    break outer;
                }
            }
        }
        exitGame();
    }

    private static void executePacket(byte[] bytes) throws IOException {
        if (bytes.length == 0) {
            return;
        }
        int packetId = bytes[0];
        switch (NetworkPacketType.fromID(packetId)) {
            case START_GAME -> {
                int xSize = bytes[5];
                int ySize = bytes[6];
                if (xSize == 0 || ySize == 0) {
                    return;
                }
                Difficulty difficulty = Difficulty.values()[bytes[7]];
                board = new GameBoard(xSize, ySize, difficulty);
            }
            case MOVE_DOWN -> board.moveCursorDown();
            case MOVE_LEFT -> board.moveCursorLeft();
            case MOVE_RIGHT -> board.moveCursorRight();
            case MOVE_UP -> board.moveCursorUp();
            case TOGGLE_BOMB_MARK -> board.toggleBombMark();
            case REVEAL -> board.reveal();
            case QUIT -> exitGame();
            default -> {}
        }
    }

    private static void exitGame() throws IOException {
        clientSocket.close();
        serverSocket.close();
        System.exit(0);
    }

}
