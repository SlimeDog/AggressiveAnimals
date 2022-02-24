package dev.ratas.aggressiveanimals.aggressive.timers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.aggressive.AttackReason;
import dev.ratas.aggressiveanimals.aggressive.MobWrapper;

public class GroupAggressivity implements Runnable {
    private final AggressivityManager aggressivityManager;
    private final Set<MobWrapper> checkableMobs;

    public GroupAggressivity(AggressivityManager aggressivityManager, Collection<MobWrapper> checkableMobs) {
        this.aggressivityManager = aggressivityManager;
        this.checkableMobs = new HashSet<>(checkableMobs);
    }

    public void addTrackableMob(MobWrapper mob) {
        checkableMobs.add(mob);
    }

    @Override
    public void run() {
        for (MobWrapper mob : new HashSet<>(checkableMobs)) {
            if (!mob.isAttacking()) {
                checkableMobs.remove(mob);
                continue;
            }
            checkMob(mob);
        }
    }

    private void checkMob(MobWrapper mob) {
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
            LivingEntity targetEntity = bukkitEntity.getTarget();
            if (!(targetEntity instanceof Player target)) {
                continue;
            }
            aggressivityManager.attemptAttacking((Mob) e, target, AttackReason.GROUP_AGGRESSION);
        }
    }

    public void reload() {
        checkableMobs.clear();
    }

}
