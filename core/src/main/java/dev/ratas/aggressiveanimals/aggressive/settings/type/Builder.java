package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.HashSet;
import java.util.Set;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.slimedogcore.api.config.SDCConfiguration;

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
    private Setting<Boolean> overrideTargets;
    private Setting<Double> groupAgressionDistance;
    private PlayerStateSettings playerStateSettings;
    private MobWorldSettings worldSettings;
    private Setting<Boolean> alwaysAggressive;

    public Builder(SDCConfiguration section) {
        this.section = section;
    }

    private void loadType() {
        String tpyeName = section.getName();
        try {
            type = new Setting<>("", MobType.from(tpyeName.toLowerCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalMobTypeSettingsException("Unknown entity type " + tpyeName + " (" + e.getMessage() + ")");
        }
    }

    private void loadEnabled() {
        String path = "enabled";
        enabled = new Setting<>(path, section.getBoolean(path, false));
    }

    private void loadAlwaysAggressive() {
        String path = "always-aggressive";
        alwaysAggressive = new Setting<>(path, section.getBoolean(path, false));
    }

    private void loadSpeed() {
        String path = "speed-multiplier";
        speedMultiplier = new Setting<>(path, section.getDouble(path, 1.0));
    }

    private void loadAttackSettings() {
        String path = "attack-damage";
        Setting<Double> damage = new Setting<>(path, section.getDouble(path, 1.0D));
        path = "attack-damage-limit";
        Setting<Double> attackDamageLimit = new Setting<>(path, section.getDouble(path, 2.0));
        path = "attack-speed";
        Setting<Double> speed = new Setting<>(path, section.getDouble(path, 20));
        path = "attack-leap-height";
        Setting<Float> attackLeapHeight = new Setting<>(path, (float) section.getDouble(path, 0.0F));
        attackSettings = new MobAttackSettings(damage, attackDamageLimit, speed, attackLeapHeight);
    }

    private void loadAcquisitionSettings() {
        String path = "acquisition-range";
        Setting<Double> acquisitionRange = new Setting<>(path, section.getDouble(path, 16.0D));
        path = "deacquisition-range";
        Setting<Double> deacquisitionRange = new Setting<>(path, section.getDouble(path, 20.0D));
        acquisitionSettings = new MobAcquisitionSettings(acquisitionRange, deacquisitionRange);
    }

    private void loatAttackerHealthThreshold() {
        String path = "attacker-health-threshold";
        attackerHealthThreshold = new Setting<>(path, section.getDouble(path, 25));
    }

    private void loadMobAgeSettings() {
        String path = "age.adult";
        Setting<Boolean> attackAsAdult = new Setting<>(path, section.getBoolean(path, true));
        path = "age.baby";
        Setting<Boolean> attackAsBaby = new Setting<>(path, section.getBoolean(path, false));
        ageSettings = new MobAgeSettings(attackAsAdult, attackAsBaby);
    }

    private void loadMiscSettings() {
        String path = "include-npcs";
        Setting<Boolean> includeNpcs = new Setting<>(path, section.getBoolean(path, false));
        path = "include-named-mobs";
        Setting<Boolean> includeNamedMobs = new Setting<>(path, section.getBoolean(path, false));
        path = "include-tamed-mobs";
        Setting<Boolean> includeTamed = new Setting<>(path, section.getBoolean(path, true));
        miscSettings = new MobMiscSettings(includeNpcs, includeNamedMobs, includeTamed);
    }

    private void loadOverrideTargets() {
        String path = "override-targeting";
        overrideTargets = new Setting<>(path, section.getBoolean(path, false));
    }

    private void loadGroupAgrewssionDistance() {
        String path = "group-aggression-distance";
        groupAgressionDistance = new Setting<>(path, section.getDouble(path, 20.0D));
    }

    public void loadPlayerStateSettings() {
        String path = "player-movement.standing";
        Setting<Boolean> attackStanding = new Setting<>(path, section.getBoolean(path, true));
        path = "player-movement.sneaking";
        Setting<Boolean> attackSneaking = new Setting<>(path, section.getBoolean(path, true));
        path = "player-movement.walking";
        Setting<Boolean> attackWalking = new Setting<>(path, section.getBoolean(path, true));
        path = "player-movement.sprinting";
        Setting<Boolean> attackSprinting = new Setting<>(path, section.getBoolean(path, true));
        path = "player-movement.looking";
        Setting<Boolean> attackLooking = new Setting<>(path, section.getBoolean(path, true));
        path = "player-movement.sleeping";
        Setting<Boolean> attackSleeping = new Setting<>(path, section.getBoolean(path, true));
        path = "player-movement.guiding";
        Setting<Boolean> attackGliding = new Setting<>(path, section.getBoolean(path, true));
        playerStateSettings = new PlayerStateSettings(attackStanding, attackSneaking, attackWalking, attackSprinting,
                attackLooking, attackSleeping, attackGliding);
    }

    private void loadWorldSettings() {
        String path = "enabled-worlds";
        Setting<Set<String>> enabled = new Setting<>(path, new HashSet<>(section.getStringList(path)));
        path = "disabled-worlds";
        Setting<Set<String>> disabled = new Setting<>(path, new HashSet<>(section.getStringList(path)));
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
        loadOverrideTargets();
        loadGroupAgrewssionDistance();
        loadPlayerStateSettings();
        loadPlayerStateSettings();
        loadWorldSettings();
        loadPlayerStateSettings();
        loadWorldSettings();
        if ((!type.value().isTameable() && type.value() != MobType.fox && type.value() != MobType.ocelot)
                && !miscSettings.includeTamed().value()) {
            throw new IllegalMobTypeSettingsException(
                    "Cannot include tameable of " + type.value().name() + " since the mobtype is not tameable");
        }
        return new MobTypeSettings(type, enabled, speedMultiplier, attackSettings, acquisitionSettings,
                attackerHealthThreshold, ageSettings, miscSettings, alwaysAggressive, overrideTargets,
                groupAgressionDistance,
                playerStateSettings, worldSettings);
    }

    public static class IllegalMobTypeSettingsException extends IllegalStateException {

        public IllegalMobTypeSettingsException(String msg) {
            super(msg);
        }

    }

}
