package dev.ratas.aggressiveanimals.config.messaging.context.factory;

import dev.ratas.aggressiveanimals.config.messaging.context.TripleContext;
import dev.ratas.aggressiveanimals.config.messaging.context.delegating.DelegatingTripleContext;

public class DelegatingTripleContextFactory<T, U, V> implements ContextFactory<TripleContext<T, U, V>> {
    private final SingleContextFactory<T> tDelegate;
    private final SingleContextFactory<U> uDelegate;
    private final SingleContextFactory<V> vDelegate;

    public DelegatingTripleContextFactory(SingleContextFactory<T> tDelegate, SingleContextFactory<U> uDelegate,
            SingleContextFactory<V> vDelegate) {
        this.tDelegate = tDelegate;
        this.uDelegate = uDelegate;
        this.vDelegate = vDelegate;
    }

    public TripleContext<T, U, V> getContext(T t, U u, V v) {
        return new DelegatingTripleContext<>(tDelegate.getContext(t), uDelegate.getContext(u), vDelegate.getContext(v));
    }

}
