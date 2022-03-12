package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.reasons.ChangeReason;
import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;

public record MobTypeSettings(Setting<MobType> entityType, Setting<Boolean> enabled, Setting<Double> speedMultiplier,
        MobAttackSettings attackSettings, MobAcquisitionSettings acquisitionSettings,
        Setting<Double> attackerHealthThreshold, MobAgeSettings ageSettings, MobMiscSettings miscSettings,
        Setting<Boolean> alwaysAggressive, // Setting<Boolean> overrideTargets, // may be (re)implemented later
        Setting<Double> groupAgressionDistance,
        PlayerStateSettings playerStateSettings, MobWorldSettings worldSettings) {

    public boolean shouldAttack(Mob mob, Player target, NPCHookManager npcHooks) {
        if (mob.getType() != entityType.value().getBukkitType()) {
            throw new IllegalArgumentException(
                    "Mob is of wrong type (at application time). Expected " + entityType.value().name() + " and got "
                            + mob.getType());
        }
        if (!enabled.value()) {
            return false;
        }
        if (!worldSettings.isEnabledInWorld(mob.getWorld())) {
            return false;
        }
        if (!miscSettings.shouldBeAggressive(npcHooks, mob, target)) {
            return false;
        }
        double curRelHealth = mob.getHealth() / mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        if (curRelHealth < attackerHealthThreshold.value() / 100) { // percentage to relative value
            return false;
        }
        if (target != null) {
            if (target.getHealth() <= attackSettings.attackDamageLimit().value()) {
                return false;
            }
            if (!acquisitionSettings.isInRange(mob, target)) {
                return false;
            }
            if (!playerStateSettings.shouldAttack(mob, target)) {
                return false;
            }
            if (target.getGameMode() != GameMode.SURVIVAL) {
                return false;
            }
        }
        if (!worldSettings.isEnabledInWorld(mob.getWorld())) {
            return false;
        }
        if (!ageSettings.shouldAttack(mob)) {
            return false;
        }
        return true;
    }

    /**
     * Checks if a mob should stop attacking. Returns a ChangeReason if that is the
     * case and null otherwise.
     *
     * @param wrapper the mob wrapper in question
     * @return the ChangeReason if mob should be pacified, null otherwise
     */
    public ChangeReason shouldStopAttacking(TrackedMob wrapper) {
        if (alwaysAggressive.value()) {
            return null;
        }
        Mob mob = wrapper.getBukkitEntity();
        LivingEntity target = wrapper.getTarget();
        if (!(target instanceof Player)) {
            return ChangeReason.NO_TARGET; // nothing to stop attacking?
        }
        double curRelHealth = mob.getHealth() / mob.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        if (curRelHealth < attackerHealthThreshold.value() / 100) { // percentage to relative value
            return ChangeReason.MOB_TOO_DAMAGED;
        }
        Player player = (Player) target;
        if (!acquisitionSettings.isInRange(mob, player)) {
            return ChangeReason.OUT_OF_RANGE;
        }
        if (player.getGameMode() != GameMode.SURVIVAL) {
            return ChangeReason.WRONG_GAMEMODE;
        }
        return null;
    }

    /**
     * Checks if the mob type settings are applicable in the location / world
     *
     * @param location target location
     * @return true if applicable, false otherwise
     */
    public boolean isApplicableAt(Location location) {
        return worldSettings.isEnabledInWorld(location.getWorld());
    }

    /**
     * Check if settings other than enabled and world settings are the same.
     *
     * @param other the other settings instance to check against
     * @return if all settings other than enabled and world settings are the same
     */
    public boolean hasSimilarSettings(MobTypeSettings other) {
        // need to cast to double because otherwise they are wrappers and NOT the same
        // instance of the wrapper
        if ((double) speedMultiplier.value() != (double) other.speedMultiplier.value()) {
            return false;
        }
        if (!attackSettings.equals(other.attackSettings)) {
            return false;
        }
        if (!acquisitionSettings.equals(other.acquisitionSettings)) {
            return false;
        }
        if ((double) attackerHealthThreshold.value() != (double) other.attackerHealthThreshold.value()) {
            return false;
        }
        if (!ageSettings.equals(other.ageSettings)) {
            return false;
        }
        if (!miscSettings.equals(other.miscSettings)) {
            return false;
        }
        if ((boolean) alwaysAggressive.value() != (boolean) other.alwaysAggressive.value()) {
            return false;
        }
        // if ((boolean) overrideTargets.value() != (boolean)
        // other.overrideTargets.value()) {
        // return false;
        // }
        if ((double) groupAgressionDistance.value() != (double) other.groupAgressionDistance.value()) {
            return false;
        }
        if (!playerStateSettings.equals(other.playerStateSettings)) {
            return false;
        }
        return true;
    }

    public int getSettingSimilarities(MobTypeSettings other) {
        int similarities = 0;
        // need to cast to double because otherwise they are wrappers and NOT the same
        // instance of the wrapper
        if ((double) speedMultiplier.value() == (double) other.speedMultiplier.value()) {
            similarities++;
        }
        if (attackSettings.equals(other.attackSettings)) {
            similarities++;
        }
        if (acquisitionSettings.equals(other.acquisitionSettings)) {
            similarities++;
        }
        if ((double) attackerHealthThreshold.value() == (double) other.attackerHealthThreshold.value()) {
            similarities++;
        }
        if (ageSettings.equals(other.ageSettings)) {
            similarities++;
        }
        if (miscSettings.equals(other.miscSettings)) {
            similarities++;
        }
        if ((boolean) alwaysAggressive.value() == (boolean) other.alwaysAggressive.value()) {
            similarities++;
        }
        // if ((boolean) overrideTargets.value() == (boolean)
        // other.overrideTargets.value()) {
        // similarities++;
        // }
        if ((double) groupAgressionDistance.value() == (double) other.groupAgressionDistance.value()) {
            similarities++;
        }
        if (playerStateSettings.equals(other.playerStateSettings)) {
            similarities++;
        }
        return similarities;
    }

    public List<Setting<?>> getAllSettings() {
        List<Setting<?>> settings = new ArrayList<>();
        // settings.add(entityType); // ignore for now
        settings.add(enabled);
        settings.add(alwaysAggressive);
        settings.add(speedMultiplier);
        settings.add(attackSettings.damage());
        settings.add(attackSettings.attackDamageLimit());
        settings.add(attackSettings.speed());
        settings.add(attackSettings.attackLeapHeight());
        settings.add(acquisitionSettings.acquisitionRange());
        settings.add(acquisitionSettings.deacquisitionRange());
        settings.add(attackerHealthThreshold);
        settings.add(ageSettings.attackAsAdult());
        settings.add(ageSettings.attackAsBaby());
        settings.add(miscSettings.includeNpcs());
        settings.add(miscSettings.includeNamedMobs());
        if (entityType.value().isTameable()) {
            settings.add(miscSettings.includeTamed());
        }
        // settings.add(overrideTargets);
        settings.add(groupAgressionDistance);
        settings.add(playerStateSettings.attackStanding());
        settings.add(playerStateSettings.attackSneaking());
        settings.add(playerStateSettings.attackWalking());
        settings.add(playerStateSettings.attackSprinting());
        settings.add(playerStateSettings.attackLooking());
        settings.add(playerStateSettings.attackSleeping());
        settings.add(playerStateSettings.attackGliding());
        settings.add(worldSettings.enabledWorlds());
        settings.add(worldSettings.disabledWorlds());
        return settings;
    }

}
