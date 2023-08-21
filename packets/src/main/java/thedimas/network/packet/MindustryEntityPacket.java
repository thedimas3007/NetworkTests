package thedimas.network.packet;

import lombok.*;
import mindustry.gen.Entityc;
import thedimas.network.util.Entities;

/**
 * The MindustryEntityPacket class represents a packet containing a serialized Mindustry game entity.
 * It provides methods to write and read entities for network communication.
 */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressWarnings("unused")
public class MindustryEntityPacket implements Packet {
    /**
     * The serialized byte array representing the Mindustry game entity.
     */
    byte[] serialized;

    /**
     * Writes a Mindustry entity to the packet.
     *
     * @param entity the entity to be written
     */
    public void write(Entityc entity) {
        serialized = Entities.write(entity);
    }

    /**
     * Reads a serialized Mindustry entity from the packet.
     *
     * @return the read Mindustry entity
     */
    public Entityc read() {
        return Entities.read(serialized);
    }
}
