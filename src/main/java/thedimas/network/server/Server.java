package thedimas.network.server;

import thedimas.network.enums.DcReason;
import thedimas.network.packet.DisconnectPacket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

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
        logger.info("[Server] Starting...");
        listening = true;
        serverSocket = new ServerSocket(port);
        listeners.forEach(ServerListener::started);
        logger.fine("[Server] Started");
        try {
            while (listening) {
                Socket clientSocket = serverSocket.accept();
                ServerClientHandler clientHandler = new ServerClientHandler(clientSocket);
                String ip = clientSocket.getInetAddress().getHostAddress();
                listeners.forEach(ServerListener::stopped);
                clients.add(clientHandler);
                listeners.forEach(l -> l.connected(clientSocket));
                logger.info("[Server] New connection from " + ip);
                new Thread(() -> {
                    try {
                        clientHandler.received(packet -> {
                            logger.info("[Server] Packet received " + packet.getClass().getSimpleName());
                            listeners.forEach(l -> l.received(clientHandler, packet));
                        });
                        clientHandler.start();
                    } catch (IOException e) {
                        logger.severe("[Server] Failed to listen client " + ip);
                        throw new RuntimeException("Failed to listen client " + ip);
                    }
                }).start();
            }
        } catch (SocketException e) {
            logger.warning("[Server] Socket closed");
            e.printStackTrace();
        }
    }

    public void addListener(ServerListener listener) {
        listeners.add(listener);
    }

    public void stop() throws IOException {
        listening = false;
        clients.forEach(client -> {
            try {
                client.send(new DisconnectPacket(DcReason.SERVER_CLOSED));
                client.disconnect();
            } catch (IOException e) {
                logger.severe("[Server] Failed to close client " + client.getSocket().getInetAddress().getHostAddress());
                throw new RuntimeException(e);
            }
        });
        serverSocket.close();
        listeners.forEach(ServerListener::stopped);
    }
}
