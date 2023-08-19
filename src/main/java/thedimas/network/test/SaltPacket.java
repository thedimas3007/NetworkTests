package thedimas.network.test;

import lombok.*;
import thedimas.network.packet.Packet;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaltPacket implements Packet {
    byte[] salt;
}
