package thedimas.network.enums;

/**
 * The DcReason enum represents different reasons for a disconnection event between the client and server.
 */
@SuppressWarnings("unused")
public enum DcReason {
    /**
     * Indicates that the connection was forcefully closed either by the client or the server.
     */
    CONNECTION_CLOSED,

    /**
     * Indicates that the client gracefully disconnected from the server.
     */
    DISCONNECTED,

    /**
     * Indicates that the server is shutting down, and this reason is sent to all clients.
     */
    SERVER_CLOSED,

    /**
     * Indicates that the stream between the client and server became corrupted.
     * This reason is usually sent to listeners.
     */
    STREAM_CORRUPTED,

    /**
     * Indicates that the client's access was denied by the server.
     */
    ACCESS_DENIED,

    /**
     * Indicates that a connection was terminated due to a timeout.
     */
    TIMEOUT
}

