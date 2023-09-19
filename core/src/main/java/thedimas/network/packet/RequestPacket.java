package thedimas.network.packet;

import lombok.*;
import thedimas.network.client.Client;

import java.util.function.Consumer;

/**
 * The RequestPacket represents a request packet containing an ID and a payload packet of a specified type.
 *
 * @see Client#request(Packet, Consumer)
 * @param <T> the type of payload packet.
 */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestPacket<T extends Packet> implements Packet {
    /**
     * The unique identifier for the request packet.
     */
    int id;

    /**
     * The payload packet contained within this request packet.
     */
    T packet;
}
