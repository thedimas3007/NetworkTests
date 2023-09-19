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

/**
 * The Server class represents a server that listens for incoming client connections, manages connected clients,
 * and facilitates communication between the clients and the server.
 */
@SuppressWarnings("unused")
public class Server {
    private final List<ServerClientHandler> clients = new ArrayList<>();
    private final List<ServerListener> listeners = new ArrayList<>();
    private final Map<Class<?>, List<BiConsumer<ServerClientHandler, Packet>>> packetListeners = new HashMap<>();
    private final EventListener events = new EventListener();
    private final int port;
    private ServerSocket serverSocket;
    private boolean listening;

    public Server(int port) {
        this.port = port;
    }

    /**
     * Starts the server by initializing the server socket, accepting client connections,
     * and managing communication with connected clients.
     * <br/>
     * This method is blocking.
     *
     * @throws IOException if there is an error while starting the server
     */
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
                events.fire(new ServerConnectionEvent(clientHandler));
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
                        if (packetListeners.containsKey(packet.getClass())) {
                            packetListeners.get(packet.getClass()).forEach(l -> l.accept(clientHandler, packet));
                        }

                    });
                    clientHandler.disconnected(reason -> {
                        events.fire(new ServerDisconnectionEvent(clientHandler, reason));
                        listeners.forEach(l -> l.disconnected(clientHandler, reason));
                    });
                    clientHandler.listen();
                }).start();
            }
        } catch (SocketException e) {
            logger.log(Level.SEVERE, "Socket closed", e);
        }
    }

    /**
     * Sends a packet to all connected clients.
     *
     * @param packet the packet to be sent
     */
    public void send(Packet packet) {
        clients.forEach(c -> {
            try {
                c.send(packet);
            } catch (IOException e) {
                logger.log(Level.FINE, "Unable to send packet to " + c.getIp(), e);
            }
        });
    }

    /**
     * Sends a packet to the specified clients.
     *
     * @param client the target client to receive a packet
     * @param packet the packet to be sent
     */
    public void send(ServerClientHandler client, Packet packet) {
        try {
            client.send(packet);
        } catch (IOException e) {
            logger.log(Level.FINE, "Unable to send packet to " + c.getIp(), e);
        }
    }


    /**
     * Stops the server by disconnecting all connected clients and closing the server socket.
     *
     * @throws IOException if there is an error while stopping the server
     */
    public void stop() throws IOException {
        listening = false;
        clients.forEach(client -> client.disconnect(DcReason.SERVER_CLOSED));
        serverSocket.close();
        events.fire(new ServerStoppedEvent());
        listeners.forEach(ServerListener::stopped);
    }

    /**
     * Adds a listener to receive events from the server.
     *
     * @param listener the listener to be added
     */
    public void addListener(ServerListener listener) {
        listeners.add(listener);
    }

    /**
     * Registers a listener for packets of a specific type.
     *
     * @param <T>      the type of packet to listen for, represented by a class.
     * @param packet   the class representing the type of packet to listen for.
     * @param consumer the consumer function to execute when a packet of the specified type is received.
     */
    public <T extends Packet> void onPacket(Class<T> packet, BiConsumer<ServerClientHandler, T> consumer) {
        if (!packetListeners.containsKey(packet)) {
            packetListeners.put(packet, new ArrayList<>());
        }
        packetListeners.get(packet).add((BiConsumer<ServerClientHandler, Packet>) consumer);
    }

    /**
     * Registers a listener for events of a specific type.
     *
     * @param <T>      the type of event to listen for, represented by a class.
     * @param event    the class representing the type of event to listen for.
     * @param consumer the consumer function to execute when an event of the specified type is fired.
     */
    public <T extends Event> void onEvent(Class<T> event, Consumer<T> consumer) {
        events.on(event, consumer);
    }
}
