package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.MemoryConfiguration;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.slimedogcore.api.config.SDCConfiguration;
import dev.ratas.slimedogcore.impl.config.ConfigurationWrapper;

public class Builder {
    private static final DefaultMobTypeSettings IN_CODE_DEFAULT_SETTINGS = generateDefaultSettings();
    private final SDCConfiguration section;
    private Setting<MobType> type;
    private Setting<Boolean> enabled;
    private Setting<Double> speedMultiplier;
    private MobAttackSettings attackSettings;
    private MobAcquisitionSettings acquisitionSettings;
    private Setting<Double> attackerHealthThreshold;
    private MobAgeSettings ageSettings;
    private MobMiscSettings miscSettings;
    // private Setting<Boolean> overrideTargets;
    private Setting<Double> groupAgressionDistance;
    private PlayerStateSettings playerStateSettings;
    private MobWorldSettings worldSettings;
    private Setting<Boolean> alwaysAggressive;

    public Builder(SDCConfiguration section) {
        this.section = section;
    }

    private void loadType() {
        String typeName = section.getName();
        try {
            type = new Setting<>("", MobType.from(typeName.toLowerCase()), MobType.defaults);
        } catch (IllegalArgumentException e) {
            throw new IllegalMobTypeSettingsException("Unknown entity type " + typeName + " (" + e.getMessage() + ")");
        }
    }

    private void loadEnabled() {
        String path = "enabled";
        boolean def = false;
        enabled = fromSection(section, path, def);
    }

    private void loadAlwaysAggressive() {
        String path = "always-aggressive";
        boolean def = false;
        alwaysAggressive = fromSection(section, path, def);
    }

    private void loadSpeed() {
        String path = "speed-multiplier";
        double def = 1.0D;
        speedMultiplier = fromSection(section, path, def);
    }

    private void loadAttackSettings() {
        String path = "attack-damage";
        double def = 1.0D;
        Setting<Double> damage = fromSection(section, path, def);
        path = "attack-damage-limit";
        def = 2.0D;
        Setting<Double> attackDamageLimit = fromSection(section, path, def);
        path = "attack-speed";
        def = 20.0D;
        Setting<Double> speed = fromSection(section, path, def);
        path = "attack-leap-height";
        float deff = 0.0F;
        Setting<Float> attackLeapHeight = fromSection(section, path, deff);
        attackSettings = new MobAttackSettings(damage, attackDamageLimit, speed, attackLeapHeight);
    }

    private void loadAcquisitionSettings() {
        String path = "acquisition-range";
        double def = 16.0D;
        Setting<Double> acquisitionRange = fromSection(section, path, def);
        path = "deacquisition-range";
        def = 20.0D;
        Setting<Double> deacquisitionRange = fromSection(section, path, def);
        acquisitionSettings = new MobAcquisitionSettings(acquisitionRange, deacquisitionRange);
    }

    private void loatAttackerHealthThreshold() {
        String path = "attacker-health-threshold";
        double def = 25.0D;
        attackerHealthThreshold = fromSection(section, path, def);
    }

    private void loadMobAgeSettings() {
        String path = "age.adult";
        boolean def = true;
        Setting<Boolean> attackAsAdult = fromSection(section, path, def);
        path = "age.baby";
        def = false;
        Setting<Boolean> attackAsBaby = fromSection(section, path, def);
        ageSettings = new MobAgeSettings(attackAsAdult, attackAsBaby);
    }

    private void loadMiscSettings() {
        String path = "include-npcs";
        boolean def = false;
        Setting<Boolean> includeNpcs = fromSection(section, path, def);
        path = "include-named-mobs";
        def = false;
        Setting<Boolean> includeNamedMobs = fromSection(section, path, def);
        path = "include-tamed-mobs";
        def = true;
        Setting<Boolean> includeTamed = fromSection(section, path, def);
        miscSettings = new MobMiscSettings(includeNpcs, includeNamedMobs, includeTamed);
    }

    // private void loadOverrideTargets() {
    // String path = "override-targeting";
    // boolean def = false;
    // overrideTargets = fromSection(section, path, def);
    // }

    private void loadGroupAgrewssionDistance() {
        String path = "group-aggression-distance";
        double def = 20.0D;
        groupAgressionDistance = fromSection(section, path, def);
    }

    public void loadPlayerStateSettings() {
        String path = "player-movement.standing";
        boolean def = true; // same default for all for now
        Setting<Boolean> attackStanding = fromSection(section, path, def);
        path = "player-movement.sneaking";
        Setting<Boolean> attackSneaking = fromSection(section, path, def);
        path = "player-movement.walking";
        Setting<Boolean> attackWalking = fromSection(section, path, def);
        path = "player-movement.sprinting";
        Setting<Boolean> attackSprinting = fromSection(section, path, def);
        path = "player-movement.looking";
        Setting<Boolean> attackLooking = fromSection(section, path, def);
        path = "player-movement.sleeping";
        Setting<Boolean> attackSleeping = fromSection(section, path, def);
        path = "player-movement.guiding";
        Setting<Boolean> attackGliding = fromSection(section, path, def);
        playerStateSettings = new PlayerStateSettings(attackStanding, attackSneaking, attackWalking, attackSprinting,
                attackLooking, attackSleeping, attackGliding);
    }

    private void loadWorldSettings() {
        String path = "enabled-worlds";
        Set<String> def = new HashSet<>(); // empty for both for now
        Setting<Set<String>> enabled = fromSection(section, path, def);
        path = "disabled-worlds";
        Setting<Set<String>> disabled = fromSection(section, path, def);
        worldSettings = new MobWorldSettings(enabled, disabled);
    }

    public MobTypeSettings build() throws IllegalMobTypeSettingsException {
        if (section == null) {
            throw new IllegalMobTypeSettingsException("Path does not defined a configuration section");
        }
        loadType();
        loadEnabled();
        loadSpeed();
        loadAttackSettings();
        loadAcquisitionSettings();
        loatAttackerHealthThreshold();
        loadMobAgeSettings();
        loadMiscSettings();
        loadAlwaysAggressive();
        // loadOverrideTargets();
        loadGroupAgrewssionDistance();
        loadPlayerStateSettings();
        loadPlayerStateSettings();
        loadWorldSettings();
        loadPlayerStateSettings();
        loadWorldSettings();
        if (type.value() != MobType.defaults && // let the defaults type be for now
                ((!type.value().isTameable() && type.value() != MobType.fox && type.value() != MobType.ocelot)
                        && !miscSettings.includeTamed().value())) {
            throw new IllegalMobTypeSettingsException(
                    "Cannot include tameable of " + type.value().name() + " since the mobtype is not tameable");
        }
        return new MobTypeSettings(type, enabled, speedMultiplier, attackSettings, acquisitionSettings,
                attackerHealthThreshold, ageSettings, miscSettings, alwaysAggressive, // overrideTargets,
                groupAgressionDistance,
                playerStateSettings, worldSettings);
    }

    public static DefaultMobTypeSettings getDefaultSettings() {
        return IN_CODE_DEFAULT_SETTINGS;
    }

    private static DefaultMobTypeSettings generateDefaultSettings() {
        SDCConfiguration emptySection = new ConfigurationWrapper(new MemoryConfiguration().createSection("defaults"));
        return new DefaultMobTypeSettings(new Builder(emptySection).build());
    }

    public static class IllegalMobTypeSettingsException extends IllegalStateException {

        public IllegalMobTypeSettingsException(String msg) {
            super(msg);
        }

    }

    private static <T> Setting<T> fromSection(SDCConfiguration section, String path, T inCodeDef) {
        SDCConfiguration defSect = section.getDefaultSection();
        if (defSect == null) {
            // no defaults set for the mob type - using in code defaults
            T val = getValue(section, path, inCodeDef);
            return new Setting<>(path, val, inCodeDef);
        } else {
            if (defSect.isSet(path)) {
                // value set in default config, but haven't checked user's config
                // only passing in code default value for typing - won't change the value
                T inDefConfig = getValue(defSect, path, inCodeDef);
                T val = getValue(section, path, inDefConfig);
                return new Setting<>(path, val, inDefConfig);
            } else {
                // value not set in default config
                // using user config or in code config
                T val = getValue(section, path, inCodeDef);
                return new Setting<>(path, val, inCodeDef);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getValue(SDCConfiguration section, String path, T def) {
        if (def instanceof Double) {
            return (T) (Double) section.getDouble(path, (double) def);
        } else if (def instanceof Float) {
            return (T) (Float) (float) section.getDouble(path, (double) (float) def);
        } else if (def instanceof Integer) {
            return (T) (Integer) section.getInt(path, (int) def);
        } else if (def instanceof Long) {
            return (T) (Long) section.getLong(path, (long) def);
        }
        return (T) section.get(path, def);
    }

    public static final class DefaultMobTypeSettings {
        private final MobTypeSettings settings;
        private final Map<String, Setting<?>> settingMap = new HashMap<>();

        private DefaultMobTypeSettings(MobTypeSettings settings) {
            this.settings = settings;
            this.populateMap();
        }

        private void populateMap() {
            for (Setting<?> s : this.settings.getAllSettings()) {
                settingMap.put(s.path(), s);
            }
        }

        public MobTypeSettings getSettings() {
            return settings;
        }

        public Setting<?> getSettingFor(Setting<?> other) {
            return settingMap.get(other.path());
        }

        public boolean isDefault(Setting<?> other) {
            Setting<?> fromThis = getSettingFor(other);
            if (fromThis == null) {
                throw new IllegalArgumentException("Unknown settings: " + other);
            }
            return fromThis.value().equals(other.value());
        }

    }

}
