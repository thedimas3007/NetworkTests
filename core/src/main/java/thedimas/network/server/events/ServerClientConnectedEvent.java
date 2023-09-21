package thedimas.network.server.events;

import lombok.*;
import thedimas.network.event.Event;
import thedimas.network.server.ServerClientHandler;

/**
 * Represents an event indicating that a client has connected to the server.
 */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerClientConnectedEvent implements Event {
    /**
     * The newly connected client.
     */
    ServerClientHandler client;
}
