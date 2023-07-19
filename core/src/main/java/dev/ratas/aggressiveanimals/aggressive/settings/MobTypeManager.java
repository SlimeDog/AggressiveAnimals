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
    private final MobTypeSettings.DefaultMobTypeSettings inCodeDefaults;
    private MobTypeSettings.DefaultMobTypeSettings inConfigDefaults;

    public MobTypeManager(SlimeDogPlugin plugin, Settings settings) {
        this.plugin = plugin;
        SDCConfiguration defSection = settings.getDefaultsSection().getDefaultSection();
        inCodeDefaults = new MobTypeSettings.DefaultMobTypeSettings(
                new Builder(defSection, defSection, plugin.getLogger()).build());
        loadMobs(settings);
    }

    private void loadMobs(Settings settings) {
        SDCConfiguration section = settings.getMobSection();
        if (section == null) {
            plugin.getLogger().info("No section for 'mobs' found so no per-mob configuration was loaded");
            return;
        }
        Builder builder = new Builder(settings.getDefaultsSection(), inCodeDefaults, plugin.getLogger());
        try {
            inConfigDefaults = new MobTypeSettings.DefaultMobTypeSettings(builder.build());
        } catch (Builder.IllegalMobTypeSettingsException e) {
            // TODO - show warning?
            inConfigDefaults = new MobTypeSettings.DefaultMobTypeSettings(inCodeDefaults.getSettings());
        }
        for (String key : section.getKeys(false)) {
            builder = new Builder(section.getConfigurationSection(key), inConfigDefaults, plugin.getLogger());
            MobTypeSettings typeSettings;
            try {
                typeSettings = builder.build();
            } catch (Builder.IllegalMobTypeSettingsException e) {
                plugin.getLogger().warning("Unable to load settings for mob type " + key
                        + ": unknown entity type; please check the configuration: " + e.getMessage());
                continue;
            }
            types.put(typeSettings.entityType().value(), typeSettings);
        }
    }

    public boolean isManaged(MobType type) {
        return getEnabledSettings(type) != null;
    }

    /**
     * Gets the in code defaults.
     *
     * @return
     */
    public MobTypeSettings.DefaultMobTypeSettings getInCodeDefaultSettings() {
        return inCodeDefaults;
    }

    /**
     * Gets the in-config defaults.
     *
     * @return
     */
    public MobTypeSettings.DefaultMobTypeSettings getConfigDefaultSettings() {
        return inConfigDefaults;
    }

    public MobTypeSettings getDefinedSettings(MobType type) {
        return types.get(type);
    }

    public MobTypeSettings getEnabledSettings(MobType type) {
        MobTypeSettings settings = types.get(type);
        if (settings == null) {
            return null;
        }
        return settings.enabled().value() ? settings : null;
    }

    public void reload(Settings settings) {
        types.clear();
        loadMobs(settings);
    }

    public Collection<MobTypeSettings> getUsedSettings() {
        List<MobTypeSettings> sorted = new ArrayList<>(types.values());
        sorted.sort((mts1, mts2) -> mts1.entityType().value().name().compareTo(mts2.entityType().value().name()));
        return sorted;
    }

    public Collection<MobTypeSettings> getEnabledSettings() {
        List<MobTypeSettings> sorted = new ArrayList<>(types.values());
        sorted.removeIf(mts -> !mts.enabled().value());
        sorted.sort((mts1, mts2) -> mts1.entityType().value().name().compareTo(mts2.entityType().value().name()));
        return sorted;
    }

    public Collection<MobTypeSettings> getDisabledSettings() {
        List<MobTypeSettings> sorted = new ArrayList<>(types.values());
        sorted.removeIf(mts -> mts.enabled().value());
        sorted.sort((mts1, mts2) -> mts1.entityType().value().name().compareTo(mts2.entityType().value().name()));
        return sorted;
    }

}
