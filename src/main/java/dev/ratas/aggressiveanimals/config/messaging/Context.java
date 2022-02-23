package dev.ratas.aggressiveanimals.config.messaging;

import org.bukkit.entity.Player;

public interface Context {
    String fill(String msg);

    public static class PlayerTargetContext implements Context {
        private static final String TARGET_PLACEHOLDER = "%target%";
        private final String placeholder;
        private final Player namedTarget;

        public PlayerTargetContext(String placeholder, Player namedTarget) {
            this.placeholder = placeholder;
            this.namedTarget = namedTarget;
        }

        public String fill(String msg) {
            if (namedTarget != null) {
                msg = replaceWith(msg, placeholder, namedTarget.getName());
            }
            return msg;
        }

        private String replaceWith(String msg, String target, String replacement) {
            return msg.replace(target, replacement);
        }

        public static Context defaultPlaceholder(Player player) {
            return new PlayerTargetContext(TARGET_PLACEHOLDER, player);
        }
    }

}
