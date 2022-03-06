package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.Set;

import org.bukkit.World;

public record MobWorldSettings(Set<String> enabledWorlds, Set<String> disabledWorlds) {

    public boolean isEnabledInWorld(World world) {
        return isEnabledInWorld(world.getName());
    }

    public boolean isEnabledInWorld(String worldName) {
        if (disabledWorlds.contains(worldName)) {
            return false;
        }
        if (enabledWorlds.isEmpty() || enabledWorlds.contains(worldName)) {
            return true;
        }
        return false;
    }

}
