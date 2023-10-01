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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Getter
@SuppressWarnings({"unused", "unchecked"})
public class ServerClientHandler {
    // region variables
    private Consumer<Packet> packetListener = packet -> {
    };
    private Consumer<DcReason> disconnectListener = reason -> {
    };
    private final Map<Integer, Consumer<Object>> responseListeners = new HashMap<>();
    private final ExecutorService executor = new ScheduledThreadPoolExecutor(1);

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final Socket socket;

    private volatile boolean listening;
    private volatile boolean disconnected;
    private volatile long lastReceived = 0;

    private final boolean closeTimeout;
    // endregion

    // region constructor
    ServerClientHandler(Socket socket) {
        this.socket = socket;
        this.closeTimeout = false;
    }

    ServerClientHandler(Socket socket, boolean closeTimeout) {
        this.socket = socket;
        this.closeTimeout = closeTimeout;
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
        lastReceived = System.currentTimeMillis();
        listening = true;
        disconnected = false;
        try {
            executor.execute(() -> {
                while (true) {
                    if (lastReceived + Server.TIMEOUT < System.currentTimeMillis()) {
                        break;
                    }
                }

                try {
                    disconnect(DcReason.TIMEOUT);
                } catch (IOException e) {
                    close();
                }
            });

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

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException | IOException ignored) {
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
            lastReceived = System.currentTimeMillis();
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
