package dev.ratas.aggressiveanimals.config.messaging.message;

import dev.ratas.aggressiveanimals.config.messaging.context.SingleContext;

public class SingleContextMessage<T extends SingleContext<U>, U> extends AbstractMessage<T> {

    public SingleContextMessage(String raw, T context) {
        super(raw, context);
    }

}
