package thedimas.network.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventListener {
    private final Map<Class<?>, List<Consumer<Event>>> listeners = new HashMap<>();

    public <T extends Event> void on(Class<T> event, Consumer<T> consumer) {
        if (!listeners.containsKey(event)) {
            listeners.put(event, new ArrayList<>());
        }
        listeners.get(event).add((Consumer<Event>) consumer);
    }

    public void fire(Event event) {
        if (!listeners.containsKey(event.getClass())) {
            return;
        }
        listeners.get(event.getClass()).forEach(l -> l.accept(event));
    }
}
