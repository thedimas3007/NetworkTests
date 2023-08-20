package thedimas.network.packet;

import arc.util.io.Reads;
import arc.util.io.Writes;
import lombok.*;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;
import mindustry.gen.Player;

import java.io.*;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MindustryEntityPacket implements Packet {
    byte[] serialized;

    public void write(Entityc entity) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutput dataOutput = new DataOutputStream(byteArrayOutputStream);
        Writes writes = new Writes(dataOutput);
        writes.b(entity.classId());
        entity.write(writes);
        serialized = byteArrayOutputStream.toByteArray();
    }

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
