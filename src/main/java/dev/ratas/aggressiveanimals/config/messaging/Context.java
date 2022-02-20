package dev.ratas.aggressiveanimals.config.messaging;

import org.bukkit.entity.Player;

public class Context {
    private static final String TARGET_PLACEHOLDER = "{target}";
    private final Player namedTarget;

    private Context(Player namedTarget) {
        this.namedTarget = namedTarget;
    }

    public String fill(String msg) {
        if (namedTarget != null) {
            msg = replaceWith(msg, TARGET_PLACEHOLDER, namedTarget.getName());
        }
        return msg;
    }

    private String replaceWith(String msg, String target, String replacement) {
        return msg.replace(target, replacement);
    }

    public static class Builder {
        private Player namedTarget;

        public Builder withNamedTarget(Player namedTarget) {
            this.namedTarget = namedTarget;
            return this;
        }

        public Context build() {
            return new Context(namedTarget);
        }
    }

}
