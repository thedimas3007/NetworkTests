package thedimas.network;

import thedimas.network.packet.ConnectPacket;
import thedimas.network.packet.Packet;
import thedimas.network.packet.PlayerPacket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static thedimas.network.Main.logger;

@SuppressWarnings("unused")
public class Server {
    private ServerSocket serverSocket;
    private final List<Socket> clients = new ArrayList<>();
    private boolean listening;
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void listen() throws IOException {
        logger.info("[Server] Starting...");
        listening = true;
        serverSocket = new ServerSocket(port);
        logger.fine("[Server] Started");

        while (listening) {
            Socket clientSocket = serverSocket.accept();
            String ip = clientSocket.getInetAddress().getHostAddress();
            clients.add(clientSocket);
            logger.info("[Server] New connection from " + ip);
            new Thread(() -> {
                try {
                    ServerClientHandler clientHandler = new ServerClientHandler(clientSocket);
                    clientHandler.received(packet -> {
                        if (packet instanceof ConnectPacket) {
                            logger.info("[Packets] New connection! " + packet);
                        } else if (packet instanceof PlayerPacket playerPacket) {
                            logger.info("[Packets] New player! " + playerPacket.getPlayer());
                        }
                    });
                    clientHandler.run();
                } catch (IOException e) {
                    logger.severe("Failed to listen client " + ip);
                    throw new RuntimeException("Failed to listen client " + ip);
                }
            }).start();
        }
    }

    public void close() throws IOException {
        listening = false;
        clients.forEach(socket -> {
            try {
                socket.close();
            } catch (IOException e) {
                logger.severe("Failed to close client " + socket.getInetAddress().getHostAddress());
                throw new RuntimeException(e);
            }
        });
        serverSocket.close();
    }

    private static class ServerClientHandler {
        private final Socket clientSocket;
        private Consumer<Packet> listener = (packet) -> {};
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private boolean listening;

        public ServerClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() throws IOException {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            listening = true;

            while (listening) {
                try {
                    Object receivedObject = in.readObject();
                    handle(receivedObject);
                } catch (IOException e) {
                    logger.severe("[Server] Unable to read Object");
                    e.printStackTrace();
                    break;
                } catch (ClassNotFoundException e) {
                    logger.severe("[Server] Class not found");
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void disconnect() throws IOException {
            listening = false;
            in.close();
            out.close();
            clientSocket.close();
        }

        private void received(Consumer<Packet> consumer) { // TODO: List of consumers
            listener = consumer;
        }

        private void handle(Object object) {
            logger.info("[Server] New object: " + object.toString());
            if (object instanceof Packet packet) {
                listener.accept(packet);
            }
        }
    }
}
