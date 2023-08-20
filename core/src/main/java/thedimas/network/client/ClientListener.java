package thedimas.network.client;

import thedimas.network.enums.DcReason;
import thedimas.network.packet.Packet;

/**
 * The ClientListener interface defines methods that represent events related to the client's connection,
 * received packets, and disconnections.
 */
@SuppressWarnings("unused")
public interface ClientListener {

    /**
     * Invoked when the client has successfully connected to the server.
     */
    void connected();

    /**
     * Invoked when a packet is received from the server.
     *
     * @param packet the received packet
     */
    void received(Packet packet);

    /**
     * Invoked when the client is disconnected from the server.
     *
     * @param reason the reason for disconnection
     */
    void disconnected(DcReason reason);
}

