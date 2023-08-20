package thedimas.network.test;

import thedimas.network.client.Client;
import thedimas.network.client.ClientListener;
import thedimas.network.enums.DcReason;
import thedimas.network.packet.Packet;
import thedimas.network.util.Bytes;

import java.io.IOException;
import java.util.logging.Level;

import static thedimas.network.Main.logger;

public class TestClient {
    public static void main(String[] args) throws IOException {
        Client client = new Client("127.0.0.1", 9999);
        client.addListener(new ClientListener() {
            @Override
            public void connected() {

            }

            @Override
            public void received(Packet packet) {
                try {
                    if (packet instanceof SaltPacket saltPacket) {
                        byte[] password = Bytes.hashed(Bytes.combine(saltPacket.getSalt(), "somepasswd".getBytes()));
                        client.send(new AuthPacket(password));
                    }
                } catch (IOException e) {
                    logger.log(Level.FINE, "Unable to send AuthPacket", e);
                }
            }

            @Override
            public void disconnected(DcReason reason) {

            }
        });
        client.connect();
//        client.disconnect();
        while (true) {
        }
    }
}
