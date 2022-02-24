package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.HashSet;

import org.bukkit.configuration.ConfigurationSection;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;

// # mob-type:
// #   enabled: true                      Should revenge attacks by this mob-type be enabled?
// #   speed-multiplier: 1.0              How fast can the mob move? (for example: 1.0 is regular speed, 0.5 is half speed and 2.0 is double speed)
// #   attack-damage: 1                   How much damage will the mob inflict per attack? (in half-hearts)
// #   attack-unto-death: false           Should the mob kill the player if enough damage is inflicted? If false, the attack will stop at 1 heart
// #   attack-speed: 10                   How often can the mob damage the player? (in ticks)
// #   attack-range: 1                    From how many blocks away can the mob hit the player? (in blocks)
// #   acquisition-range: 12              How close will mobs acquire a player and start an attack?
// #   deacquisition-range: 20            How far away must the player run to stop an attack?
// #   attacker-health-threshold: 5       The attack should stop when the mob health falls below the threshold (in half-hearts)
// #   age:                               Should adult mobs and/or baby mobs attack the player?
// #     adult: true                      Adult mobs should attack
// #     baby: false                      Baby mobs should attack
// #   ignore-npcs: true                  Ignore NPCs created by Citizens, EliteMobs, InfernalMobs, and Shopkeepers
// #   named-mobs-only: false             Should mobs attack only if they are named?
// #   retribution-only: true             Should mobs retaliate only if attacked by the player?
// #   override-targeting: false          If true, remove vanilla targeting behavior and only use attack-conditions
// #   group-aggression-distance: 10      If other mobs of the same type are close enough, they should join the attack (in blocks)
// #   player-movement:                   Mob should attack only if the player is
// #     standing: true
// #     sneaking: true
// #     walking: true
// #     sprinting: true
// #     looking: true                    Like Enderman
// #   enabled-worlds: []                 Worlds in which this mob-type is enabled; if empty, it is enabled in all worlds
// #   disabled-worlds:                   Worlds in which this mob-type is disabled; must be set explicitly
// #     - "world_example"

public class Builder {
    private final ConfigurationSection section;
    private MobType type;
    private boolean enabled;
    private double speedMultiplier;
    private MobAttackSettings attackSettings;
    private MobAcquisationSettings acquisitionSettings;
    private double minAttackHealth;
    private MobAgeSettings ageSettings;
    private MobMiscSettings miscSettings;
    private boolean overrideTargets;
    private double groupAgressionDistance;
    private PlayerStateSettings playerStateSettings;
    private MobWorldSettings worldSettings;
    private boolean retaliateOnly;

    public Builder(ConfigurationSection section) {
        this.section = section;
    }

    private void loadType() {
        String tpyeName = section.getName();
        try {
            type = MobType.from(tpyeName.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalMobTypeSettingsException("Unknown entity type " + tpyeName);
        }
    }

    private void loadEnabled() {
        enabled = section.getBoolean("enabled", false);
    }

    private void loadSpeed() {
        speedMultiplier = section.getDouble("speed-multiplier", 1.0);
    }

    private void loadAttackSettings() {
        double damage = section.getDouble("attack-damage", 1.0D);
        boolean canKill = section.getBoolean("attack-unto-death", false);
        double speed = section.getDouble("attack-speed", 10);
        double range = section.getDouble("attack-range", 1.0D);
        float attackLeapHeight = (float) section.getDouble("attack-leap-height", 0.0F);
        attackSettings = new MobAttackSettings(damage, canKill, speed, range, attackLeapHeight);
    }

    private void loadAcquisitionSettings() {
        double acquisitionRange = section.getDouble("acquisition-range", 12.0D);
        double deacquisitionRange = section.getDouble("deacquisition-range", 20.0D);
        acquisitionSettings = new MobAcquisationSettings(acquisitionRange, deacquisitionRange);
    }

    private void loadMinAttackHealth() {
        minAttackHealth = section.getDouble("attacker-health-threshold", 5);
    }

    private void loadMobAgeSettings() {
        boolean attackAsAdult = section.getBoolean("age.adult");
        boolean attackAsBaby = section.getBoolean("age.baby");
        ageSettings = new MobAgeSettings(attackAsAdult, attackAsBaby);
    }

    private void loadMiscSettings() {
        boolean ignoreNpcs = section.getBoolean("ignore-npcs", true);
        boolean targetAsNamedOnly = section.getBoolean("named-mobs-only", false);
        miscSettings = new MobMiscSettings(ignoreNpcs, targetAsNamedOnly);
    }

    private void loadRetalitateOnly() {
        retaliateOnly = section.getBoolean("retribution-only", true);
    }

    private void loadOverrideTargets() {
        overrideTargets = section.getBoolean("override-targeting", false);
    }

    private void loadGroupAgrewssionDistance() {
        groupAgressionDistance = section.getDouble("group-aggression-distance", 10.0D);
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
        loadMinAttackHealth();
        loadMobAgeSettings();
        loadMiscSettings();
        loadRetalitateOnly();
        loadOverrideTargets();
        loadGroupAgrewssionDistance();
        loadPlayerStateSettings();
        loadPlayerStateSettings();
        loadWorldSettings();
        loadPlayerStateSettings();
        loadWorldSettings();
        return new MobTypeSettings(type, enabled, speedMultiplier, attackSettings, acquisitionSettings,
                minAttackHealth, ageSettings, miscSettings, retaliateOnly, overrideTargets, groupAgressionDistance,
                playerStateSettings, worldSettings);
    }

    public static class IllegalMobTypeSettingsException extends IllegalStateException {

        public IllegalMobTypeSettingsException(String msg) {
            super(msg);
        }

    }

}
