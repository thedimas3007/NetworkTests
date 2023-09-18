package thedimas.network.server;

import thedimas.network.enums.DcReason;
import thedimas.network.packet.Packet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
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
        listeners.forEach(ServerListener::started);
        try {
            while (listening) {
                Socket clientSocket = serverSocket.accept();
                ServerClientHandler clientHandler = new ServerClientHandler(clientSocket);
                String ip = clientSocket.getInetAddress().getHostAddress();
                listeners.forEach(ServerListener::stopped);
                clients.add(clientHandler);
                logger.info("New connection from " + ip);
                clientHandler.init();
                listeners.forEach(l -> l.connected(clientHandler));
                new Thread(() -> {
                    clientHandler.received(packet -> {
                        logger.info("Packet received " + packet.getClass().getSimpleName());
                        listeners.forEach(l -> l.received(clientHandler, packet));
                        if (packetListeners.containsKey(packet.getClass())) {
                            packetListeners.get(packet.getClass()).forEach(l -> l.accept(clientHandler, packet));
                        }

                    });
                    clientHandler.disconnected(reason ->
                            listeners.forEach(l -> l.disconnected(clientHandler, reason))
                    );
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
     * Stops the server by disconnecting all connected clients and closing the server socket.
     *
     * @throws IOException if there is an error while stopping the server
     */
    public void stop() throws IOException {
        listening = false;
        clients.forEach(client -> client.disconnect(DcReason.SERVER_CLOSED));
        serverSocket.close();
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
     * Registers an event listener for packets of a specific type.
     *
     * @param <T>      The type of packet to listen for, represented by a class.
     * @param packet   The class representing the type of packet to listen for.
     * @param consumer The consumer function to execute when a packet of the specified type is received.
     */
    public <T extends Packet> void on(Class<T> packet, BiConsumer<ServerClientHandler, T> consumer) {
        if (!packetListeners.containsKey(packet)) {
            packetListeners.put(packet, new ArrayList<>());
        }
        packetListeners.get(packet).add((BiConsumer<ServerClientHandler, Packet>) consumer);
    }
}
