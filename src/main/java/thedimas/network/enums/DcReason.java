package thedimas.network.enums;

public enum DcReason {
    /** When the client forcefully closes connection. */
    CONNECTION_CLOSED,

    /** When the client gracefully disconnects from the server */
    DISCONNECTED,

    /** Is sent to all clients when the server is shutting down */
    SERVER_CLOSED,
    TIMEOUT,
    PACKET_SPAM; // TODO: implement spam detection
}
