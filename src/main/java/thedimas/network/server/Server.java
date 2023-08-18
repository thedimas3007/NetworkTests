package thedimas.network.server;

import thedimas.network.enums.DcReason;
import thedimas.network.packet.DisconnectPacket;
import thedimas.network.packet.Packet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static thedimas.network.Main.logger;

@SuppressWarnings("unused")
public class Server {
    private final List<ServerClientHandler> clients = new ArrayList<>();
    private final List<ServerListener> listeners = new ArrayList<>();
    private final int port;
    private ServerSocket serverSocket;
    private boolean listening;

    public Server(int port) {
        this.port = port;
    }

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
                listeners.forEach(l -> l.connected(clientHandler));
                logger.info("New connection from " + ip);
                new Thread(() -> {
                    clientHandler.received(packet -> {
                        logger.info("Packet received " + packet.getClass().getSimpleName());
                        listeners.forEach(l -> l.received(clientHandler, packet));
                    });
                    clientHandler.disconnected(reason ->
                            listeners.forEach(l -> l.disconnected(clientHandler, reason))
                    );
                    clientHandler.start();
                }).start();
            }
        } catch (SocketException e) {
            logger.log(Level.SEVERE, "Socket closed", e);
        }
    }

    public void send(Packet packet) {
        clients.forEach(c -> {
            try {
                c.send(packet);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Unable to send packet to " + c.getIp(), e);
            }
        });
    }

    public void stop() throws IOException {
        listening = false;
        clients.forEach(client -> {
            try {
                client.send(new DisconnectPacket(DcReason.SERVER_CLOSED));
                client.disconnect();
            } catch (IOException e) {
                logger.severe("Failed to close client " + client.getSocket().getInetAddress().getHostAddress());
                throw new RuntimeException(e);
            }
        });
        serverSocket.close();
        listeners.forEach(ServerListener::stopped);
    }

    public void addListener(ServerListener listener) {
        listeners.add(listener);
    }
}
