package thedimas.network.packet;

import lombok.*;
import thedimas.network.type.Player;

import java.io.Serializable;

/** Test packet that holds a {@link Player} */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SuppressWarnings("unused")
public class PlayerPacket implements Serializable {
    private Player player;
}
