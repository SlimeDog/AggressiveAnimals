package dev.ratas.aggressiveanimals.config.messaging;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Context {

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

    public static interface TripleBuilder<T, U, V> {

        Context context(T t, U u, V v);

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

    public static class HelperDelegateBuilder<T, R, H> implements MultiBuilder<R, H> {
        private final BiFunction<R, H, T> helperFunction;
        private final ContextBuilder<T> delegate;

        public HelperDelegateBuilder(ContextBuilder<T> delegate, BiFunction<R, H, T> helperFunction) {
            this.delegate = delegate;
            this.helperFunction = helperFunction;
        }

        @Override
        public Context context(R r, H h) {
            return delegate.context(helperFunction.apply(r, h));
        }

    }

    public static class DelegateWithMultiContextBuilder<T, U, V> implements TripleBuilder<T, U, V> {
        private final ContextBuilder<T> delegate1;
        private final MultiBuilder<U, V> delegate2;

        public DelegateWithMultiContextBuilder(ContextBuilder<T> delegate1, MultiBuilder<U, V> delegate2) {
            this.delegate1 = delegate1;
            this.delegate2 = delegate2;
        }

        @Override
        public Context context(T t, U u, V v) {
            return new DelegatingContext(delegate1.context(t), delegate2.context(u, v));
        }

    }

}
