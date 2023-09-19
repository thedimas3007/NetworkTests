package thedimas.network.packet;

import lombok.*;

import java.io.Serializable;

/**
 * The ObjectPacket class represents a packet used to transport an arbitrary object payload.
 */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ObjectPacket <T extends Serializable> implements Packet {
    /**
     * The payload object contained within this packet.
     */
    T payload;
}
