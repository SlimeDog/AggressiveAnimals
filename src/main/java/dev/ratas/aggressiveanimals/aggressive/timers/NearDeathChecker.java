package dev.ratas.aggressiveanimals.aggressive.timers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.AggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;

public class NearDeathChecker implements Runnable {
    private final AggressiveAnimals plugin;
    private final Set<TrackedMob> checkableMobs;

    public NearDeathChecker(AggressiveAnimals plugin, Collection<TrackedMob> checkableMobs) {
        this.plugin = plugin;
        this.checkableMobs = new HashSet<>(checkableMobs);
    }

    public void addTrackableMob(TrackedMob mob) {
        checkableMobs.add(mob);
    }

    @Override
    public void run() {
        for (TrackedMob mob : new HashSet<>(checkableMobs)) {
            double damLimit = mob.getSettings().attackSettings().attackDamageLimit();
            if (damLimit <= 0) {
                continue;
            }
            LivingEntity targetEntity = mob.getBukkitEntity().getTarget();
            if (!(targetEntity instanceof Player)) {
                continue;
            }
            Player target = (Player) targetEntity;
            if (target.getHealth() <= damLimit) {
                plugin.debug("Removing target from " + mob.getBukkitEntity() + " because of target health of "
                        + target.getHealth());
                mob.getBukkitEntity().setTarget(null);
            }

        }
    }

}
