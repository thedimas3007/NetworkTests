package thedimas.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static thedimas.network.Main.logger;

@SuppressWarnings("unused")
public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;

    private PrintWriter out;
    private ObjectOutputStream outStr;


    private BufferedReader in;
    private ObjectInputStream inStr;

    private final int port;
    private boolean listening;

    public Server(int port) {
        this.port = port;
    }

    public void listen() throws IOException {
        logger.info("[Server] Starting...");
        listening = true;

        serverSocket = new ServerSocket(port);
        logger.fine("[Server] Started");

        clientSocket = serverSocket.accept();
        logger.info("[Server] New connection from " + clientSocket.getInetAddress().toString());

        outStr = new ObjectOutputStream(clientSocket.getOutputStream());
        inStr = new ObjectInputStream(clientSocket.getInputStream());

        out = new PrintWriter(outStr, true);
        in = new BufferedReader(new InputStreamReader(inStr));

        while (listening) {
            try {
                Object receivedObject = inStr.readObject();
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

    private void handle(Object object) {
        logger.info("[Server] New object: " + object.toString());
    }

    public void close() throws IOException {
        listening = false;
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
}
