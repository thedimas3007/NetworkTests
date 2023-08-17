package thedimas.network.client;

import thedimas.network.enums.DcReason;
import thedimas.network.packet.Packet;

@SuppressWarnings("unused")
public interface ClientListener {
    void connected();

    void received(Packet packet);

    void disconnected(DcReason reason);
}
