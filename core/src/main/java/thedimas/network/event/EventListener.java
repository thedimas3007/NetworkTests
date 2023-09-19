package thedimas.network.event;

import thedimas.network.packet.Packet;
import thedimas.network.server.ServerClientHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A generic event listener for registering and firing events of specific types.
 */
public class EventListener {
    private final Map<Class<?>, List<Consumer<Event>>> listeners = new HashMap<>();

    /**
     * Registers an event listener for events of a specific type.
     *
     * @param <T>      the type of event to listen for, represented by a class.
     * @param event    the class representing the type of event to listen for.
     * @param consumer the consumer function to execute when an event of the specified type is fired.
     */
    public <T extends Event> void on(Class<T> event, Consumer<T> consumer) {
        if (!listeners.containsKey(event)) {
            listeners.put(event, new ArrayList<>());
        }
        listeners.get(event).add((Consumer<Event>) consumer);
    }

    /**
     * Fires an event, causing registered listeners for that event type to execute.
     *
     * @param event The event to be fired.
     */
    public void fire(Event event) {
        if (!listeners.containsKey(event.getClass())) {
            return;
        }
        listeners.get(event.getClass()).forEach(l -> l.accept(event));
    }
}
