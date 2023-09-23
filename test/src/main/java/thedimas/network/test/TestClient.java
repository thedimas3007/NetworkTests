package thedimas.network.test;

import thedimas.network.client.Client;
import thedimas.network.client.events.ClientConnectedEvent;
import thedimas.network.packet.*;

import java.io.IOException;
import java.util.logging.Level;

import static thedimas.network.Main.logger;

public class TestClient {
    public static void main(String[] args) throws IOException {
        Client client = new Client("127.0.0.1", 9999);

        client.onEvent(ClientConnectedEvent.class, clientConnectedEvent -> {
            try {
                client.<String>request(new ObjectPacket<>("some test"), s -> logger.info("Resp: " + s));

                client.<Long>request(new PingPacket(System.currentTimeMillis()), l -> logger.info("Spent " + (System.currentTimeMillis() - l) / 2 + "ms"));

            } catch (IOException e) {
                logger.log(Level.WARNING, "Holy hell", e);
            }
        });

        client.connect();
//        client.disconnect();
        while (true) {
        }
    }
}
