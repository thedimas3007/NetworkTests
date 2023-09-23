package thedimas.network.packet;

import lombok.*;

import java.io.Serializable;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ObjectPacket<T extends Serializable> implements Packet {
    T payload;
}
