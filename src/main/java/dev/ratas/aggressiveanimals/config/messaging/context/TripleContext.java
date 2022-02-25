package dev.ratas.aggressiveanimals.config.messaging.context;

public interface TripleContext<T, U, V> extends Context {

    T getFirstContent();

    U getSecondContent();

    V getThirdContent();

}
