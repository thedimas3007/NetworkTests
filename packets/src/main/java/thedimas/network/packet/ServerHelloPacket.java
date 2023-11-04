package thedimas.network.packet;

import lombok.*;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerHelloPacket implements Packet {
    String server;
}
