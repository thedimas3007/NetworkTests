package thedimas.network.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventListener {
    private final Map<Class<?>, List<Consumer<Event>>> listeners = new HashMap<>();

    public <T extends Event> void on(Class<T> event, Consumer<T> consumer) {
        listeners.computeIfAbsent(event, k -> new ArrayList<>())
                .add((Consumer<Event>) consumer);
    }

    public void fire(Event event) {
        listeners.computeIfAbsent(event.getClass(), k -> new ArrayList<>())
                .forEach(listener -> listener.accept(event));
    }
}
