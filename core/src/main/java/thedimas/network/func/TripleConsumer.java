package thedimas.network.func;

@FunctionalInterface
public interface TripleConsumer<T, U, V> {
    void accept(T t, U u, V v);
}
