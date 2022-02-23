package dev.ratas.aggressiveanimals.config.messaging;

import java.util.function.Function;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public interface Context {
    public static final String PLAYER_TARGET_PLACEHOLDER = "%target%";
    public static final String ENTITY_TYPE_PLACEHOLDER = "%mob-type%";
    public static final String STATUS_PLACEHOLDER = "%status%";
    public static final ContextBuilder<Player> PLAYER_TARGET = new ContextBuilder<>(
            PLAYER_TARGET_PLACEHOLDER, p -> p.getName());
    public static final ContextBuilder<EntityType> MOB_TYPE = new ContextBuilder<>(
            ENTITY_TYPE_PLACEHOLDER, e -> e.name());
    public static final ContextBuilder<Boolean> STATUS = new ContextBuilder<>(
            ENTITY_TYPE_PLACEHOLDER, b -> String.valueOf(b));
    public static final MultiBuilder<EntityType, Boolean> TYPE_SETTINGS = new DelegateMultiContextBuilder<>(MOB_TYPE,
            STATUS);

    String fill(String msg);

    public static class StringReplacementContext implements Context {
        private final String placeholder;
        private final String replacement;

        public StringReplacementContext(String placeholder, String replacement) {
            this.placeholder = placeholder;
            this.replacement = replacement;
        }

        public String fill(String msg) {
            return replaceWith(msg, placeholder, replacement);
        }

        protected String replaceWith(String msg, String target, String replacement) {
            return msg.replace(target, replacement);
        }

    }

    public static class DelegatingContext implements Context {
        private final Context[] delegates;

        public DelegatingContext(Context... delegates) {
            this.delegates = delegates;
        }

        @Override
        public String fill(String msg) {
            for (Context delegate : delegates) {
                msg = delegate.fill(msg);
            }
            return msg;
        }
    }

    public static interface Builder<T> {

        Context context(T t);

    }

    public static interface MultiBuilder<T, V> {

        Context context(T t, V v);

    }

    public static class ContextBuilder<T> implements Builder<T> {
        private final String placeholder;
        private final Function<T, String> converter;

        public ContextBuilder(String placeholder, Function<T, String> converter) {
            this.placeholder = placeholder;
            this.converter = converter;
        }

        @Override
        public Context context(T t) {
            return new StringReplacementContext(placeholder, converter.apply(t));
        }
    }

    public static class DelegateMultiContextBuilder<T, V> implements MultiBuilder<T, V> {
        private final ContextBuilder<T> delegate1;
        private final ContextBuilder<V> delegate2;

        public DelegateMultiContextBuilder(ContextBuilder<T> delegate1, ContextBuilder<V> delegate2) {
            this.delegate1 = delegate1;
            this.delegate2 = delegate2;
        }

        @Override
        public Context context(T t, V v) {
            return new DelegatingContext(delegate1.context(t), delegate2.context(v));
        }

    }

}
