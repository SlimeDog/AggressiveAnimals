package dev.ratas.aggressiveanimals.aggressive.timers;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.reasons.AttackReason;

public class GroupAggressivity extends AbstractQueuedRunnable<TrackedMob> {
    private final AggressivityManager aggressivityManager;

    public GroupAggressivity(AggressivityManager aggressivityManager, long processAtOnce) {
        super(processAtOnce, () -> aggressivityManager.getAllTrackedMobs());
        this.aggressivityManager = aggressivityManager;
    }

    @Override
    public void process(TrackedMob mob) {
        if (!mob.hasAttackingGoals()) {
            return;
        }
        checkMob(mob);
    }

    public void checkMob(TrackedMob mob) {
        double dist = mob.getSettings().groupAgressionDistance().value();
        if (dist <= 0) {
            return;
        }
        Player target = mob.getTarget();
        if (target == null) {
            return;
        }
        final double maxDist2 = dist * dist;
        Mob bukkitEntity = mob.getBukkitEntity();
        EntityType entityType = bukkitEntity.getType();
        Location entityLoc = bukkitEntity.getLocation();
        for (Entity e : entityLoc.getWorld().getNearbyEntities(entityLoc, dist, dist, dist,
                e -> e.getType() == entityType)) {
            double curDist2 = e.getLocation().distanceSquared(entityLoc);
            if (curDist2 > maxDist2) {
                continue; // in cube but not in sphere
            }
            aggressivityManager.attemptAttacking((Mob) e, target, AttackReason.GROUP_AGGRESSION);
        }
    }

}
