package thedimas.network.server.events;

import lombok.*;
import thedimas.network.enums.DcReason;
import thedimas.network.event.Event;
import thedimas.network.server.ServerClientHandler;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerDisconnectionEvent implements Event {
    ServerClientHandler client;
    DcReason reason;
}
