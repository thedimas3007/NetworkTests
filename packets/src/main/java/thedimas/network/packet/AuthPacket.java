package thedimas.network.packet;

import lombok.*;

/**
 * The AuthPacket class represents a packet containing authentication information.
 */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthPacket implements Packet {
    /**
     * The password byte array for authentication.
     */
    byte[] password;
}
