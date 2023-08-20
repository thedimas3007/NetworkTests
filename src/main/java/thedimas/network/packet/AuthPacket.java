package thedimas.network.packet;

import lombok.*;
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthPacket implements Packet {
    byte[] password;
}
