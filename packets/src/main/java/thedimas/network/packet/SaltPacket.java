package thedimas.network.packet;

import lombok.*;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaltPacket implements Packet {
    byte[] salt;
}
