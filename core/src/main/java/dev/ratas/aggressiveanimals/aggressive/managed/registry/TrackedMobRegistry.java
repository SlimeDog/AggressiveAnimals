package dev.ratas.aggressiveanimals.aggressive.managed.registry;

import java.util.Collection;

import org.bukkit.entity.Mob;

import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;

public interface TrackedMobRegistry {

    TrackedMob register(Mob mob, MobTypeSettings settings);

    void unregister(TrackedMob wrapper);

    void unregister(Mob mob, MobTypeSettings settings);

    TrackedMob getTrackedMob(Mob mob);

    /**
     * Clear all data regarding registry
     */
    void clear();

    Collection<TrackedMob> getAllTrackedMobs();

}
