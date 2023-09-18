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

/**
 * The ServerClientHandler class represents a handler for an individual client connected to the server.
 * It manages communication with the client, sending and receiving packets, and handling disconnection events.
 */
@Getter
@SuppressWarnings("unused")
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

    /**
     * Initializes the handler by setting up input and output streams for communication with the client.
     * Handles potential errors and disconnection events.
     */
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

    /**
     * Starts listening for incoming packets from the client and handles them appropriately.
     * Handles potential errors and disconnection events.
     */
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

    /**
     * Sends a packet to the connected client.
     *
     * @param packet the packet to be sent
     * @throws IOException if there is an error while sending the packet
     */
    public <T extends Packet> void send(T packet) throws IOException {
        out.writeObject(packet);
    }

    /**
     * Disconnects the client with the specified reason and sends a disconnect packet.
     *
     * @param reason the reason for the disconnection
     */
    public void disconnect(DcReason reason) {
        try {
            send(new DisconnectPacket(reason));
            close();
        } catch (IOException e) {
            logger.log(Level.FINE, "Error while disconnecting client " + socket.getInetAddress().getHostAddress(), e);
        }
    }

    /**
     * Closes the communication streams and socket, disconnecting the client.
     */
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

    /**
     * Sets a consumer to handle received packets from the client.
     *
     * @param consumer the consumer to handle received packets
     */
    void received(Consumer<Packet> consumer) { // TODO: List of consumers
        packetListener = consumer;
    }

    /**
     * Sets a consumer to handle disconnection events from the client.
     *
     * @param consumer the consumer to handle disconnection events
     */
    void disconnected(Consumer<DcReason> consumer) {
        disconnectListener = consumer;
    }

    /**
     * Handles a received object from the client and forwards it to the appropriate listener.
     *
     * @param object the received object
     */
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

    /**
     * Handles a disconnect packet and triggers disconnection actions if necessary.
     *
     * @param reason the reason for disconnection
     */
    private void handleDisconnect(DcReason reason) {
        if (!disconnected) {
            logger.warning("Disconnected: " + reason.name());
            disconnectListener.accept(reason);
            close();
        }
    }

    /**
     * Returns the IP address of the connected client.
     *
     * @return the IP address of the client
     */
    public String getIp() {
        return socket.getInetAddress().getHostAddress();
    }
}
