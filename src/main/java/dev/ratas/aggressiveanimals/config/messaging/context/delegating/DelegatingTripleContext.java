package dev.ratas.aggressiveanimals.config.messaging.context.delegating;

import dev.ratas.aggressiveanimals.config.messaging.context.SingleContext;
import dev.ratas.aggressiveanimals.config.messaging.context.TripleContext;

public class DelegatingTripleContext<T, U, V> implements TripleContext<T, U, V> {
    private final SingleContext<T> delegate1;
    private final SingleContext<U> delegate2;
    private final SingleContext<V> delegate3;

    public DelegatingTripleContext(SingleContext<T> delegate1, SingleContext<U> delegate2, SingleContext<V> delegate3) {
        this.delegate1 = delegate1;
        this.delegate2 = delegate2;
        this.delegate3 = delegate3;
    }

    @Override
    public String fill(String msg) {
        msg = delegate1.fill(msg);
        msg = delegate2.fill(msg);
        msg = delegate3.fill(msg);
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

    @Override
    public V getThirdContent() {
        return delegate3.getContents();
    }

}
