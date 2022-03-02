package dev.ratas.aggressiveanimals.aggressive.timers;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.reasons.AttackReason;

public class GroupAggressivity implements Runnable {
    private final AggressivityManager aggressivityManager;

    public GroupAggressivity(AggressivityManager aggressivityManager) {
        this.aggressivityManager = aggressivityManager;
    }

    @Override
    public void run() {
        for (TrackedMob mob : new HashSet<>(aggressivityManager.getAllTrackedMobs())) {
            if (!mob.isAttacking()) {
                continue;
            }
            checkMob(mob);
        }
    }

    private void checkMob(TrackedMob mob) {
        double dist = mob.getSettings().groupAgressionDistance();
        if (dist <= 0) {
            return;
        }
        double dist2 = dist * dist;
        Mob bukkitEntity = mob.getBukkitEntity();
        EntityType entityType = bukkitEntity.getType();
        Location entityLoc = bukkitEntity.getLocation();
        for (Entity e : entityLoc.getWorld().getNearbyEntities(entityLoc, dist, dist, dist,
                e -> e.getType() == entityType)) {
            if (e.getLocation().distanceSquared(entityLoc) < dist2) {
                continue; // in cube but not in sphere
            }
            Player target = aggressivityManager.getRegisteredTarget(mob);
            if (target == null) {
                continue;
            }
            aggressivityManager.attemptAttacking((Mob) e, target, AttackReason.GROUP_AGGRESSION);
        }
    }

}
