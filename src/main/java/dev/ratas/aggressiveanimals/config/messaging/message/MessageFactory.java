package dev.ratas.aggressiveanimals.config.messaging.message;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.Validate;

import dev.ratas.aggressiveanimals.config.messaging.context.Context;
import dev.ratas.aggressiveanimals.config.messaging.context.DoubleContext;
import dev.ratas.aggressiveanimals.config.messaging.context.factory.ContextFactory;
import dev.ratas.aggressiveanimals.config.messaging.context.factory.DoubleContextFactory;

public class MessageFactory<C extends Context> {
    private final Class<?> messageClass;
    // private final Class<?> contextClass; // instead of class, use a context
    // factory
    private final Class<?> contextClass;
    private final ContextFactory<C> contextFactory;
    private final Constructor<Message<C>> messageConstructor;
    private final String raw;

    @SuppressWarnings("unchecked") // M extends Message<C>, CF extends ContextFactory<C>,
    public MessageFactory(Class<?> messageClass, Class<?> contextClass, ContextFactory<C> contextFactory, String raw) {
        Validate.isAssignableFrom(Message.class, messageClass, "Illegal message class: " + messageClass);
        Validate.isAssignableFrom(Context.class, contextClass, "Illegal context class: " + contextClass);
        this.messageClass = messageClass;
        this.contextClass = contextClass;
        this.contextFactory = contextFactory;
        this.raw = raw;
        Constructor<Message<C>> msgConstructor;
        // try {
        try {
            msgConstructor = (Constructor<Message<C>>) this.messageClass.getConstructor(String.class,
                    this.contextClass);
        } catch (NoSuchMethodException | SecurityException e) {
            try { // due to type parameters being reduced to Object in runtime
                msgConstructor = (Constructor<Message<C>>) this.messageClass.getConstructor(String.class, Object.class);
            } catch (NoSuchMethodException | SecurityException e1) {
                e.printStackTrace();
                throw new IllegalArgumentException(e1);
            }
        }
        // } catch (NoSuchMethodExccontextClasseption | SecurityException e) {
        // }
        this.messageConstructor = msgConstructor;
    }

    public ContextFactory<C> getContextFactory() {
        return contextFactory;
    }

    public Message<C> getMessage(C context) {
        try {
            return messageConstructor.newInstance(raw, context);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static class DoubleFactory<U, V> extends MessageFactory<DoubleContext<U, V>> {

        public DoubleFactory(Class<?> messageClass, Class<?> contextClass, DoubleContextFactory<U, V> contextFactory,
                String raw) {
            super(messageClass, contextClass, contextFactory, raw);
        }

        @SuppressWarnings("unchecked")
        public DoubleContextFactory<U, V> getContextFactory() {
            return (DoubleContextFactory<U, V>) super.getContextFactory();
        }

    }

}
