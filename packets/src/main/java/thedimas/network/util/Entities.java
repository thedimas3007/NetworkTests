package thedimas.network.util;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;

import java.io.*;

/**
 * The Entities class provides utility methods for working with Mindustry entities.
 */
public class Entities {
    /**
     * Serializes Mindustry entity
     *
     * @param entity the entity to be serialized
     * @return the serialized entity
     */
    public static byte[] write(Entityc entity) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutput dataOutput = new DataOutputStream(byteArrayOutputStream);
        Writes writes = new Writes(dataOutput);
        writes.b(entity.classId());
        entity.write(writes);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Deserialized Mindustry entity
     *
     * @param serialized the serialized entity
     * @return the deserialized entity
     */
    public static Entityc read(byte[] serialized) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serialized);
        DataInput dataInput = new DataInputStream(byteArrayInputStream);
        Reads reads = new Reads(dataInput);
        int classId = reads.ub();
        Entityc entityc = (Entityc) EntityMapping.map(classId).get();
        entityc.read(reads);
        return entityc;
    }
}
