package thedimas.network.packet;

import lombok.*;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusPacket implements Packet {
    int wave, tps, ram, players, units;
    String map;
}
