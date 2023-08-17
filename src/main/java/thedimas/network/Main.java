package thedimas.network;

import thedimas.network.client.Client;
import thedimas.network.server.Server;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public final static Logger logger = Logger.getLogger(Main.class.getName());
    private final static ConsoleHandler consoleHandler = new ConsoleHandler();
    private final static LogFormatter formatter = new LogFormatter();

    public static void main(String[] args) throws IOException {
        consoleHandler.setLevel(Level.FINE);
        consoleHandler.setFormatter(formatter);
        logger.setUseParentHandlers(false);
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.FINE);

        Server server = new Server(1234);
        Client client = new Client("127.0.0.1", 1234);

        new Thread(() -> {
            try {
                server.start();
            } catch (Throwable t) {
                logger.severe("Unable to start server");
                throw new RuntimeException(t);
            }
        }).start();

        new Thread(() -> {
            try {
                client.connect();
            } catch (Throwable t) {
                Main.logger.severe("[Client] Unable to connect");
                throw new RuntimeException(t);
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.hasNext() && scanner.next().equals("exit")) {
                server.stop();
                logger.warning("Server closed");
                break;
            }
        }
    }
}
