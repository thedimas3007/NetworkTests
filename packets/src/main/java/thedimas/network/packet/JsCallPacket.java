package thedimas.network.packet;

import lombok.*;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JsCallPacket extends Packet {
    String jsCode;
}
