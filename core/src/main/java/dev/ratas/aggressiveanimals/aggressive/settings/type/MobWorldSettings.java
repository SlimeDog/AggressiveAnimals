package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.Set;

import org.bukkit.World;

public record MobWorldSettings(Setting<Set<String>> enabledWorlds, Setting<Set<String>> disabledWorlds) {

    public boolean isEnabledInWorld(World world) {
        return isEnabledInWorld(world.getName());
    }

    public boolean isEnabledInWorld(String worldName) {
        if (disabledWorlds.value().contains(worldName)) {
            return false;
        }
        if (enabledWorlds.value().isEmpty() || enabledWorlds.value().contains(worldName)) {
            return true;
        }
        return false;
    }

}
