package thedimas.network;

import thedimas.network.packet.ConnectPacket;

import java.io.*;
import java.net.Socket;

@SuppressWarnings("unused")
public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private ObjectOutputStream objOut;
    private BufferedReader in;
    private String ip;
    private int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void connect() throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        objOut = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void join(String name, String lang) throws IOException {
        ConnectPacket packet = new ConnectPacket();
        packet.setName(name);
        packet.setLang(lang);
        objOut.writeObject(packet);
    }

    public void disconnect() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
