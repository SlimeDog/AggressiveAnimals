package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.List;

import org.bukkit.World;

public record MobWorldSettings(Setting<List<String>> enabledWorlds, Setting<List<String>> disabledWorlds)
        implements CheckingSettingBoundle {

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

    @Override
    @SuppressWarnings("unchecked")
    public void checkAllTypes() throws IllegalStateException {
        @SuppressWarnings("rawtypes")
        Setting<List> ew = (Setting<List>) (Object) enabledWorlds;
        checkType(ew, List.class);
        @SuppressWarnings("rawtypes")
        Setting<List> dw = (Setting<List>) (Object) disabledWorlds;
        checkType(dw, List.class);
    }

}
