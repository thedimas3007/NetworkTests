package thedimas.network.server.events;

import lombok.*;
import thedimas.network.enums.DcReason;
import thedimas.network.event.Event;
import thedimas.network.server.ServerClientHandler;

/**
 * Represents an event indicating that a client has been disconnected from the server.
 */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerClientDisconnectedEvent implements Event {
    /**
     * The disconnected client.
     */
    private ServerClientHandler client;

    /**
     * The reason for the disconnection, specified as a {@link DcReason}.
     */
    private DcReason reason;
}

