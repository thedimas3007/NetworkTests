package thedimas.network.packet;

import lombok.Getter;

import java.io.Serializable;

/**
 * The Packet interface represents a serializable packet that can be exchanged between the client and server.
 * Classes implementing this interface define the structure and content of packets used in network communication.
 */
public interface Packet extends Serializable {
    @Getter
    int id = (int) Math.round(Math.random() * Integer.MAX_VALUE);
}
