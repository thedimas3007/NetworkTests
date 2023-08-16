package thedimas.network.server;

import thedimas.network.packet.Packet;

import java.net.Socket;

public interface ServerListener {
    void started();
    void connected(Socket client);
    void received(ServerClientHandler client, Packet packet);
    void disconnected(Socket client);
    void stopped();
}
