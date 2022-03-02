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

    void markAttacking(TrackedMob mob, Player target);

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
