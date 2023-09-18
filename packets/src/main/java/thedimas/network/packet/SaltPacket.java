package thedimas.network.packet;

import lombok.*;

/**
 * The SaltPacket class represents a packet containing a salt value for secure communication.
 */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SaltPacket extends Packet {
    /**
     * The salt byte array for secure communication.
     */
    byte[] salt;
}
