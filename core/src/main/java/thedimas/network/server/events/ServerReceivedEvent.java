package thedimas.network.server.events;

import lombok.*;
import thedimas.network.event.Event;
import thedimas.network.packet.Packet;
import thedimas.network.server.ServerClientHandler;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerReceivedEvent implements Event {
    ServerClientHandler client;
    Packet packet;
}
