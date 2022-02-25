package dev.ratas.aggressiveanimals.config.messaging.context.delegating;

import dev.ratas.aggressiveanimals.config.messaging.context.DoubleContext;
import dev.ratas.aggressiveanimals.config.messaging.context.SingleContext;

public class DelegatingDoubleContext<T, U> implements DoubleContext<T, U> {
    private final SingleContext<T> delegate1;
    private final SingleContext<U> delegate2;

    public DelegatingDoubleContext(SingleContext<T> delegate1, SingleContext<U> delegate2) {
        this.delegate1 = delegate1;
        this.delegate2 = delegate2;
    }

    @Override
    public String fill(String msg) {
        msg = delegate1.fill(msg);
        msg = delegate2.fill(msg);
        return msg;
    }

    @Override
    public T getFirstContent() {
        return delegate1.getContents();
    }

    @Override
    public U getSecondContent() {
        return delegate2.getContents();
    }

}
