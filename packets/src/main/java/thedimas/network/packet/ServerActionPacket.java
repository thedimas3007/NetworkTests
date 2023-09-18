package thedimas.network.packet;

import lombok.*;
import thedimas.network.enums.ServerAction;

/**
 * The ServerActionPacket class represents a packet containing a server action to be performed.
 */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerActionPacket extends Packet {
    /**
     * The server action to be performed.
     */
    ServerAction action;
}
