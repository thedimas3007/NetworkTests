package thedimas.network.packet;

import lombok.*;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JsCallPacket implements Packet {
    String jsCode;
}
