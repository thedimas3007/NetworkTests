package thedimas.network.packet;

import lombok.*;

import java.time.Instant;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PingPacket implements Packet {
    long created;
}
