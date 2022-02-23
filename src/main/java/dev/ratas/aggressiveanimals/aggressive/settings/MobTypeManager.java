package dev.ratas.aggressiveanimals.aggressive.settings;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import dev.ratas.aggressiveanimals.aggressive.settings.type.Builder;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.config.Settings;

public class MobTypeManager {
    private final JavaPlugin plugin;
    private final Map<EntityType, MobTypeSettings> types = new HashMap<>();

    public MobTypeManager(JavaPlugin plugin, Settings settings) {
        this.plugin = plugin;
        loadMobs(settings);
    }

    private void loadMobs(Settings settings) {
        ConfigurationSection section = settings.getMobSection();
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
                plugin.getLogger().warning("Unable to load mob type settings for " + key + ": " + e.getMessage());
                continue;
            }
            if (!typeSettings.enabled()) {
                continue; // ignoring since not enabled
            }
            types.put(typeSettings.entityType(), typeSettings);
        }
    }

    public boolean isManaged(EntityType type) {
        return getSettings(type) != null;
    }

    public MobTypeSettings getSettings(EntityType type) {
        return types.get(type);
    }

    public void reload(Settings settings) {
        types.clear();
        loadMobs(settings);
    }

    public Collection<MobTypeSettings> getUsedSettings() {
        return Collections.unmodifiableCollection(types.values());
    }

}
