package thedimas.network.test;

import thedimas.network.packet.*;
import thedimas.network.server.Server;
import thedimas.network.server.events.*;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static thedimas.network.Main.logger;

public class TestServer {
    public static void main(String[] args) throws IOException {
        Server server = new Server(9999);

        server.onEvent(ServerStartedEvent.class, event -> {
            logger.info("Server started");
        });

        server.onEvent(ServerStoppedEvent.class, event -> {
            logger.info("Server stopped");
        });

        server.onEvent(ServerClientConnectedEvent.class, event -> {
            logger.info("New client: " + event.getClient().getIp());
        });

        server.onEvent(ServerClientDisconnectedEvent.class, event -> {
            logger.info("Client " + event.getClient().getIp() +  " disconnected: " + event.getReason());
        });

        server.onEvent(ServerReceivedEvent.class, event -> {
            logger.info("Client " + event.getClient().getIp() +  " sent a packet: " + event.getPacket());
        });

        server.onPacket(KeepAlivePacket.class, (client, packet) -> {
            logger.info("Client " + client.getIp() + " wants to be alive");
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
