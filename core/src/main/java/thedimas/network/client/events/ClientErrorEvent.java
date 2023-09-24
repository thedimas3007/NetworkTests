package thedimas.network.client.events;

import lombok.*;
import thedimas.network.event.Event;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientErrorEvent implements Event {
    String message;
    Throwable error;
}
