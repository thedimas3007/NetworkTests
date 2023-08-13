package thedimas.network.type;

import lombok.*;

import java.io.Serializable;


@Data
@AllArgsConstructor
@Builder
public class Player implements Serializable { // TODO: from mindustry.gen.Player
    private final String uuid;
    private final String name;
    private final String ip;
    private final String locale;
    private int id;
}
