package thedimas.network.server;

import lombok.Getter;
import thedimas.network.enums.DcReason;
import thedimas.network.packet.DisconnectPacket;
import thedimas.network.packet.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;

import static thedimas.network.Main.logger;

@Getter
public class ServerClientHandler {
    private final Socket socket;
    private Consumer<Packet> packetListener = (packet) -> {
    };

    private Consumer<DcReason> disconnectListener = (reason -> {
    });
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean listening;
    private boolean disconnected;

    ServerClientHandler(Socket socket) {
        this.socket = socket;
    }

    void start() {
        listening = true;
        disconnected = false;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            while (listening) {
                Object receivedObject = in.readObject();
                handlePacket(receivedObject);
            }
        } catch (IOException e) {
            if (e instanceof ObjectStreamException) {
                logger.warning("Corrupted stream");
                handleDisconnect(DcReason.STREAM_CORRUPTED);
            } else {
                logger.warning("Connection closed");
                handleDisconnect(DcReason.CONNECTION_CLOSED);
            }
            disconnect();
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Class not found");
        }
    }

    public void send(Packet packet) throws IOException {
        out.writeObject(packet);
    }

    public void disconnect() {
        try {
            listening = false;
            disconnected = true;
            if (in != null) in.close();
            if (out != null) out.close();
            socket.close();
        } catch (IOException e) {
            logger.severe("Error while disconnecting client " + socket.getInetAddress().getHostAddress());
        }
    }

    void received(Consumer<Packet> consumer) { // TODO: List of consumers
        packetListener = consumer;
    }

    void disconnected(Consumer<DcReason> consumer) {
        disconnectListener = consumer;
    }

    void handlePacket(Object object) {
        logger.config("New object: " + object.toString());
        if (object instanceof Packet packet) {
            if (packet instanceof DisconnectPacket disconnectPacket) {
                handleDisconnect(disconnectPacket.getReason());
            } else {
                packetListener.accept(packet);
            }
        }
    }

    private void handleDisconnect(DcReason reason) {
        if (!disconnected) {
            logger.warning("Disconnected: " + reason.name());
            disconnectListener.accept(reason);
            disconnect();
        }
    }

    public String getIp() {
        return socket.getInetAddress().getHostAddress();
    }
}
