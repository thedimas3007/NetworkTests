package thedimas.network;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public final static Logger logger = Logger.getLogger(Main.class.getName());
    private final static ConsoleHandler consoleHandler = new ConsoleHandler();
    private final static LogFormatter formatter = new LogFormatter();
    public static void main(String[] args) throws IOException, InterruptedException {
        consoleHandler.setLevel(Level.FINE);
        consoleHandler.setFormatter(formatter);
        logger.setUseParentHandlers(false);
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.FINE);

        Server server = new Server(1234);
        Client client = new Client("127.0.0.1", 1234);

        new Thread(() -> {
            try {
                logger.info("Starting server");
                server.listen();
            } catch (Throwable t) {
                Main.logger.severe("Unable to start server");
                throw new RuntimeException(t);
            }
        }).start();

        new Thread(() -> {
            try {
                logger.info("Connecting to the server");
                client.connect();
                logger.fine("Connected to the server");
                client.join("Vasia", "ru_RU");
            } catch (Throwable t) {
                Main.logger.severe("Unable to connect to the server");
                throw new RuntimeException(t);
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.hasNext() && scanner.next().equals("exit")) {
                client.disconnect();
                logger.warning("Client disconnected");
                server.close();
                logger.warning("Server closed");
                Thread.sleep(100);
                break;
            }
        }
    }
}
