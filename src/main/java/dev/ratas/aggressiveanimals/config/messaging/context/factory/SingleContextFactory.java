package dev.ratas.aggressiveanimals.config.messaging.context.factory;

import java.util.function.Function;

import dev.ratas.aggressiveanimals.config.messaging.context.SingleContext;
import dev.ratas.aggressiveanimals.config.messaging.context.impl.SimpleSingleContext;

public class SingleContextFactory<T> implements ContextFactory<SingleContext<T>> {
    private final String placeholder;
    private final Function<T, String> mapper;

    public SingleContextFactory(String placeholder, Function<T, String> mapper) {
        this.placeholder = placeholder;
        this.mapper = mapper;
    }

    public SingleContext<T> getContext(T t) {
        return new SimpleSingleContext<>(placeholder, t, mapper);
    }

}
