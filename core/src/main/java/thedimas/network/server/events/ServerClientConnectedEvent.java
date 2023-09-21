package thedimas.network.server.events;

import lombok.*;
import thedimas.network.event.Event;
import thedimas.network.server.ServerClientHandler;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerClientConnectedEvent implements Event {
    ServerClientHandler client;
}
