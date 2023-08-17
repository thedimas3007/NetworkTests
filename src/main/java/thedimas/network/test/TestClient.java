package thedimas.network.test;

import thedimas.network.client.Client;
import thedimas.network.client.ClientListener;
import thedimas.network.enums.DcReason;
import thedimas.network.packet.Packet;
import thedimas.network.packet.PlayerPacket;

import java.io.IOException;

public class TestClient {
    public static void main(String[] args) throws IOException {
        Client client = new Client("127.0.0.1", 9999);
        client.connect();
        client.addListener(new ClientListener() {
            @Override
            public void connected() {
                try {
                    client.send(new PlayerPacket());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void received(Packet packet) {

            }

            @Override
            public void disconnected(DcReason reason) {

            }
        });
//        client.disconnect();
        while (true) {
        }
    }
}
