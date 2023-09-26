package thedimas.network.server;

import lombok.Getter;
import org.jetbrains.annotations.Blocking;
import thedimas.network.enums.DcReason;
import thedimas.network.packet.DisconnectPacket;
import thedimas.network.packet.Packet;
import thedimas.network.packet.RequestPacket;
import thedimas.network.packet.ResponsePacket;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

@Getter
@SuppressWarnings({"unused", "unchecked"})
public class ServerClientHandler {
    // region variables
    private Consumer<Packet> packetListener = packet -> {
    };
    private Consumer<DcReason> disconnectListener = reason -> {
    };
    private final Map<Integer, Consumer<Object>> responseListeners = new HashMap<>();

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final Socket socket;

    private boolean listening;
    private boolean disconnected;
    // endregion

    // region constructor
    ServerClientHandler(Socket socket) {
        this.socket = socket;
    }
    // endregion

    // region initialization
    void init() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            if (e instanceof ObjectStreamException) {
                handleDisconnect(DcReason.STREAM_CORRUPTED);
            } else {
                handleDisconnect(DcReason.CONNECTION_CLOSED);
            }
            close();
        }
    }

    @Blocking
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
                handleDisconnect(DcReason.STREAM_CORRUPTED);
            } else {
                handleDisconnect(DcReason.CONNECTION_CLOSED);
            }
            close();
        } catch (ClassNotFoundException e) {
            handleDisconnect(DcReason.CLASS_NOT_FOUND);
        }
    }
    // endregion

    // region networking
    public <T extends Packet> void send(T packet) throws IOException {
        out.writeObject(packet);
    }

    public void disconnect(DcReason reason) throws IOException {
        send(new DisconnectPacket(reason));
        close();
    }

    private void close() {
        try {
            listening = false;
            disconnected = true;
            if (in != null) in.close();
            if (out != null) out.close();
            socket.close();
        } catch (IOException ignored) {
        }
    }

    public <T> void request(Packet packet, Consumer<T> listener) throws IOException {
        RequestPacket<Packet> requestPacket = new RequestPacket<>((int) (Math.random() * Integer.MAX_VALUE), packet);
        responseListeners.put(requestPacket.getId(), (Consumer<Object>) listener);
        send(requestPacket);
    }

    public <T extends Serializable> void response(int id, T resp) throws IOException {
        send(new ResponsePacket<>(id, resp));
    }

    public <T extends Serializable> void response(RequestPacket<Packet> requestPacket, T resp) throws IOException {
        response(requestPacket.getId(), resp);
    }
    // endregion

    // region listening
    void received(Consumer<Packet> consumer) {
        packetListener = consumer;
    }

    void disconnected(Consumer<DcReason> consumer) {
        disconnectListener = consumer;
    }
    // endregion

    // region private handlers
    private void handlePacket(Object object) {
        if (object instanceof Packet packet) {
            if (packet instanceof DisconnectPacket disconnectPacket) {
                handleDisconnect(disconnectPacket.getReason());
            } else {
                if (packet instanceof ResponsePacket<?> responsePacket) {
                    responseListeners.computeIfPresent(responsePacket.getTarget(), (key, listener) -> {
                        listener.accept(responsePacket.getResponse());
                        return null;
                    });
                }
                
                packetListener.accept(packet);
            }
        }
    }

    private void handleDisconnect(DcReason reason) {
        if (!disconnected) {
            disconnectListener.accept(reason);
            close();
        }
    }
    // endregion

    // region etc
    public String getIp() {
        return socket.getInetAddress().getHostAddress();
    }
    // endregion
}
