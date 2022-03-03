package dev.ratas.aggressiveanimals.aggressive.managed.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.managed.target.TargetManager;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.aggressive.timers.GroupAggressivity;

public class WorldRegistry implements TrackedMobRegistry {
    private final Map<Mob, TrackedMob> trackedMobs = new HashMap<>();
    private final TargetManager targetManager = new TargetManager();
    private final GroupAggressivity groupAggro;

    public WorldRegistry(GroupAggressivity groupAggro) {
        this.groupAggro = groupAggro;
    }

    @Override
    public TrackedMob register(Mob mob, MobTypeSettings settings) {
        if (settings.entityType().getBukkitType() != mob.getType()) {
            throw new IllegalArgumentException(
                    "Mob and settings do not match: " + mob.getType() + " vs " + settings.entityType());
        }
        TrackedMob tracked = new TrackedMob(mob, settings);
        trackedMobs.put(mob, tracked);
        return tracked;
    }

    @Override
    public void unregister(TrackedMob wrapper) {
        unregister(wrapper.getBukkitEntity(), wrapper.getSettings());
    }

    @Override
    public void unregister(Mob mob, MobTypeSettings settings) {
        if (settings.entityType().getBukkitType() != mob.getType()) {
            throw new IllegalArgumentException(
                    "Mob and settings do not match: " + mob.getType() + " vs " + settings.entityType());
        }
        trackedMobs.remove(mob);
    }

    @Override
    public TrackedMob getTrackedMob(Mob mob) {
        return trackedMobs.get(mob);
    }

    @Override
    public void markAttacking(TrackedMob mob, Player target, boolean triggerNeighbours) {
        targetManager.setTarget(mob, target);
        if (triggerNeighbours) {
            groupAggro.checkMob(mob);
        }
    }

    @Override
    public void markNotAttacking(TrackedMob wrapper) {
        targetManager.removeTarget(wrapper);
    }

    @Override
    public void stopAttacking(Player player) {
        targetManager.removeTarget(player);
    }

    @Override
    public void clear() {
        trackedMobs.clear();
        targetManager.clear();
    }

    @Override
    public Collection<TrackedMob> getAllTrackedMobs() {
        return Collections.unmodifiableCollection(trackedMobs.values());
    }

    @Override
    public Player getTargetOf(TrackedMob mob) {
        return targetManager.getCurrentTarget(mob);
    }

    @Override
    public void resetTarget(TrackedMob mob) {
        targetManager.setTarget(mob, targetManager.getCurrentTarget(mob));
    }

}
