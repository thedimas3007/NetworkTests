package thedimas.network.type;

import lombok.*;

import java.io.Serializable;


@Data
@With
@AllArgsConstructor
@Builder
public class Player implements Serializable { // TODO: from mindustry.gen.Player
    private String uuid;
    private String name;
    private String ip;
    private String locale;
    private Integer id;
}
