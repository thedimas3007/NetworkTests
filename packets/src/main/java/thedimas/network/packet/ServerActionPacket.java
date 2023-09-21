package thedimas.network.packet;

import lombok.*;
import thedimas.network.enums.ServerAction;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerActionPacket implements Packet {
    ServerAction action;
}
