package dev.ratas.aggressiveanimals.aggressive.managed.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.aggressive.timers.GroupAggressivity;

public class GlobalRegistry implements TrackedMobRegistry {
    private final Map<World, TrackedMobRegistry> perWorldManager = new HashMap<>();
    private final GroupAggressivity groupAggro;

    public GlobalRegistry(GroupAggressivity groupAggro) {
        this.groupAggro = groupAggro;
    }

    public TrackedMobRegistry getWorldManager(World world) {
        return perWorldManager.compute(world, (w, am) -> (am == null) ? new WorldRegistry(groupAggro) : am);
    }

    @Override
    public TrackedMob register(Mob mob, MobTypeSettings settings) {
        return getWorldManager(mob.getWorld()).register(mob, settings);
    }

    @Override
    public TrackedMob getTrackedMob(Mob mob) {
        return getWorldManager(mob.getWorld()).getTrackedMob(mob);
    }

    @Override
    public void unregister(Mob mob, MobTypeSettings settings) {
        getWorldManager(mob.getWorld()).unregister(mob, settings);
    }

    @Override
    public void unregister(TrackedMob mob) {
        unregister(mob.getBukkitEntity(), mob.getSettings());
    }

    @Override
    public void markAttacking(TrackedMob mob, Player target, boolean triggerNeighbours) {
        getWorldManager(mob.getBukkitEntity().getWorld()).markAttacking(mob, target, triggerNeighbours);
    }

    @Override
    public void markNotAttacking(TrackedMob mob) {
        getWorldManager(mob.getBukkitEntity().getWorld()).markNotAttacking(mob);
    }

    @Override
    public void stopAttacking(Player player) {
        getWorldManager(player.getWorld()).stopAttacking(player);
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

    @Override
    public Player getTargetOf(TrackedMob mob) {
        return getWorldManager(mob.getBukkitEntity().getWorld()).getTargetOf(mob);
    }

    @Override
    public boolean resetTarget(TrackedMob mob) {
        return getWorldManager(mob.getBukkitEntity().getWorld()).resetTarget(mob);
    }

}
