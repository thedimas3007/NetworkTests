package thedimas.network.packet;

import lombok.*;
import mindustry.entities.EntityGroup;
import mindustry.gen.Entityc;
import thedimas.network.util.Entities;

import java.util.ArrayList;
import java.util.List;

/**
 * The MindustryEntityPacket class represents a packet containing a serialized List of Mindustry game entities.
 * It provides methods to write and read entities for network communication.
 */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressWarnings("unused")
public class MindustryEntityListPacket implements Packet {
    /**
     * The list of byte arrays representing the list of serialized Mindustry game entities.
     */
    List<byte[]> serializedList = new ArrayList<>();


    /**
     * Writes a single Mindustry entity to the packet.
     *
     * @param entity the entity to be written
     */
    public void write(Entityc entity) {
        serializedList.add(Entities.write(entity));
    }

    /**
     * Writes a List of Mindustry entities to the packet.
     *
     * @param entities the List of the entities to be written
     */
    public void write(List<Entityc> entities) {
        entities.forEach(entity -> {
            serializedList.add(Entities.write(entity));
        });
    }

    /**
     * Writes a List of Mindustry entities to the packet.
     *
     * @param entityGroup the group of entities to be written
     */
    public void write(EntityGroup<Entityc> entityGroup) {
        entityGroup.each(entity -> {
            serializedList.add(Entities.write(entity));
        });
    }

    /**
     * Reads a serialized list of Mindustry entities from the packet.

     * @return the read List of Mindustry entities
     */
    public List<Entityc> read() {
        List<Entityc> entities = new ArrayList<>();
        serializedList.forEach(serialized -> {
            entities.add(Entities.read(serialized));
        });
        return entities;
    }
}
