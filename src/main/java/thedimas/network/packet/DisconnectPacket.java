package thedimas.network.packet;

import lombok.*;
import thedimas.network.enums.DcReason;

/** Called when the client wants to gracefully disconnect from the server or server is being closed. */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DisconnectPacket implements Packet {
    DcReason reason;
}
