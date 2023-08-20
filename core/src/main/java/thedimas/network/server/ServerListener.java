package thedimas.network.server;

import thedimas.network.enums.DcReason;
import thedimas.network.packet.Packet;

/**
 * The ServerListener interface defines methods that represent events related to the server's lifecycle,
 * client connections, received packets, client disconnections, and server stopping.
 */
@SuppressWarnings("unused")
public interface ServerListener {
    /**
     * Invoked when the server has started successfully.
     */
    void started();

    /**
     * Invoked when a new client is connected to the server.
     *
     * @param client the connected client handler
     */
    void connected(ServerClientHandler client);

    /**
     * Invoked when a packet is received from a connected client.
     *
     * @param client the client handler that sent the packet
     * @param packet the received packet
     */
    void received(ServerClientHandler client, Packet packet);

    /**
     * Invoked when a client is disconnected from the server.
     *
     * @param client the disconnected client handler
     * @param reason the reason for disconnection
     */
    void disconnected(ServerClientHandler client, DcReason reason);

    /**
     * Invoked when the server is stopped.
     */
    void stopped();
}
