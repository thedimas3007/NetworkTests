package thedimas.network.server.events;

import lombok.*;
import thedimas.network.event.Event;
import thedimas.network.packet.Packet;
import thedimas.network.server.ServerClientHandler;

/**
 * Represents an event indicating that the server has received a packet from a client.
 */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerReceivedEvent implements Event {
    /**
     * The client handler responsible for sending the received packet.
     */
    ServerClientHandler client;

    /**
     * The packet that was received by the server.
     */
    Packet packet;
}
