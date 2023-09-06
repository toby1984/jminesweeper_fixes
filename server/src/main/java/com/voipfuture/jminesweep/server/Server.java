package com.voipfuture.jminesweep.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import com.voipfuture.jminesweep.server.cell.GameBoard;
import com.voipfuture.jminesweep.shared.Constants;
import com.voipfuture.jminesweep.shared.Difficulty;
import com.voipfuture.jminesweep.shared.NetworkPacketType;
import com.voipfuture.jminesweep.shared.Utils;

public class Server {
    private static GameBoard board;

    public static void main(String[] args) throws IOException {
        final ServerSocket socket = new ServerSocket(Constants.SERVER_TCP_PORT);
        // enable binding to the port even though it's still in state TIME_WAIT
        // from a previous program run
        socket.setReuseAddress(true);
        while (true) {
            final Socket clientSocket = socket.accept();
            System.out.println("Received client");
            try (var in = clientSocket.getInputStream()) {
                executePacket(in.readNBytes(8));
                try (var out = clientSocket.getOutputStream()) {
                    out.write(NetworkPacketType.SCREEN_CONTENT.id);
                    final byte[] screenContent = board.render().getBytes(StandardCharsets.UTF_8);
                    out.write(Utils.intToNet(screenContent.length));
                    out.write(screenContent);
                    System.out.println("Sent screen");
                }
            }
        }
    }

    private static void executePacket(byte[] bytes) {
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
            case QUIT -> System.exit(0);
        }
    }

}
