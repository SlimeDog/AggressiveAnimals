package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.HashSet;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.slimedogcore.api.config.SDCConfiguration;

public class Builder {
    private final SDCConfiguration section;
    private MobType type;
    private boolean enabled;
    private double speedMultiplier;
    private MobAttackSettings attackSettings;
    private MobAcquisitionSettings acquisitionSettings;
    private double attackerHealthThreshold;
    private MobAgeSettings ageSettings;
    private MobMiscSettings miscSettings;
    private boolean overrideTargets;
    private double groupAgressionDistance;
    private PlayerStateSettings playerStateSettings;
    private MobWorldSettings worldSettings;
    private boolean alwaysAggressive;

    public Builder(SDCConfiguration section) {
        this.section = section;
    }

    private void loadType() {
        String tpyeName = section.getName();
        try {
            type = MobType.from(tpyeName.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalMobTypeSettingsException("Unknown entity type " + tpyeName + " (" + e.getMessage() + ")");
        }
    }

    private void loadEnabled() {
        enabled = section.getBoolean("enabled", false);
    }

    private void loadAlwaysAggressive() {
        alwaysAggressive = section.getBoolean("always-aggressive", false);
    }

    private void loadSpeed() {
        speedMultiplier = section.getDouble("speed-multiplier", 1.0);
    }

    private void loadAttackSettings() {
        double damage = section.getDouble("attack-damage", 1.0D);
        double attackDamageLimit = section.getDouble("attack-damage-limit", 2.0);
        double speed = section.getDouble("attack-speed", 20);
        float attackLeapHeight = (float) section.getDouble("attack-leap-height", 0.0F);
        attackSettings = new MobAttackSettings(damage, attackDamageLimit, speed, attackLeapHeight);
    }

    private void loadAcquisitionSettings() {
        double acquisitionRange = section.getDouble("acquisition-range", 16.0D);
        double deacquisitionRange = section.getDouble("deacquisition-range", 20.0D);
        acquisitionSettings = new MobAcquisitionSettings(acquisitionRange, deacquisitionRange);
    }

    private void loatAttackerHealthThreshold() {
        attackerHealthThreshold = section.getDouble("attacker-health-threshold", 25);
    }

    private void loadMobAgeSettings() {
        boolean attackAsAdult = section.getBoolean("age.adult", true);
        boolean attackAsBaby = section.getBoolean("age.baby", false);
        ageSettings = new MobAgeSettings(attackAsAdult, attackAsBaby);
    }

    private void loadMiscSettings() {
        boolean includeNpcs = section.getBoolean("include-npcs", false);
        boolean includeNamedMobs = section.getBoolean("include-named-mobs", false);
        boolean includeTamed = section.getBoolean("include-tamed-mobs", false);
        miscSettings = new MobMiscSettings(includeNpcs, includeNamedMobs, includeTamed);
    }

    private void loadOverrideTargets() {
        overrideTargets = section.getBoolean("override-targeting", false);
    }

    private void loadGroupAgrewssionDistance() {
        groupAgressionDistance = section.getDouble("group-aggression-distance", 20.0D);
    }

    public void loadPlayerStateSettings() {
        boolean attackStanding = section.getBoolean("player-movement.standing", true);
        boolean attackSneaking = section.getBoolean("player-movement.sneaking", true);
        boolean attackWalking = section.getBoolean("player-movement.walking", true);
        boolean attackSprinting = section.getBoolean("player-movement.sprinting", true);
        boolean attackLooking = section.getBoolean("player-movement.looking", true);
        boolean attackSleeping = section.getBoolean("player-movement.sleeping", true);
        boolean attackGliding = section.getBoolean("player-movement.guiding", true);
        playerStateSettings = new PlayerStateSettings(attackStanding, attackSneaking, attackWalking, attackSprinting,
                attackLooking, attackSleeping, attackGliding);
    }

    private void loadWorldSettings() {
        worldSettings = new MobWorldSettings(new HashSet<>(section.getStringList("enabled-worlds")), new HashSet<>(
                section.getStringList("disabled-worlds")));
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
        if ((!type.isTameable() && type != MobType.fox) && miscSettings.includeTamed()) {
            throw new IllegalMobTypeSettingsException(
                    "Cannot include tameable of " + type.name() + " since the mobtype is not tameable");
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
