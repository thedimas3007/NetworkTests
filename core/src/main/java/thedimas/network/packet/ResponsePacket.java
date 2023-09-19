package thedimas.network.packet;

import lombok.*;
import thedimas.network.server.ServerClientHandler;

import java.io.Serializable;

/**
 * The ResponsePacker represents a response packet containing a target identifier and a response payload of a specified serializable type.
 *
 * @see ServerClientHandler#response(RequestPacket, Serializable)
 * @param <T> the type of the response payload, which must implement {@link Serializable}.
 */
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePacket<T extends Serializable> implements Packet {
    /**
     * The unique identifier of request packet
     *
     * @see RequestPacket#id
     */
    int target;

    /**
     * The response payload contained within this response packet.
     */
    T response;
}
