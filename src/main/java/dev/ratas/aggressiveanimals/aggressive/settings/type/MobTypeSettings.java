package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.aggressive.ChangeReason;
import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;

public record MobTypeSettings(EntityType entityType, boolean enabled, double speedMultiplier,
        MobAttackSettings attackSettings, MobAcquisationSettings acquisitionSettings, double minAttackHealth,
        MobAgeSettings ageSettings, MobMiscSettings miscSettings, boolean retaliateOnly, boolean overrideTargets,
        double groupAgressionDistance, PlayerStateSettings playerStateSettings, MobWorldSettings worldSettings) {

    private static final double LEAVE_HEALTH_AT = 1.0D;

    public boolean shouldApplyTo(Mob mob, Player target, NPCHookManager npcHooks) {
        if (mob.getType() != entityType) {
            throw new IllegalArgumentException(
                    "Mob is of wrong type (at application time). Expected " + entityType.name() + " and got "
                            + mob.getType());
        }
        if (!enabled) {
            return false;
        }
        if (!worldSettings.isEnabledInWorld(mob.getWorld())) {
            return false;
        }
        if (!miscSettings.shouldBeAggressive(npcHooks, mob, target)) {
            return false;
        }
        return true;
    }

    public boolean shouldAttack(Mob mob, Player target) {
        if (mob.getType() != entityType) {
            throw new IllegalArgumentException(
                    "Mob is of wrong type (at attack time). Expected " + entityType.name() + " and got "
                            + mob.getType());
        }
        if (!enabled) {
            return false;
        }
        if (!attackSettings.canKill() && target.getHealth() <= LEAVE_HEALTH_AT) {
            return false;
        }
        if (!acquisitionSettings.isInRange(mob, target)) {
            return false;
        }
        if (target.getHealth() < minAttackHealth) {
            return false;
        }
        if (!ageSettings.shouldAttack(mob)) {
            return false;
        }
        if (!playerStateSettings.shouldAttack(mob, target)) {
            return false;
        }
        if (!worldSettings.isEnabledInWorld(target.getWorld())) {
            return false;
        }
        return true;
    }

    /**
     * Checks if a mob should be passified. Returns a ChangeReason if that is the
     * case and null otherwise.
     *
     * @param mob the mob in question
     * @return the ChangeReason if mob should be passified, null otherwise
     */
    public ChangeReason shouldBePassified(Mob mob) {
        LivingEntity target = mob.getTarget();
        if (!(target instanceof Player)) {
            return ChangeReason.NO_TARGET;
        }
        Player player = (Player) target;
        if (!acquisitionSettings.isInRange(mob, player)) {
            return ChangeReason.OUT_OF_RANGE;
        }
        return null;
    }

}
