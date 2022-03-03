package dev.ratas.aggressiveanimals.config.messaging.tmp;

import org.apache.commons.lang.Validate;

import dev.ratas.slimedogcore.api.messaging.context.SDCSingleContext;

public class DelegatingMultipleToOneContext<T> implements SDCSingleContext<T> {
    private final SDCSingleContext<T>[] delegates;
    private final T content;

    public DelegatingMultipleToOneContext(SDCSingleContext<T>[] delegates) {
        Validate.notEmpty(delegates, "Need at least one delegte");
        this.content = delegates[0].getContents();
        for (int i = 1; i < delegates.length; i++) {
            T other = delegates[i].getContents();
            Validate.isTrue(this.content == other, "All contents should be the same");
        }
        this.delegates = delegates;
    }

    @Override
    public String fill(String msg) {
        for (SDCSingleContext<T> context : delegates) {
            msg = context.fill(msg);
        }
        return msg;
    }

    @Override
    public T getContents() {
        return content;
    }

}
