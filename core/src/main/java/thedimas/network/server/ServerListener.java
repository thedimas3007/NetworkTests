package thedimas.network.server;

import thedimas.network.enums.DcReason;
import thedimas.network.packet.Packet;

public interface ServerListener {
    void started();

    void connected(ServerClientHandler client);

    void received(ServerClientHandler client, Packet packet);

    void disconnected(ServerClientHandler client, DcReason reason);

    void stopped();
}
