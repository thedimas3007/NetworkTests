package thedimas.network.packet;

import lombok.*;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestPacket<T extends Packet> implements Packet {
    int id;
    T packet;
}
