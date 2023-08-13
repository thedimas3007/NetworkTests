package thedimas.network;

import thedimas.network.type.Player;
import thedimas.network.packet.ConnectPacket;
import thedimas.network.packet.PlayerPacket;

import java.io.*;
import java.net.Socket;

import static thedimas.network.Main.logger;

@SuppressWarnings("unused")
public class Client {
    private Socket clientSocket;

    private PrintWriter out;
    private BufferedReader in;

    private ObjectOutputStream outStr;
    private ObjectInputStream inStr;

    private final String ip;
    private final int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void connect() throws IOException {
        logger.info("[Client] Connecting...");
        clientSocket = new Socket(ip, port);

        outStr = new ObjectOutputStream(clientSocket.getOutputStream());
        inStr = new ObjectInputStream(clientSocket.getInputStream());

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        logger.fine("[Client] Connected to " + ip);
    }

    public void join(String name, String lang) {
        try {
            ConnectPacket packet = new ConnectPacket()
                    .withName(name)
                    .withLang(lang);
            outStr.writeObject(packet);
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
        outStr.writeObject(packet);
    }

    public void disconnect() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
