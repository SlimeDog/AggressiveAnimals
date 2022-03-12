package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.MemoryConfiguration;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.slimedogcore.api.config.SDCConfiguration;
import dev.ratas.slimedogcore.impl.config.ConfigurationWrapper;

public class Builder {
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
            type = new Setting<>("", MobType.from(typeName.toLowerCase()), MobType.__INVALID);
        } catch (IllegalArgumentException e) {
            throw new IllegalMobTypeSettingsException("Unknown entity type " + typeName + " (" + e.getMessage() + ")");
        }
    }

    private void loadEnabled() {
        String path = "enabled";
        boolean def = false;
        enabled = new Setting<>(path, section.getBoolean(path, def), def);
    }

    private void loadAlwaysAggressive() {
        String path = "always-aggressive";
        boolean def = false;
        alwaysAggressive = new Setting<>(path, section.getBoolean(path, def), def);
    }

    private void loadSpeed() {
        String path = "speed-multiplier";
        double def = 1.0D;
        speedMultiplier = new Setting<>(path, section.getDouble(path, def), def);
    }

    private void loadAttackSettings() {
        String path = "attack-damage";
        double def = 1.0D;
        Setting<Double> damage = new Setting<>(path, section.getDouble(path, def), def);
        path = "attack-damage-limit";
        def = 2.0D;
        Setting<Double> attackDamageLimit = new Setting<>(path, section.getDouble(path, def), def);
        path = "attack-speed";
        def = 20.0D;
        Setting<Double> speed = new Setting<>(path, section.getDouble(path, def), def);
        path = "attack-leap-height";
        float deff = 0.0F;
        Setting<Float> attackLeapHeight = new Setting<>(path, (float) section.getDouble(path, deff), deff);
        attackSettings = new MobAttackSettings(damage, attackDamageLimit, speed, attackLeapHeight);
    }

    private void loadAcquisitionSettings() {
        String path = "acquisition-range";
        double def = 16.0D;
        Setting<Double> acquisitionRange = new Setting<>(path, section.getDouble(path, def), def);
        path = "deacquisition-range";
        def = 20.0D;
        Setting<Double> deacquisitionRange = new Setting<>(path, section.getDouble(path, def), def);
        acquisitionSettings = new MobAcquisitionSettings(acquisitionRange, deacquisitionRange);
    }

    private void loatAttackerHealthThreshold() {
        String path = "attacker-health-threshold";
        double def = 25.0D;
        attackerHealthThreshold = new Setting<>(path, section.getDouble(path, def), def);
    }

    private void loadMobAgeSettings() {
        String path = "age.adult";
        boolean def = true;
        Setting<Boolean> attackAsAdult = new Setting<>(path, section.getBoolean(path, def), def);
        path = "age.baby";
        def = false;
        Setting<Boolean> attackAsBaby = new Setting<>(path, section.getBoolean(path, def), def);
        ageSettings = new MobAgeSettings(attackAsAdult, attackAsBaby);
    }

    private void loadMiscSettings() {
        String path = "include-npcs";
        boolean def = false;
        Setting<Boolean> includeNpcs = new Setting<>(path, section.getBoolean(path, def), def);
        path = "include-named-mobs";
        def = false;
        Setting<Boolean> includeNamedMobs = new Setting<>(path, section.getBoolean(path, def), def);
        path = "include-tamed-mobs";
        def = true;
        Setting<Boolean> includeTamed = new Setting<>(path, section.getBoolean(path, def), def);
        miscSettings = new MobMiscSettings(includeNpcs, includeNamedMobs, includeTamed);
    }

    // private void loadOverrideTargets() {
    //     String path = "override-targeting";
    //     boolean def = false;
    //     overrideTargets = new Setting<>(path, section.getBoolean(path, def), def);
    // }

    private void loadGroupAgrewssionDistance() {
        String path = "group-aggression-distance";
        double def = 20.0D;
        groupAgressionDistance = new Setting<>(path, section.getDouble(path, def), def);
    }

    public void loadPlayerStateSettings() {
        String path = "player-movement.standing";
        boolean def = true; // same default for all for now
        Setting<Boolean> attackStanding = new Setting<>(path, section.getBoolean(path, def), def);
        path = "player-movement.sneaking";
        Setting<Boolean> attackSneaking = new Setting<>(path, section.getBoolean(path, def), def);
        path = "player-movement.walking";
        Setting<Boolean> attackWalking = new Setting<>(path, section.getBoolean(path, def), def);
        path = "player-movement.sprinting";
        Setting<Boolean> attackSprinting = new Setting<>(path, section.getBoolean(path, def), def);
        path = "player-movement.looking";
        Setting<Boolean> attackLooking = new Setting<>(path, section.getBoolean(path, def), def);
        path = "player-movement.sleeping";
        Setting<Boolean> attackSleeping = new Setting<>(path, section.getBoolean(path, def), def);
        path = "player-movement.guiding";
        Setting<Boolean> attackGliding = new Setting<>(path, section.getBoolean(path, def), def);
        playerStateSettings = new PlayerStateSettings(attackStanding, attackSneaking, attackWalking, attackSprinting,
                attackLooking, attackSleeping, attackGliding);
    }

    private void loadWorldSettings() {
        String path = "enabled-worlds";
        Set<String> def = new HashSet<>(); // empty for both for now
        Setting<Set<String>> enabled = new Setting<>(path, new HashSet<>(section.getStringList(path)), def);
        path = "disabled-worlds";
        Setting<Set<String>> disabled = new Setting<>(path, new HashSet<>(section.getStringList(path)), def);
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
        if (type.value() != MobType.__INVALID && // let the invalid type be for now
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

    public static MobTypeSettings getDefaultSettings() {
        SDCConfiguration emptySection = new ConfigurationWrapper(new MemoryConfiguration().createSection("__INVALID"));
        return new Builder(emptySection).build();
    }

    public static class IllegalMobTypeSettingsException extends IllegalStateException {

        public IllegalMobTypeSettingsException(String msg) {
            super(msg);
        }

    }

}
