package thedimas.network.packet;

import lombok.*;
import mindustry.gen.Entityc;
import thedimas.network.util.Entities;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressWarnings("unused")
public class MindustryEntityPacket implements Packet {
    byte[] serialized;

    public void write(Entityc entity) {
        serialized = Entities.write(entity);
    }

    public Entityc read() {
        return Entities.read(serialized);
    }
}
