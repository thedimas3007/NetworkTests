package thedimas.network.client.events;

import lombok.*;
import thedimas.network.enums.DcReason;
import thedimas.network.event.Event;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientDisconnectedEvent implements Event {
    DcReason reason;
}
