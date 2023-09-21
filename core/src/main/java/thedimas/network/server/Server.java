package thedimas.network.server;

import thedimas.network.enums.DcReason;
import thedimas.network.event.Event;
import thedimas.network.event.EventListener;
import thedimas.network.packet.Packet;
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
        listening = true;
        serverSocket = new ServerSocket(port);
        logger.config("Started");
        events.fire(new ServerStartedEvent());
        listeners.forEach(ServerListener::started);
        try { // TODO: some sort of registerClient
            while (listening) {
                Socket clientSocket = serverSocket.accept();
                ServerClientHandler clientHandler = new ServerClientHandler(clientSocket);
                String ip = clientSocket.getInetAddress().getHostAddress();
                events.fire(new ServerClientConnectedEvent(clientHandler));
                listeners.forEach(ServerListener::stopped);
                clients.add(clientHandler);
                logger.info("New connection from " + ip);
                clientHandler.init();
                listeners.forEach(l -> l.connected(clientHandler));

                new Thread(() -> {
                    clientHandler.received(packet -> {
                        logger.info("Packet received " + packet.getClass().getSimpleName());
                        events.fire(new ServerReceivedEvent(clientHandler, packet));
                        listeners.forEach(l -> l.received(clientHandler, packet));
                        packetListeners.computeIfAbsent(packet.getClass(), k -> new ArrayList<>())
                                .forEach(l -> l.accept(clientHandler, packet));
                    });
                    clientHandler.disconnected(reason -> {
                        events.fire(new ServerClientDisconnectedEvent(clientHandler, reason));
                        listeners.forEach(l -> l.disconnected(clientHandler, reason));
                    });
                    clientHandler.listen();
                }).start();
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
    // endregion
}
