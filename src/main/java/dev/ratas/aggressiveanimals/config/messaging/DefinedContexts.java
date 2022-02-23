package dev.ratas.aggressiveanimals.config.messaging;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.config.messaging.Context.ContextBuilder;
import dev.ratas.aggressiveanimals.config.messaging.Context.DelegateMultiContextBuilder;
import dev.ratas.aggressiveanimals.config.messaging.Context.MultiBuilder;

public final class DefinedContexts {
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

    private DefinedContexts() {
        // simply a listing class
    }

}
