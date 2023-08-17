package thedimas.network.test;

import thedimas.network.enums.DcReason;
import thedimas.network.packet.Packet;
import thedimas.network.server.Server;
import thedimas.network.server.ServerClientHandler;
import thedimas.network.server.ServerListener;

import java.io.IOException;
import java.util.Scanner;

import static thedimas.network.Main.logger;

public class TestServer {
    public static void main(String[] args) throws IOException {
        Server server = new Server(9999);
        server.addListener(new ServerListener() {
            @Override
            public void started() {
                logger.fine("Server started");
            }

            @Override
            public void connected(ServerClientHandler client) {
                logger.fine("New connection");
            }

            @Override
            public void received(ServerClientHandler client, Packet packet) {
                logger.fine("New packet");
            }

            @Override
            public void disconnected(ServerClientHandler client, DcReason reason) {
                logger.fine("New disconnection");
            }

            @Override
            public void stopped() {
                logger.fine("Server stopped");
            }
        });
        new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.hasNext() && scanner.next().equals("exit")) {
                server.stop();
                logger.warning("Server closed");
                break;
            }
        }
    }
}
