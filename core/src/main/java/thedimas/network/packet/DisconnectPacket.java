package thedimas.network.packet;

import lombok.*;
import thedimas.network.enums.DcReason;

/**
 * The DisconnectPacket class represents a packet that is sent when the client wants to gracefully disconnect from the server
 * or when the server is being closed. It contains the reason for the disconnection.
 */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DisconnectPacket implements Packet {
    /**
     * The reason for the disconnection event.
     */
    DcReason reason;
}

