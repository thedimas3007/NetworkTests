package thedimas.network.server;

import lombok.Getter;
import thedimas.network.packet.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;

import static thedimas.network.Main.logger;

public class ServerClientHandler {
    @Getter
    private final Socket socket;
    private Consumer<Packet> listener = (packet) -> {
    };
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean listening;

    public ServerClientHandler(Socket socket) {
        this.socket = socket;
    }

    void start() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        listening = true;

        while (listening) {
            try {
                Object receivedObject = in.readObject();
                handle(receivedObject);
            } catch (IOException e) {
                logger.warning("[Server] Connection closed");
                break;
            } catch (ClassNotFoundException e) {
                logger.severe("[Server] Class not found");
                e.printStackTrace();
                break;
            }
        }
    }

    public void send(Packet packet) throws IOException {
        out.writeObject(packet);
    }

    public void disconnect() throws IOException {
        listening = false;
        in.close();
        out.close();
        socket.close();
    }

    void received(Consumer<Packet> consumer) { // TODO: List of consumers
        listener = consumer;
    }

    void handle(Object object) {
        logger.config("[Server] New object: " + object.toString());
        if (object instanceof Packet packet) {
            listener.accept(packet);
        }
    }
}
