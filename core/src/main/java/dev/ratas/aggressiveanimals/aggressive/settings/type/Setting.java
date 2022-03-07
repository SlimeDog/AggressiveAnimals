package dev.ratas.aggressiveanimals.aggressive.settings.type;

public record Setting<T>(String path, T value, T def) {

    public boolean isDefault() {
        return value.equals(def);
    }

}
