package dev.ratas.aggressiveanimals.config.messaging.context.impl;

import java.util.function.Function;

import dev.ratas.aggressiveanimals.config.messaging.context.SingleContext;

public class SimpleSingleContext<T> extends PlaceholderContext implements SingleContext<T> {
    private final T t;
    private final Function<T, String> mapper;

    public SimpleSingleContext(String placeholder, T t, Function<T, String> mapper) {
        super(placeholder);
        this.t = t;
        this.mapper = mapper;
    }

    @Override
    public T getContents() {
        return t;
    }

    @Override
    protected String getReplacement() {
        return mapper.apply(t);
    }

}
