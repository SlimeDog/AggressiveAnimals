package dev.ratas.aggressiveanimals.config.messaging.context.factory;

import dev.ratas.aggressiveanimals.config.messaging.context.Context;
import dev.ratas.aggressiveanimals.config.messaging.context.Context.VoidContext;

public interface ContextFactory<T extends Context> {
    public static final VoidContextFactory NULL = new VoidContextFactory();

    public static class VoidContextFactory implements ContextFactory<VoidContext> {

        private VoidContextFactory() {
            // the single instance
        }

        public Context getContext() {
            return Context.NULL;
        }

    }

}
