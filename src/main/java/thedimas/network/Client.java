package thedimas.network;

import thedimas.network.packet.ConnectPacket;
import thedimas.network.packet.PlayerPacket;
import thedimas.network.type.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static thedimas.network.Main.logger;

@SuppressWarnings("unused")
public class Client {
    private final String ip;
    private final int port;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void connect() throws IOException {
        logger.info("[Client] Connecting...");
        clientSocket = new Socket(ip, port);

        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
        logger.fine("[Client] Connected to " + ip);
    }

    public void join(String name, String lang) {
        try {
            ConnectPacket packet = ConnectPacket.builder()
                    .name(name)
                    .name(lang)
                    .build();
            out.writeObject(packet);
        } catch (IOException e) {
            logger.severe("[Client] Unable to write Object");
            e.printStackTrace();
        }
    }

    public void player() throws IOException {
        PlayerPacket packet = new PlayerPacket();
        Player player = Player.builder()
                .uuid("ajoweur2344anghEPOIWUHP==")
                .name("name")
                .ip("127.0.0.1")
                .locale("ru_RU")
                .id(228)
                .build();
        packet.setPlayer(player);
        out.writeObject(packet);
    }

    public void disconnect() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
