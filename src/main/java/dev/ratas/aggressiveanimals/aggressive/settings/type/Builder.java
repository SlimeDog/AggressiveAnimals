package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.HashSet;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

// # mob-type:
// #   enabled: true                      Should revenge attacks by this mob-type be enabled?
// #   speed-multiplier: 1.0              How fast can the mob move? (for example: 1.0 is regular speed, 0.5 is half speed and 2.0 is double speed)
// #   attack-damage: 1                   How much damage will the mob supposed to inflict per attack? (in half-hearts)
// #   attack-unto-death: false           Should the mob kill the player if enough damage is inflicted? If false, the attack will stop at 1 heart
// #   attack-speed: 10                   How often can the mob damage the player? (in ticks)
// #   attack-range: 1                    From how many blocks away can the mob hit the player? (in blocks)
// #   attack-chance: 50                  Chance that the mob will attack the player, per chance-duration? (in percentage)
// #   chance-duration: 100               How often should the attack chance be calculated? (in ticks)
// #   acquisition-range: 12              How close will mobs acquire a player and start an attack?
// #   deacquisition-range: 20            How far away must the player run to stop an attack?
// #   health-percentage: 100             Should the mob attack only if healthy? (in percentage)
// #   age:                               Should adult mobs and/or baby mobs attack the player?
// #     adult: true                      Adult mobs should attack
// #     baby: false                      Baby mobs should attack
// #   ignore-npcs: true                  Ignore NPCs created by Citizens, EliteMobs, InfernalMobs, and Shopkeepers
// #   named-mobs-only: false             Should mobs attack only if they are named?
// #   retaliate-only: true               Should mobs retaliate only if attacked by the player?
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
    private EntityType type;
    private boolean enabled;
    private double speedMultiplier;
    private MobAttackSettings attackSettings;
    private MobAcquisationSettings acquisitionSettings;
    private double healthPercentAtLeast;
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
            type = EntityType.valueOf(tpyeName.toUpperCase());
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
        double chance = section.getDouble("attack-chance", 50.0D);
        long chanceRecalcTicks = section.getLong("chance-duration", 100);
        attackSettings = new MobAttackSettings(damage, canKill, speed, range, chance, chanceRecalcTicks);
    }

    private void loadAcquisitionSettings() {
        double acquisitionRange = section.getDouble("acquisition-range", 12.0D);
        double deacquisitionRange = section.getDouble("deacquisition-range", 20.0D);
        acquisitionSettings = new MobAcquisationSettings(acquisitionRange, deacquisitionRange);
    }

    private void loadHealthPercentAtLeast() {
        healthPercentAtLeast = section.getDouble("health-percentage", 100);
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
        retaliateOnly = section.getBoolean("retaliate-only", true);
    }

    private void loadOverrideTargets() {
        overrideTargets = section.getBoolean("override-targeting", false);
    }

    private void loadGroupAgrewssionDistance() {
        groupAgressionDistance = section.getDouble("group-aggression-distance", 10.0D);
    }

    public void loadPlayerStateSettings() {
        boolean attackStanding = section.getBoolean("standing", true);
        boolean attackSneaking = section.getBoolean("sneaking", true);
        boolean attackWalking = section.getBoolean("walking", true);
        boolean attackSprinting = section.getBoolean("sprinting", true);
        boolean attackLooking = section.getBoolean("looking", true);
        playerStateSettings = new PlayerStateSettings(attackStanding, attackSneaking, attackWalking, attackSprinting,
                attackLooking);
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
        loadHealthPercentAtLeast();
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
                healthPercentAtLeast, ageSettings, miscSettings, retaliateOnly, overrideTargets, groupAgressionDistance,
                playerStateSettings, worldSettings);
    }

    public static class IllegalMobTypeSettingsException extends IllegalStateException {

        public IllegalMobTypeSettingsException(String msg) {
            super(msg);
        }

    }

}