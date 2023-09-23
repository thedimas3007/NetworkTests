package thedimas.network.packet;

import lombok.*;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PingPacket implements Packet {
    long created;
}
