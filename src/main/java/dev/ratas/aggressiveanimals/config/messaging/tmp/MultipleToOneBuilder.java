package dev.ratas.aggressiveanimals.config.messaging.tmp;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import dev.ratas.slimedogcore.api.messaging.context.factory.SDCSingleContextFactory;
import dev.ratas.slimedogcore.api.messaging.delivery.MessageTarget;
import dev.ratas.slimedogcore.api.messaging.factory.SDCSingleContextMessageFactory;
import dev.ratas.slimedogcore.impl.messaging.context.factory.SingleContextFactory;
import dev.ratas.slimedogcore.impl.messaging.factory.SingleContextMessageFactory;

public final class MultipleToOneBuilder<T> {
    private final String raw;
    private final List<SDCSingleContextFactory<T>> delegates = new ArrayList<>();

    public MultipleToOneBuilder(String raw) {
        this.raw = raw;
    }

    public MultipleToOneBuilder<T> with(String placeholder, Function<T, String> mapper) {
        delegates.add(new SingleContextFactory<>(placeholder, mapper));
        return this;
    }

    @SuppressWarnings("unchecked")
    public SDCSingleContextMessageFactory<T> build() {
        DelegatingMultipleToOneContextFactory<T> df = new DelegatingMultipleToOneContextFactory<>(
                delegates.toArray(new SDCSingleContextFactory[0]));
        return new SingleContextMessageFactory<>(df, raw, MessageTarget.TEXT);
    }

}
