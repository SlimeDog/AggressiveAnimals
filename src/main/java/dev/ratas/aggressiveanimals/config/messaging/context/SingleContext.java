package dev.ratas.aggressiveanimals.config.messaging.context;

import java.util.function.Function;

import dev.ratas.aggressiveanimals.config.messaging.context.impl.SimpleSingleContext;

public interface SingleContext<T> extends Context {

    T getContents();

    class Factory<T> {
        private final String placeholder;
        private final Function<T, String> mapper;

        public Factory(String placeholder, Function<T, String> mapper) {
            this.placeholder = placeholder;
            this.mapper = mapper;
        }

        public SingleContext<T> context(T to) {
            return new SimpleSingleContext<>(placeholder, to, mapper);
        }

    }

}
