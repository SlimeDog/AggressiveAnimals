package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.Collection;

public record Setting<T>(String path, T value, T def) {

    public boolean isDefault() {
        if (value instanceof Number) {
            return ((Number) value).floatValue() == ((Number) def).floatValue();
        } else if (value instanceof Collection) {
            Collection<?> l1 = (Collection<?>) value;
            Collection<?> l2 = (Collection<?>) def;
            if (l1.isEmpty() && l2.isEmpty()) {
                return true;
            }
        }
        return value.equals(def);
    }

}
