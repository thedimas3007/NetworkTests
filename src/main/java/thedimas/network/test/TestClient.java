package thedimas.network.test;

import thedimas.network.client.Client;
import thedimas.network.packet.PlayerPacket;

import java.io.IOException;

public class TestClient {
    public static void main(String[] args) throws IOException {
        Client client = new Client("127.0.0.1", 9999);
        client.connect();
        client.send(new PlayerPacket());
        client.disconnect();
    }
}
