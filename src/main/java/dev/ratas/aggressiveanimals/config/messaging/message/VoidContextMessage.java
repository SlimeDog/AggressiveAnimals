package dev.ratas.aggressiveanimals.config.messaging.message;

import org.apache.commons.lang3.Validate;

import dev.ratas.aggressiveanimals.config.messaging.context.Context;

public class VoidContextMessage extends AbstractMessage<Context.VoidContext> {

    public VoidContextMessage(String raw, Context.VoidContext context) {
        super(raw, context);
        Validate.isTrue(context == Context.NULL, "Conatext for VoidContextMessage needs to be Context.NULL");
    }

}
