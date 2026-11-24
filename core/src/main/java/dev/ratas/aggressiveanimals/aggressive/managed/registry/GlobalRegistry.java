package dev.ratas.aggressiveanimals.aggressive.managed.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Mob;

import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.aggressive.timers.GroupAggressivity;

/**
 * Per-world registry of tracked mobs.
 * <p>
 * Keyed by world UID rather than {@link World} so that an entry can never pin
 * an unloaded world (and its chunk data) in memory. Only {@link #register}
 * creates an entry; lookups and unregistration never do, so a stale mob from
 * an unloaded world cannot re-create the entry after {@link #removeWorld}.
 */
public class GlobalRegistry implements TrackedMobRegistry {
    private final Map<UUID, TrackedMobRegistry> perWorldManager = new HashMap<>();
    private final GroupAggressivity groupAggro;

    public GlobalRegistry(GroupAggressivity groupAggro) {
        this.groupAggro = groupAggro;
    }

    private TrackedMobRegistry getOrCreateWorldManager(World world) {
        return perWorldManager.computeIfAbsent(world.getUID(), uid -> new WorldRegistry(groupAggro));
    }

    /**
     * Get the registry for a world without creating one.
     *
     * @param world the world
     * @return the registry for the world, or null if nothing is tracked there
     */
    public TrackedMobRegistry getWorldManager(World world) {
        return perWorldManager.get(world.getUID());
    }

    /**
     * Drop all data for a world (e.g. when it unloads).
     *
     * @param world the world
     * @return the mobs that were tracked in that world (a detached copy)
     */
    public Collection<TrackedMob> removeWorld(World world) {
        TrackedMobRegistry removed = perWorldManager.remove(world.getUID());
        if (removed == null) {
            return Collections.emptyList();
        }
        List<TrackedMob> mobs = new ArrayList<>(removed.getAllTrackedMobs());
        removed.clear();
        return mobs;
    }

    @Override
    public TrackedMob register(Mob mob, MobTypeSettings settings) {
        return getOrCreateWorldManager(mob.getWorld()).register(mob, settings);
    }

    @Override
    public TrackedMob getTrackedMob(Mob mob) {
        TrackedMobRegistry worldManager = getWorldManager(mob.getWorld());
        return worldManager == null ? null : worldManager.getTrackedMob(mob);
    }

    @Override
    public void unregister(Mob mob, MobTypeSettings settings) {
        TrackedMobRegistry worldManager = getWorldManager(mob.getWorld());
        if (worldManager != null) {
            worldManager.unregister(mob, settings);
        }
    }

    @Override
    public void unregister(TrackedMob mob) {
        unregister(mob.getBukkitEntity(), mob.getSettings());
    }

    @Override
    public void clear() {
        perWorldManager.clear();
    }

    @Override
    public Collection<TrackedMob> getAllTrackedMobs() {
        List<TrackedMob> tracked = new ArrayList<>();
        for (TrackedMobRegistry perWorld : perWorldManager.values()) {
            tracked.addAll(perWorld.getAllTrackedMobs());
        }
        return Collections.unmodifiableCollection(tracked);
    }

}
