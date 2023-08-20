package thedimas.network.packet;

import arc.util.io.Reads;
import arc.util.io.Writes;
import lombok.*;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;

import java.io.*;

/**
 * The MindustryEntityPacket class represents a packet containing a serialized Mindustry game entity.
 * It provides methods to write and read entities for network communication.
 */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutput dataOutput = new DataOutputStream(byteArrayOutputStream);
        Writes writes = new Writes(dataOutput);
        writes.b(entity.classId());
        entity.write(writes);
        serialized = byteArrayOutputStream.toByteArray();
    }

    /**
     * Reads a serialized Mindustry entity from the packet.
     *
     * @return the read Mindustry entity
     */
    public Entityc read() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serialized);
        DataInput dataInput = new DataInputStream(byteArrayInputStream);
        Reads reads = new Reads(dataInput);
        int classId = reads.ub();
        Entityc entityc = (Entityc) EntityMapping.map(classId).get();
        entityc.read(reads);
        return entityc;
    }
}
