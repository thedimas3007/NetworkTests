package thedimas.network.packet;

import lombok.*;
import thedimas.network.type.Player;

/**
 * Test packet that holds a {@link Player}
 */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressWarnings("unused")
public class PlayerPacket implements Packet {
    private Player player;
}
