package thedimas.network.server;

import lombok.Getter;
import thedimas.network.enums.DcReason;
import thedimas.network.packet.DisconnectPacket;
import thedimas.network.packet.Packet;
import thedimas.network.packet.RequestPacket;
import thedimas.network.packet.ResponsePacket;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;

import static thedimas.network.Main.logger;

@Getter
@SuppressWarnings("unused")
public class ServerClientHandler {
    private final Socket socket;
    private Consumer<Packet> packetListener = packet -> {};
    private Consumer<DcReason> disconnectListener = reason -> {};
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean listening;
    private boolean disconnected;

    ServerClientHandler(Socket socket) {
        this.socket = socket;
    }

    void init() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            if (e instanceof ObjectStreamException) {
                logger.warning("Corrupted stream");
                handleDisconnect(DcReason.STREAM_CORRUPTED);
            } else {
                logger.warning("Connection closed");
                handleDisconnect(DcReason.CONNECTION_CLOSED);
            }
            close();
        }
    }

    void listen() {
        listening = true;
        disconnected = false;
        try {
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
            close();
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Class not found");
        }
    }

    public <T extends Packet> void send(T packet) throws IOException {
        out.writeObject(packet);
    }

    public void disconnect(DcReason reason) {
        try {
            send(new DisconnectPacket(reason));
            close();
        } catch (IOException e) {
            logger.log(Level.FINE, "Error while disconnecting client " + socket.getInetAddress().getHostAddress(), e);
        }
    }

    public void close() {
        try {
            listening = false;
            disconnected = true;
            if (in != null) in.close();
            if (out != null) out.close();
            socket.close();
        } catch (IOException e) {
            logger.log(Level.FINE, "Error while closing connection " + socket.getInetAddress().getHostAddress(), e);
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

    public <T extends Serializable> void response(RequestPacket<Packet> requestPacket, T resp) throws IOException {
        send(new ResponsePacket<>(requestPacket.getId(), resp));
    }

    private void handleDisconnect(DcReason reason) {
        if (!disconnected) {
            logger.warning("Disconnected: " + reason.name());
            disconnectListener.accept(reason);
            close();
        }
    }

    public String getIp() {
        return socket.getInetAddress().getHostAddress();
    }
}
