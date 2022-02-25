package dev.ratas.aggressiveanimals.config.messaging.context;

public interface DoubleContext<T, U> extends Context {

    T getFirstContent();

    U getSecondContent();
}
