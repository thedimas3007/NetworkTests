package thedimas.network.test;

import lombok.*;
import thedimas.network.packet.Packet;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthPacket implements Packet {
    byte[] password;
}
