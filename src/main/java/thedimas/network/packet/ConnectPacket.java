package thedimas.network.packet;

import lombok.*;

/** Basic example packet based on {@link Packet} interface */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConnectPacket implements Packet {
    private String name;
    private String lang;
}
