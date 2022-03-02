package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.HashSet;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.slimedogcore.api.config.SDCConfiguration;

// # mob-type:
// #   enabled: true                      If true, this mob-type should engage in attacks against players
// #   always-aggressive: false           If true, mobs should attack only in retaliation for player attack
// #   speed-multiplier: 1.0              Multiple of vanilla movement speed (examples: 1.0 is regular speed, 0.5 is half speed, 2.0 is double speed)
// #   attack-damage: 1.0                 Damage inflicted on the target on each attack (in half-hearts)
// #   attack-damage-limit: 2             The attacker will leave the target alive with the specified amount of health (in half hearts)
// #   attack-speed: 20                   Frequency of attack, same as vanilla except for zombies (in ticks)
// #   attack-range: 1                    Distance at which the mob can damage the player (in blocks)
// #   attack-leap-height: 0.0            Height attacker may leap when attacking (in blocks; 0.0 or below means disabled)
// #   acquisition-range: 16              Distance at which the attaker can detect a target
// #   deacquisition-range: 20            Distance at which the victim can escape attack
// #   attacker-health-threshold: 5       Attack should stop when the health of the attacker falls below the threshold (in half-hearts)
// #   age:                               Attacks may be waged by adults and/or babies
// #     adult: true                      If true, adult mobs should attack
// #     baby: false                      If true, baby mobs should attack
// #   ignore-npcs: true                  If true, ignore NPCs created by NPC managers, such as Citizens, EliteMobs, InfernalMobs, and Shopkeepers
// #   named-mobs-only: false             If true, only named mobs may attack
// #   override-targeting: false          If true, remove vanilla targeting behavior and use only attack-conditions; useful for hostile mob-types
// #   group-aggression-range: 20         If other mobs of the same type are within range of the attacker, they should join the attack (in blocks)
// #   player-movement:                   Mob should attack if the player is
// #     standing: true
// #     sneaking: true
// #     walking: true
// #     sprinting: true
// #     looking: true                    Like locking eyes with an Enderman
// #     sleeping: true
// #     gliding: true
// #   enabled-worlds: []                 Worlds in which attacks by this mob-type is enabled; if empty, it is enabled in all worlds
// #   disabled-worlds:                   Worlds in which attacks by this mob-type is disabled; must be set explicitly; takes precedence over enabled_worlds
// #     - "world_example"

public class Builder {
    private final SDCConfiguration section;
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
        double range = section.getDouble("attack-range", 1.0D);
        float attackLeapHeight = (float) section.getDouble("attack-leap-height", 0.0F);
        attackSettings = new MobAttackSettings(damage, attackDamageLimit, speed, range, attackLeapHeight);
    }

    private void loadAcquisitionSettings() {
        double acquisitionRange = section.getDouble("acquisition-range", 16.0D);
        double deacquisitionRange = section.getDouble("deacquisition-range", 20.0D);
        acquisitionSettings = new MobAcquisationSettings(acquisitionRange, deacquisitionRange);
    }

    private void loadMinAttackHealth() {
        minAttackHealth = section.getDouble("attacker-health-threshold", 5);
    }

    private void loadMobAgeSettings() {
        boolean attackAsAdult = section.getBoolean("age.adult", true);
        boolean attackAsBaby = section.getBoolean("age.baby", false);
        ageSettings = new MobAgeSettings(attackAsAdult, attackAsBaby);
    }

    private void loadMiscSettings() {
        boolean ignoreNpcs = section.getBoolean("ignore-npcs", true);
        boolean targetAsNamedOnly = section.getBoolean("named-mobs-only", false);
        miscSettings = new MobMiscSettings(ignoreNpcs, targetAsNamedOnly);
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
        loadMinAttackHealth();
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
        return new MobTypeSettings(type, enabled, speedMultiplier, attackSettings, acquisitionSettings,
                minAttackHealth, ageSettings, miscSettings, alwaysAggressive, overrideTargets, groupAgressionDistance,
                playerStateSettings, worldSettings);
    }

    public static class IllegalMobTypeSettingsException extends IllegalStateException {

        public IllegalMobTypeSettingsException(String msg) {
            super(msg);
        }

    }

}
