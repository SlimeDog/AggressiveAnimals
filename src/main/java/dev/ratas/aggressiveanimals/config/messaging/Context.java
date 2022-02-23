package dev.ratas.aggressiveanimals.config.messaging;

import org.bukkit.entity.Player;

public class Context {
    private static final String TARGET_PLACEHOLDER = "{target}";
    private final String placeholder;
    private final Player namedTarget;

    private Context(String placeholder, Player namedTarget) {
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

    public static Context playerTarget(String placeholder, Player player) {
        return new Context(placeholder, player);
    }

    public static Context defaultPlayerTarget(Player player) {
        return playerTarget(TARGET_PLACEHOLDER, player);
    }

}
