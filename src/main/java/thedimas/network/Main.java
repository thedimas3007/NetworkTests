package thedimas.network;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public final static Logger logger = Logger.getLogger(Main.class.getName());
    private final static ConsoleHandler consoleHandler = new ConsoleHandler();
    private final static LogFormatter formatter = new LogFormatter();
    public static void main(String[] args) throws InterruptedException {
        consoleHandler.setLevel(Level.FINE);
        consoleHandler.setFormatter(formatter);
        logger.setUseParentHandlers(false);
        logger.addHandler(consoleHandler);
        logger.setLevel(Level.FINE);

        logger.severe("This is a severe message.");
        logger.warning("This is a warning message.");
        logger.info("This is an info message.");
        logger.config("This is a config message.");
        logger.fine("This is a fine message.");
        logger.finer("This is a finer message.");
        logger.finest("This is a finest message.");


        logger.info("Starting server");
        Server server = new Server(22); // I know this port is used, I'm just testing error handling

        try {
            server.listen();
        } catch (Throwable t) {
            Main.logger.severe("Unable to start server");
            throw new RuntimeException(t);
        }

        logger.info("Started server");
        Thread.sleep(1000);
    }
}
