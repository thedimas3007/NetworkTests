package thedimas.network.packet;

import lombok.Getter;

import java.io.Serializable;

/**
 * The Packet abstract class represents a serializable packet that can be exchanged between the client and server.
 * Classes extending this abstract class define the structure and content of packets used in network communication.
 */
@Getter
public abstract class Packet implements Serializable {
    int id = (int) Math.round(Math.random() * Integer.MAX_VALUE);
}
