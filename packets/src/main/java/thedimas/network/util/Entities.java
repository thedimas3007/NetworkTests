package thedimas.network.util;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;

import java.io.*;

public class Entities {
    public static byte[] write(Entityc entity) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutput dataOutput = new DataOutputStream(byteArrayOutputStream);
        Writes writes = new Writes(dataOutput);
        writes.b(entity.classId());
        entity.write(writes);
        return byteArrayOutputStream.toByteArray();
    }

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
