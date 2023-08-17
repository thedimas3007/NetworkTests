package thedimas.network.client;

import thedimas.network.enums.DcReason;
import thedimas.network.packet.DisconnectPacket;
import thedimas.network.packet.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static thedimas.network.Main.logger;

@SuppressWarnings("unused")
public class Client {
    private final String ip;
    private final int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void connect() throws IOException {
        logger.info("Connecting...");
        socket = new Socket(ip, port);

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        logger.fine("Connected to " + ip);
    }

    public void send(Packet packet) throws IOException {
        out.writeObject(packet);
    }

    public void disconnect() {
        try {
            out.writeObject(new DisconnectPacket(DcReason.DISCONNECTED));
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            logger.severe("Error while disconnecting");
        }
    }
}
