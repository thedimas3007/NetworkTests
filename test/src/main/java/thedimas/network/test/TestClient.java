package thedimas.network.test;

import thedimas.network.client.Client;
import thedimas.network.client.events.ClientConnectedEvent;
import thedimas.network.client.events.ClientDisconnectedEvent;
import thedimas.network.client.events.ClientReceivedEvent;
import thedimas.network.packet.*;

import java.io.IOException;

import static thedimas.network.Main.logger;

public class TestClient {
    public static void main(String[] args) throws IOException {
        Client client = new Client("127.0.0.1", 9999, true);

        client.onEvent(ClientConnectedEvent.class, clientConnectedEvent -> {
            logger.info("Connected");
            new Thread(() -> {
                while (true) {
                    client.send(new KeepAlivePacket());
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException ignored) {
                    }
                }
            }).start();
        });

        client.onEvent(ClientReceivedEvent.class, clientReceivedEvent -> {
            logger.info("New packet: " + clientReceivedEvent.getPacket().toString());
        });

        client.onEvent(ClientDisconnectedEvent.class, clientDisconnectedEvent -> {
            logger.info("Disconnected: " + clientDisconnectedEvent.getReason());
        });

        client.connect();
    }
}
