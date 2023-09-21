package thedimas.network.packet;

import lombok.*;
import thedimas.network.enums.DcReason;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DisconnectPacket implements Packet {
    DcReason reason;
}

