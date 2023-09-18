package thedimas.network.test;

import mindustry.gen.Entityc;
import mindustry.gen.Player;
import thedimas.network.client.Client;
import thedimas.network.client.ClientListener;
import thedimas.network.enums.DcReason;
import thedimas.network.packet.AuthPacket;
import thedimas.network.packet.MindustryEntityPacket;
import thedimas.network.packet.Packet;
import thedimas.network.packet.SaltPacket;
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
                    } else if (packet instanceof MindustryEntityPacket entityPacket) {
                        Entityc entityc = entityPacket.read();
                        logger.info(((Player) entityc).name());
                    }
                } catch (IOException e) {
                    logger.log(Level.FINE, "Unable to send AuthPacket", e);
                }
            }

            @Override
            public void disconnected(DcReason reason) {

            }
        });

        client.on(SaltPacket.class, salt -> {
            StringBuilder builder = new StringBuilder();
            for (byte b : salt.getSalt()) {
                builder.append(String.format("%02x", b));
            }
            logger.info("New salt: " + builder);
        });
        client.connect();
//        client.disconnect();
        while (true) {
        }
    }
}
