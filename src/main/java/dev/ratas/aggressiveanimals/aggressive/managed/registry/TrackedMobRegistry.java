package dev.ratas.aggressiveanimals.aggressive.managed.registry;

import java.util.Collection;

import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;

public interface TrackedMobRegistry {

    TrackedMob register(Mob mob, MobTypeSettings settings);

    void unregister(TrackedMob wrapper);

    void unregister(Mob mob, MobTypeSettings settings);

    TrackedMob getTrackedMob(Mob mob);

    /**
     * Mark the mob as attacking a specifid target and (potentially) trigger its
     * neighbours to attack as well.
     *
     * @param mob               the tracked mob
     * @param target            the target to attack
     * @param triggerNeighbours whether or not neighbours should be triggered
     */
    void markAttacking(TrackedMob mob, Player target, boolean triggerNeighbours);

    void markNotAttacking(TrackedMob wrapper);

    /**
     * Stop attacking the target (i.e due to too much damage).
     *
     * @param player target being attacked
     */
    void stopAttacking(Player player);

    /**
     * Clear all data regarding registry
     */
    void clear();

    Collection<TrackedMob> getAllTrackedMobs();

    Player getTargetOf(TrackedMob mob);

    void resetTarget(TrackedMob mob);

}
