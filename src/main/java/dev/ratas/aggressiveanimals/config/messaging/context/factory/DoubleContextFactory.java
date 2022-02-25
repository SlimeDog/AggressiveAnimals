package dev.ratas.aggressiveanimals.config.messaging.context.factory;

import dev.ratas.aggressiveanimals.config.messaging.context.DoubleContext;

public interface DoubleContextFactory<T, U> extends ContextFactory<DoubleContext<T, U>> {

    DoubleContext<T, U> getContext(T t, U u);

}
