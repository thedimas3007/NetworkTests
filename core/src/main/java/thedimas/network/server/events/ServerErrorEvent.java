package thedimas.network.server.events;

import lombok.*;
import org.jetbrains.annotations.Nullable;
import thedimas.network.event.Event;
import thedimas.network.server.ServerClientHandler;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerErrorEvent implements Event {
    String message;
    @Nullable ServerClientHandler client;
    Throwable error;
}
