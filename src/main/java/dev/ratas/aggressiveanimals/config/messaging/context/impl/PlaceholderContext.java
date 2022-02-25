package dev.ratas.aggressiveanimals.config.messaging.context.impl;

import dev.ratas.aggressiveanimals.config.messaging.context.Context;

public abstract class PlaceholderContext implements Context {
    protected final String placeholder;

    protected PlaceholderContext(String placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public String fill(String msg) {
        return msg.replace(placeholder, getReplacement());
    }

    protected abstract String getReplacement();

}
