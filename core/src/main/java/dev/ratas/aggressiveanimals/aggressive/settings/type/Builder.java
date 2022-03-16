package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.Set;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.slimedogcore.api.config.SDCConfiguration;

public class Builder {
    private final SDCConfiguration section;
    private final SDCConfiguration defSection;
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

    public Builder(SDCConfiguration section, SDCConfiguration defSettings) {
        this.section = section;
        this.defSection = defSettings;
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
        Setting<Float> attackLeapHeight = fromSection(section, path, defSection);
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
        miscSettings = new MobMiscSettings(includeNpcs, includeNamedMobs, includeTamed);
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
        Setting<Set<String>> enabled = fromSection(section, path, defSection);
        path = "disabled-worlds";
        Setting<Set<String>> disabled = fromSection(section, path, defSection);
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

    public static class IllegalMobTypeSettingsException extends IllegalStateException {

        public IllegalMobTypeSettingsException(String msg) {
            super(msg);
        }

    }

    private static <T> Setting<T> fromSection(SDCConfiguration section, String path, SDCConfiguration defSection) {
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
    private static <T> T getValue(SDCConfiguration section, String path, T def) {
        if (!section.isSet(path)) {
            return def;
        }
        if (def instanceof Double) {
            return (T) (Double) section.getDouble(path);
        } else if (def instanceof Float) {
            return (T) (Float) (float) section.getDouble(path);
        } else if (def instanceof Integer) {
            return (T) (Integer) section.getInt(path);
        } else if (def instanceof Long) {
            return (T) (Long) section.getLong(path);
        } else if (def instanceof Boolean) {
            return (T) (Boolean) section.getBoolean(path);
        }
        return (T) section.get(path);
    }

}
