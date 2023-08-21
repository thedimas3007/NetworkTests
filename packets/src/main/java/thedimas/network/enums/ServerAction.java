package thedimas.network.enums;

/**
 * The ServerAction enum represents different actions that can be performed on a server.
 */
public enum ServerAction {
    /**
     * Opens the server with the last game mode with a random map.
     */
    HOST,

    /**
     * Stops hosting the server.
     */
    STOP,

    /**
     * Exits the server application.
     */
    EXIT,

    /**
     * Restarts the server.
     */
    RESTART,

    /**
     * List all players currently in game.
     * Then server sents
     */
    PLAYERS

    /**
     *
     */
}
