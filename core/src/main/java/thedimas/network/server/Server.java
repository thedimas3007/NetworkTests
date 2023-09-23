package thedimas.network.server;

import thedimas.network.enums.DcReason;
import thedimas.network.event.Event;
import thedimas.network.event.EventListener;
import thedimas.network.func.TripleConsumer;
import thedimas.network.packet.Packet;
import thedimas.network.packet.RequestPacket;
import thedimas.network.server.events.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;

import static thedimas.network.Main.logger;

@SuppressWarnings({"unused", "unchecked"})
public class Server {
    // region variables
    private final List<ServerListener> listeners = new ArrayList<>();
    private final Map<Class<?>, List<BiConsumer<ServerClientHandler, Packet>>> packetListeners = new HashMap<>();
    private final Map<Class<?>, List<TripleConsumer<ServerClientHandler, Integer, Packet>>> requestListeners = new HashMap<>();
    private final EventListener events = new EventListener();

    private final List<ServerClientHandler> clients = new ArrayList<>();
    private ServerSocket serverSocket;

    private final int port;

    private boolean listening;
    // endregion

    // region constructor
    public Server(int port) {
        this.port = port;
    }
    // endregion

    // region networking
    public void start() throws IOException {
        logger.info("Starting...");
        serverSocket = new ServerSocket(port);
        logger.config("Started");

        listening = true;

        events.fire(new ServerStartedEvent());
        listeners.forEach(ServerListener::started);

        try {
            while (listening) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleConnection(clientSocket)).start();
            }
        } catch (SocketException e) {
            logger.log(Level.SEVERE, "Socket closed", e);
        }
    }

    public void stop() throws IOException {
        listening = false;

        clients.forEach(client -> client.disconnect(DcReason.SERVER_CLOSED));
        serverSocket.close();

        events.fire(new ServerStoppedEvent());
        listeners.forEach(ServerListener::stopped);
    }

    public void send(Packet packet) {
        clients.forEach(c -> {
            try {
                c.send(packet);
            } catch (IOException e) {
                logger.log(Level.FINE, "Unable to send packet to " + c.getIp(), e);
            }
        });
    }

    public void send(ServerClientHandler client, Packet packet) {
        try {
            client.send(packet);
        } catch (IOException e) {
            logger.log(Level.FINE, "Unable to send packet to " + client.getIp(), e);
        }
    }

    public <T> void request(Packet packet, Consumer<T> listener) {
        clients.forEach(c -> {
            try {
                c.request(packet, listener);
            } catch (IOException e) {
                logger.log(Level.FINE, "Unable to send request to " + c.getIp(), e);
            }
        });
    }

    public <T> void request(ServerClientHandler client, Packet packet, Consumer<T> listener) {
        try {
            client.request(packet, listener);
        } catch (IOException e) {
            logger.log(Level.FINE, "Unable to send request to " + client.getIp(), e);
        }
    }
    // endregion

    // region listeners
    public void addListener(ServerListener listener) {
        listeners.add(listener);
    }

    public <T extends Packet> void onPacket(Class<T> packet, BiConsumer<ServerClientHandler, T> consumer) {
        packetListeners.computeIfAbsent(packet, k -> new ArrayList<>())
                .add((BiConsumer<ServerClientHandler, Packet>) consumer);
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
    private void handleConnection(Socket clientSocket) {
        ServerClientHandler clientHandler = new ServerClientHandler(clientSocket);

        clients.add(clientHandler);
        clientHandler.init();

        logger.info("New connection from " + clientHandler.getIp());
        events.fire(new ServerClientConnectedEvent(clientHandler));
        listeners.forEach(l -> l.connected(clientHandler));


        clientHandler.received(packet -> {
            logger.info("Packet received " + packet.getClass().getSimpleName());
            events.fire(new ServerReceivedEvent(clientHandler, packet));
            listeners.forEach(l -> l.received(clientHandler, packet));
            packetListeners.computeIfAbsent(packet.getClass(), k -> new ArrayList<>())
                    .forEach(l -> l.accept(clientHandler, packet));

            if (packet instanceof RequestPacket<?> requestPacket) {
                requestListeners.computeIfAbsent(requestPacket.getPacket().getClass(), k -> new ArrayList<>())
                        .forEach(l -> l.accept(clientHandler, requestPacket.getId(), requestPacket.getPacket()));
            }
        });

        clientHandler.disconnected(reason -> {
            events.fire(new ServerClientDisconnectedEvent(clientHandler, reason));
            listeners.forEach(l -> l.disconnected(clientHandler, reason));
        });

        clientHandler.listen();
    }
    // endregion
}
