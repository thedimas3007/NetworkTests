package thedimas.network.packet;

import lombok.*;
import mindustry.entities.EntityGroup;
import mindustry.gen.Entityc;
import thedimas.network.util.Entities;

import java.util.ArrayList;
import java.util.List;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressWarnings("unused")
public class MindustryEntityListPacket implements Packet {
    List<byte[]> serializedList = new ArrayList<>();

    public void write(Entityc entity) {
        serializedList.add(Entities.write(entity));
    }

    public void write(List<Entityc> entities) {
        entities.forEach(entity -> serializedList.add(Entities.write(entity)));
    }

    public void write(EntityGroup<Entityc> entityGroup) {
        entityGroup.each(entity -> serializedList.add(Entities.write(entity)));
    }

    public List<Entityc> read() {
        List<Entityc> entities = new ArrayList<>();
        serializedList.forEach(serialized -> entities.add(Entities.read(serialized)));
        return entities;
    }
}
