package thedimas.network.packet;

import lombok.*;

import java.io.Serializable;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePacket<T extends Serializable> implements Packet {
    int target;
    T response;
}
