package thedimas.network.test;

import thedimas.network.packet.*;
import thedimas.network.server.Server;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static thedimas.network.Main.logger;

public class TestServer {
    public static void main(String[] args) throws IOException {
        Server server = new Server(9999);

        server.onRequest(ObjectPacket.class, (serverClientHandler, id, objectPacket) -> {
            logger.info(serverClientHandler.getIp() + ": " + objectPacket.toString());
            try {
                serverClientHandler.response(id, "Lol");
            } catch (IOException e) {
                logger.log(Level.WARNING, "no no no", e);
            }
        });

        server.onRequest(PingPacket.class, (serverClientHandler, id, pingPacket) -> {
            try {
                serverClientHandler.response(id, pingPacket.getCreated());
            } catch (IOException e) {
                logger.log(Level.WARNING, "no no no", e);
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
