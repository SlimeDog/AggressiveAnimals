package dev.ratas.aggressiveanimals.config.messaging.message;

import dev.ratas.aggressiveanimals.config.messaging.context.DoubleContext;

public class DoubleContextMessage<U, V> extends AbstractMessage<DoubleContext<U, V>> {

    public DoubleContextMessage(String raw, DoubleContext<U, V> context) {
        super(raw, context);
    }

}
