package thedimas.network.client.events;

import lombok.*;
import thedimas.network.event.Event;
import thedimas.network.packet.Packet;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientReceivedEvent implements Event {
    Packet packet;
}
