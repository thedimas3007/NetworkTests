package thedimas.network.packet;

import lombok.*;
import org.jetbrains.annotations.Nullable;
import thedimas.network.enums.ServerEvent;

import java.io.Serializable;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerEventPacket<T extends Serializable> implements Packet {
    ServerEvent event;
    @Nullable T payload;
}
