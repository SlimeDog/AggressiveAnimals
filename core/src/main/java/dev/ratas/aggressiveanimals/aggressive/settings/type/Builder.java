package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.List;
import java.util.logging.Logger;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings.DefaultMobTypeSettings;
import dev.ratas.slimedogcore.api.config.SDCConfiguration;
import dev.ratas.slimedogcore.api.config.exceptions.ConfigException;

public class Builder {
    private final SDCConfiguration section;
    private final SDCConfiguration defSection;
    private final DefaultMobTypeSettings optionalDefaults;
    private final Logger logger;
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

    public Builder(SDCConfiguration section, SDCConfiguration defSettings, Logger logger) {
        this(section, defSettings,
                defSettings instanceof DefaultMobTypeSettings ? (DefaultMobTypeSettings) defSettings : null, logger);
    }

    public Builder(SDCConfiguration section, SDCConfiguration defSettings, DefaultMobTypeSettings optionDefaults,
            Logger logger) {
        this.section = section;
        this.defSection = defSettings;
        this.optionalDefaults = optionDefaults;
        this.logger = logger;
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
        enabled = fromSection(section, path, defSection);
    }

    private void loadAlwaysAggressive() {
        String path = "always-aggressive";
        alwaysAggressive = fromSection(section, path, defSection);
    }

    private void loadSpeed() {
        String path = "speed-multiplier";
        speedMultiplier = fromSection(section, path, defSection);
    }

    private void loadAttackSettings() {
        String path = "attack-damage";
        Setting<Double> damage = fromSection(section, path, defSection);
        path = "attack-damage-limit";
        Setting<Double> attackDamageLimit = fromSection(section, path, defSection);
        path = "attack-speed";
        Setting<Double> speed = fromSection(section, path, defSection);
        path = "attack-leap-height";
        Setting<Double> attackLeapHeight = fromSection(section, path, defSection);
        attackSettings = new MobAttackSettings(damage, attackDamageLimit, speed, attackLeapHeight);
    }

    private void loadAcquisitionSettings() {
        String path = "acquisition-range";
        Setting<Double> acquisitionRange = fromSection(section, path, defSection);
        path = "deacquisition-range";
        Setting<Double> deacquisitionRange = fromSection(section, path, defSection);
        acquisitionSettings = new MobAcquisitionSettings(acquisitionRange, deacquisitionRange);
    }

    private void loatAttackerHealthThreshold() {
        String path = "attacker-health-threshold";
        attackerHealthThreshold = fromSection(section, path, defSection);
    }

    private void loadMobAgeSettings() {
        String path = "age.adult";
        Setting<Boolean> attackAsAdult = fromSection(section, path, defSection);
        path = "age.baby";
        Setting<Boolean> attackAsBaby = fromSection(section, path, defSection);
        ageSettings = new MobAgeSettings(attackAsAdult, attackAsBaby);
    }

    private void loadMiscSettings() {
        String path = "include-npcs";
        Setting<Boolean> includeNpcs = fromSection(section, path, defSection);
        path = "include-named-mobs";
        Setting<Boolean> includeNamedMobs = fromSection(section, path, defSection);
        path = "include-tamed-mobs";
        Setting<Boolean> includeTamed = fromSection(section, path, defSection);
        path = "protect-team-members";
        Setting<Boolean> protectTeammates = fromSection(section, path, defSection);
        path = "attack-only-in-water";
        Setting<Boolean> attackOnlyInWater = fromSection(section, path, defSection);
        miscSettings = new MobMiscSettings(includeNpcs, includeNamedMobs, includeTamed, protectTeammates,
                attackOnlyInWater);
    }

    // private void loadOverrideTargets() {
    // String path = "override-targeting";
    // boolean def = false;
    // overrideTargets = fromSection(section, path, def);
    // }

    private void loadGroupAgrewssionDistance() {
        String path = "group-aggression-range";
        groupAgressionDistance = fromSection(section, path, defSection);
    }

    public void loadPlayerStateSettings() {
        String path = "player-movement.standing";
        Setting<Boolean> attackStanding = fromSection(section, path, defSection);
        path = "player-movement.sneaking";
        Setting<Boolean> attackSneaking = fromSection(section, path, defSection);
        path = "player-movement.walking";
        Setting<Boolean> attackWalking = fromSection(section, path, defSection);
        path = "player-movement.sprinting";
        Setting<Boolean> attackSprinting = fromSection(section, path, defSection);
        path = "player-movement.looking";
        Setting<Boolean> attackLooking = fromSection(section, path, defSection);
        path = "player-movement.sleeping";
        Setting<Boolean> attackSleeping = fromSection(section, path, defSection);
        path = "player-movement.gliding";
        Setting<Boolean> attackGliding = fromSection(section, path, defSection);
        playerStateSettings = new PlayerStateSettings(attackStanding, attackSneaking, attackWalking, attackSprinting,
                attackLooking, attackSleeping, attackGliding);
    }

    private void loadWorldSettings() {
        String path = "enabled-worlds";
        Setting<List<String>> enabled = fromSection(section, path, defSection);
        path = "disabled-worlds";
        Setting<List<String>> disabled = fromSection(section, path, defSection);
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
        if (!isValidRegardingWaterAttack()) {
            logger.warning("Cannot set water-attack for mob type " + type.value().name()
                    + " because it is not an aquatic mob");
        }
        loadAlwaysAggressive();
        // loadOverrideTargets();
        loadGroupAgrewssionDistance();
        loadPlayerStateSettings();
        loadPlayerStateSettings();
        loadWorldSettings();
        loadPlayerStateSettings();
        loadWorldSettings();
        if (!isValidRegardingTamability()) {
            logger.warning(
                    "Cannot include tameable of " + type.value().name() + " since the mobtype is not tameable");
        }
        MobTypeSettings mts = new MobTypeSettings(type, enabled, speedMultiplier, attackSettings, acquisitionSettings,
                attackerHealthThreshold, ageSettings, miscSettings, alwaysAggressive, // overrideTargets,
                groupAgressionDistance, playerStateSettings, worldSettings);

        try {
            mts.checkAllTypes();
        } catch (IllegalStateException e) {
            throw new IllegalMobTypeSettingsException(e.getMessage());
        }
        return mts;
    }

    private boolean isValidRegardingWaterAttack() {
        if (type.value() == MobType.defaults) {
            return true;
        }
        if (type.value().isAquaticMob()) {
            return true;
        }
        if (miscSettings.attackOnlyInWater().isDefault()) {
            return true;
        }
        return false;
    }

    private boolean isValidRegardingTamability() {
        if (type.value() == MobType.defaults) {
            return true;
        }
        if (type.value().isTameable()) {
            return true;
        }
        MobType mobType = type.value();
        if (mobType == MobType.fox || mobType == MobType.ocelot) {
            return true;
        }
        if (miscSettings.includeTamed().isDefault()) {
            return true;
        }
        return false;
    }

    public static class IllegalMobTypeSettingsException extends IllegalStateException {

        public IllegalMobTypeSettingsException(String msg) {
            super(msg);
        }

    }

    public static class IllegalConfigurationOptionException extends ConfigException {

        protected IllegalConfigurationOptionException(String msg) {
            super(msg);
        }

    }

    private <T> Setting<T> fromSection(SDCConfiguration section, String path, SDCConfiguration defSection) {
        T defVal;
        if (defSection == null) {
            defVal = null; // assumably only for main-defaults
        } else {
            defVal = getValue(defSection, path, null); // default section should be guaranteed to have it
            if (defVal == null) {
                throw new IllegalArgumentException("Did not expect a null-valued default at " + path + " in "
                        + section.getName() + " (" + section.getValues(true) + ") ("
                        + (defSection == null ? null : defSection.getValues(true)) + ")");
            }
        }
        T val = getValue(section, path, defVal);
        if (defVal == null && val == null) {
            throw new IllegalArgumentException("Did not expect both default AND value being null for " + path + " in "
                    + section.getName() + " (" + section.getValues(true) + ") (" + defSection + ")");
        }
        return new Setting<>(path, val, defVal);
    }

    @SuppressWarnings("unchecked")
    private <T> T getValue(SDCConfiguration section, String path, T def) {
        if (optionalDefaults != null) {
            def = (T) optionalDefaults.getSettingFor(path).value();
        }
        if (!section.isSet(path)) {
            return def;
        }
        if (def instanceof Double) {
            if (!section.isDouble(path) && !section.isInt(path) && !section.isLong(path)) {
                throw new IllegalConfigurationOptionException("Expected floating point value: " + section.get(path));
            }
            return (T) (Double) section.getDouble(path);
        } else if (def instanceof Integer) {
            if (!section.isInt(path)) {
                throw new IllegalConfigurationOptionException("Expected integer value: " + section.get(path));
            }
            return (T) (Integer) section.getInt(path);
        } else if (def instanceof Long) {
            if (!section.isLong(path)) {
                throw new IllegalConfigurationOptionException("Expected integer value: " + section.get(path));
            }
            return (T) (Long) section.getLong(path);
        } else if (def instanceof Boolean) {
            if (!section.isBoolean(path)) {
                throw new IllegalConfigurationOptionException("Expected boolean value: " + section.get(path));
            }
            return (T) (Boolean) section.getBoolean(path);
        }
        return (T) section.get(path);
    }

}
