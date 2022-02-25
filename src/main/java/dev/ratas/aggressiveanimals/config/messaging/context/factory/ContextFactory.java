package dev.ratas.aggressiveanimals.config.messaging.context.factory;

import dev.ratas.aggressiveanimals.config.messaging.context.Context;
import dev.ratas.aggressiveanimals.config.messaging.context.Context.VoidContext;

public interface ContextFactory<T extends Context> {
    VoidContextFactory NULL = new VoidContextFactory();

    class VoidContextFactory implements ContextFactory<VoidContext> {

        private VoidContextFactory() {
            // the single instance
        }

        public Context getContext() {
            return Context.NULL;
        }

    }

}
