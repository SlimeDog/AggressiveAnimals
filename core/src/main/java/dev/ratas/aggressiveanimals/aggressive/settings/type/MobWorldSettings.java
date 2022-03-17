package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.List;

import org.bukkit.World;

public record MobWorldSettings(Setting<List<String>> enabledWorlds, Setting<List<String>> disabledWorlds) {

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
