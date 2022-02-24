package dev.ratas.aggressiveanimals.config.messaging;

import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.config.messaging.Context.ContextBuilder;
import dev.ratas.aggressiveanimals.config.messaging.Context.DelegateWithMultiContextBuilder;
import dev.ratas.aggressiveanimals.config.messaging.Context.HelperDelegateBuilder;
import dev.ratas.aggressiveanimals.config.messaging.Context.MultiBuilder;
import dev.ratas.aggressiveanimals.config.messaging.Context.TripleBuilder;

public final class DefinedContexts {
    public static final String PLAYER_TARGET_PLACEHOLDER = "%target%";
    public static final String ENTITY_TYPE_PLACEHOLDER = "%mob-type%";
    public static final String STATUS_PLACEHOLDER = "%status%";
    public static final ContextBuilder<Player> PLAYER_TARGET = new ContextBuilder<>(
            PLAYER_TARGET_PLACEHOLDER, p -> p.getName());
    public static final ContextBuilder<MobType> MOB_TYPE = new ContextBuilder<>(
            ENTITY_TYPE_PLACEHOLDER, e -> e.name());
    public static final ContextBuilder<String> STATUS = new ContextBuilder<>(
            STATUS_PLACEHOLDER, b -> b);
    public static final MultiBuilder<Boolean, Messages> STATUS_CUSTOM = new HelperDelegateBuilder<>(
            STATUS, (b, m) -> {
                Message msg = b ? m.getEnabledMessage() : m.getDisabledMessage();
                return msg.getRaw();
            });
    public static final TripleBuilder<MobType, Boolean, Messages> TYPE_SETTINGS = new DelegateWithMultiContextBuilder<>(
            MOB_TYPE,
            STATUS_CUSTOM);

    private DefinedContexts() {
        // simply a listing class
    }

}
