package thedimas.network.server;

import lombok.Setter;
import org.jetbrains.annotations.Blocking;
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


@SuppressWarnings({"unused", "unchecked"})
public class Server {
    // region variables
    public static int TIMEOUT = 15 * 1000;

    private final List<ServerListener> listeners = new ArrayList<>();
    private final Map<Class<?>, List<BiConsumer<ServerClientHandler, Packet>>> packetListeners = new HashMap<>();
    private final Map<Class<?>, List<TripleConsumer<ServerClientHandler, Integer, Packet>>> requestListeners = new HashMap<>();
    private final EventListener events = new EventListener();

    private final List<ServerClientHandler> clients = new ArrayList<>();
    private ServerSocket serverSocket;

    private final int port;

    private boolean listening;
    private final boolean closeTimeout;
    // endregion

    // region constructor
    public Server(int port) {
        this.port = port;
        this.closeTimeout = false;
    }

    public Server(int port, boolean closeTimeout) {
        this.port = port;
        this.closeTimeout = closeTimeout;
    }
    // endregion

    // region networking
    @Blocking
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);

        listening = true;

        events.fire(new ServerStartedEvent());
        listeners.forEach(ServerListener::started);

        try {
            while (listening) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleConnection(clientSocket)).start();
            }
        } catch (SocketException e) {
            events.fire(new ServerErrorEvent("Socket is closed", null, e));
            events.fire(new ServerStoppedEvent());
        }
    }

    public void stop() throws IOException {
        listening = false;

        clients.forEach(client -> {
            try {
                client.disconnect(DcReason.SERVER_CLOSED);
            } catch (IOException e) {
                events.fire(new ServerErrorEvent("Error while disconnecting client", client, e));
            }
        });
        serverSocket.close();

        events.fire(new ServerStoppedEvent());
        listeners.forEach(ServerListener::stopped);
    }

    public void send(Packet packet) {
        clients.forEach(c -> {
            send(packet);
        });
    }

    public void send(ServerClientHandler client, Packet packet) {
        try {
            client.send(packet);
        } catch (IOException e) {
            events.fire(new ServerErrorEvent("Unable to send packet", client, e));
        }
    }

    public <T> void request(Packet packet, Consumer<T> listener) {
        clients.forEach(c -> {
            request(c, packet, listener);
        });
    }

    public <T> void request(ServerClientHandler client, Packet packet, Consumer<T> listener) {
        try {
            client.request(packet, listener);
        } catch (IOException e) {
            events.fire(new ServerErrorEvent("Unable to send request", client, e));
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
    @Blocking
    private void handleConnection(Socket clientSocket) {
        ServerClientHandler clientHandler = new ServerClientHandler(clientSocket, closeTimeout);

        clients.add(clientHandler);
        clientHandler.init();

        events.fire(new ServerClientConnectedEvent(clientHandler));
        listeners.forEach(l -> l.connected(clientHandler));


        clientHandler.received(packet -> {
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
