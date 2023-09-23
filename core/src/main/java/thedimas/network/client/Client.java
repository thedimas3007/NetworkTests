package thedimas.network.client;

import thedimas.network.client.events.ClientConnectedEvent;
import thedimas.network.client.events.ClientDisconnectedEvent;
import thedimas.network.client.events.ClientReceivedEvent;
import thedimas.network.enums.DcReason;
import thedimas.network.event.Event;
import thedimas.network.event.EventListener;
import thedimas.network.func.TripleConsumer;
import thedimas.network.packet.DisconnectPacket;
import thedimas.network.packet.Packet;
import thedimas.network.packet.RequestPacket;
import thedimas.network.packet.ResponsePacket;
import thedimas.network.server.ServerClientHandler;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

import static thedimas.network.Main.logger;

@SuppressWarnings({"unused", "unchecked"})
public class Client {
    // region variables
    private final List<ClientListener> listeners = new ArrayList<>();
    private final Map<Class<?>, List<Consumer<Packet>>> packetListeners = new HashMap<>();
    private final Map<Integer, Consumer<Object>> responseListeners = new HashMap<>(); // listeners for responses from server
    private final Map<Class<?>, List<TripleConsumer<ServerClientHandler, Integer, Packet>>> requestListeners = new HashMap<>(); // listeners for server requests
    private final EventListener events = new EventListener();

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private final String ip;
    private final int port;

    private boolean listening;
    private boolean disconnected;
    // endregion

    // region constructor
    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    // endregion

    // region networking
    public void connect() throws IOException {
        logger.info("Connecting...");
        socket = new Socket(ip, port);
        logger.config("Connected");

        listening = true;
        disconnected = false;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            events.fire(new ClientConnectedEvent());
            listeners.forEach(ClientListener::connected);

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

    public void disconnect() {
        try {
            listening = false;
            disconnected = true;

            if (in != null) in.close();
            if (out != null) out.close();
            socket.close();
        } catch (IOException e) {
            logger.log(Level.FINE, "Error while disconnecting client " + socket.getInetAddress().getHostAddress(), e);
        }
    }

    public <T extends Packet> void send(T packet) throws IOException {
        out.writeObject(packet);
    }

    public <T> void request(Packet packet, Consumer<T> listener) throws IOException {
        RequestPacket<Packet> requestPacket = new RequestPacket<>((int) (Math.random() * Integer.MAX_VALUE), packet);
        responseListeners.put(requestPacket.getId(), (Consumer<Object>) listener);
        send(requestPacket);
    }

    public <T extends Serializable> void response(RequestPacket<Packet> requestPacket, T resp) throws IOException {
        send(new ResponsePacket<>(requestPacket.getId(), resp));
    }
    // endregion

    // region listeners
    public void addListener(ClientListener listener) {
        listeners.add(listener);
    }

    public <T extends Packet> void onPacket(Class<T> packet, Consumer<T> consumer) {
        packetListeners.computeIfAbsent(packet, k -> new ArrayList<>())
                .add((Consumer<Packet>) consumer);
    }

    public <T extends Event> void onEvent(Class<T> event, Consumer<T> consumer) {
        events.on(event, consumer);
    }

    public <T extends Packet> void onRequest(Class<T> packetClass, TripleConsumer<ServerClientHandler, Integer, T> consumer) {
        requestListeners.computeIfAbsent(packetClass, k -> new ArrayList<>())
                .add((TripleConsumer<ServerClientHandler, Integer, Packet>) consumer);
    }

    // endregion

    // region private handlers
    private void handlePacket(Object object) {
        logger.config("New object: " + object.toString());
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

                events.fire(new ClientReceivedEvent(packet));
                listeners.forEach(l -> l.received(packet));
                packetListeners.computeIfAbsent(packet.getClass(), k -> new ArrayList<>())
                        .forEach(l -> l.accept(packet));
            }
        }
    }

    private void handleDisconnect(DcReason reason) {
        if (!disconnected) {
            logger.warning("Disconnected: " + reason.name());

            events.fire(new ClientDisconnectedEvent(reason));
            listeners.forEach(l -> l.disconnected(reason));

            disconnect();
        }
    }
    // endregion
}
