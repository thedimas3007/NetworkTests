package thedimas.network.test;

import mindustry.gen.Player;
import thedimas.network.enums.DcReason;
import thedimas.network.packet.*;
import thedimas.network.server.Server;
import thedimas.network.server.ServerClientHandler;
import thedimas.network.server.ServerListener;
import thedimas.network.util.Bytes;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static thedimas.network.Main.logger;

public class TestServer {
    private static final String password = "somepasswd";
    private static final Map<ServerClientHandler, byte[]> salts = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Server server = new Server(9999);
        server.addListener(new ServerListener() {
            @Override
            public void started() {
                logger.config("Server started");
            }

            @Override
            public void connected(ServerClientHandler client) {
                logger.config("New connection");
                try {
                    byte[] salt = new byte[8];
                    new Random().nextBytes(salt);
                    client.send(new SaltPacket(salt));
                    salts.put(client, salt);
                } catch (IOException e) {
                    logger.log(Level.FINE, "Unable to send salt to client", e);
                }
            }

            @Override
            public void received(ServerClientHandler client, Packet packet) {
                logger.config("New packet");
                try {
                    if (packet instanceof AuthPacket authPacket) {
                        byte[] target = Bytes.hashed(Bytes.combine(salts.get(client), password.getBytes()));
                        if (!Arrays.equals(target, authPacket.getPassword())) {
                            client.disconnect(DcReason.ACCESS_DENIED);
                        } else {
                            client.send(new AuthSuccessfulPacket());
                            Player player = Player.create();
                            player.name("aboba");
                            MindustryEntityPacket entityPacket = new MindustryEntityPacket();
                            entityPacket.write(player);
                            client.send(entityPacket);
                        }
                    }
                } catch (IOException e) {
                    logger.log(Level.FINE, "Unable to send AuthSuccessful packet to the client", e);
                }
            }

            @Override
            public void disconnected(ServerClientHandler client, DcReason reason) {
                logger.config("New disconnection");
            }

            @Override
            public void stopped() {
                logger.config("Server stopped");
            }
        });

        server.on(AuthPacket.class, (serverClientHandler, authPacket) -> {
            logger.info("Auth received: " + Arrays.toString(authPacket.getPassword()));
        });

        server.on(RequestPacket.class, (serverClientHandler, requestPacket) -> {
            try {
                serverClientHandler.response(requestPacket, "Hi!");
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Holy hell...", e);
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
