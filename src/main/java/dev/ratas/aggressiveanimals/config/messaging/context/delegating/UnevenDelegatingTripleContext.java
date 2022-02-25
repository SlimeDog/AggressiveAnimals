package dev.ratas.aggressiveanimals.config.messaging.context.delegating;

import dev.ratas.aggressiveanimals.config.messaging.context.DoubleContext;
import dev.ratas.aggressiveanimals.config.messaging.context.SingleContext;
import dev.ratas.aggressiveanimals.config.messaging.context.TripleContext;

public class UnevenDelegatingTripleContext<T, U, V> implements TripleContext<T, U, V> {
    private final DoubleContext<T, U> delegate1;
    private final SingleContext<V> delegate2;

    public UnevenDelegatingTripleContext(DoubleContext<T, U> delegate1, SingleContext<V> delegate2) {
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
        return delegate1.getFirstContent();
    }

    @Override
    public U getSecondContent() {
        return delegate1.getSecondContent();
    }

    @Override
    public V getThirdContent() {
        return delegate2.getContents();
    }

}
