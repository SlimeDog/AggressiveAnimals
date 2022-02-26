package dev.ratas.aggressiveanimals.aggressive.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import dev.ratas.aggressiveanimals.aggressive.settings.type.Builder;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.config.Settings;
import dev.ratas.slimedogcore.api.SlimeDogPlugin;
import dev.ratas.slimedogcore.api.config.SDCConfiguration;

public class MobTypeManager {
    private final SlimeDogPlugin plugin;
    private final Map<MobType, MobTypeSettings> types = new EnumMap<>(MobType.class);

    public MobTypeManager(SlimeDogPlugin plugin, Settings settings) {
        this.plugin = plugin;
        loadMobs(settings);
    }

    private void loadMobs(Settings settings) {
        SDCConfiguration section = settings.getMobSection();
        if (section == null) {
            plugin.getLogger().info("No section for 'mobs' found so no per-mob configuration was loaded");
            return;
        }
        for (String key : section.getKeys(false)) {
            Builder builder = new Builder(section.getConfigurationSection(key));
            MobTypeSettings typeSettings;
            try {
                typeSettings = builder.build();
            } catch (Builder.IllegalMobTypeSettingsException e) {
                plugin.getLogger().warning("Unable to load settings for mob type " + key
                        + ": unknown entity type; please check the configuration");
                continue;
            }
            types.put(typeSettings.entityType(), typeSettings);
        }
    }

    public boolean isManaged(MobType type) {
        return getEnabledSettings(type) != null;
    }

    public MobTypeSettings getEnabledSettings(MobType type) {
        MobTypeSettings settings = types.get(type);
        if (settings == null) {
            return null;
        }
        return settings.enabled() ? settings : null;
    }

    public void reload(Settings settings) {
        types.clear();
        loadMobs(settings);
    }

    public Collection<MobTypeSettings> getUsedSettings() {
        List<MobTypeSettings> sorted = new ArrayList<>(types.values());
        sorted.sort((mts1, mts2) -> mts1.entityType().name().compareTo(mts2.entityType().name()));
        return sorted;
    }

}
