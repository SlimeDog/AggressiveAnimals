package dev.ratas.aggressiveanimals.config.messaging.context.factory;

import dev.ratas.aggressiveanimals.config.messaging.context.DoubleContext;
import dev.ratas.aggressiveanimals.config.messaging.context.delegating.DelegatingDoubleContext;

public class DelegatingDoubleContextFactory<T, U> implements DoubleContextFactory<T, U> {
    private final SingleContextFactory<T> tDelegate;
    private final SingleContextFactory<U> uDelegate;

    public DelegatingDoubleContextFactory(SingleContextFactory<T> tDelegate, SingleContextFactory<U> uDelegate) {
        this.tDelegate = tDelegate;
        this.uDelegate = uDelegate;
    }

    @Override
    public DoubleContext<T, U> getContext(T t, U u) {
        return new DelegatingDoubleContext<>(tDelegate.getContext(t), uDelegate.getContext(u));
    }

}
