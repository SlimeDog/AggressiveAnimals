package dev.ratas.aggressiveanimals.aggressive.managed.target;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;

public class TargetManager {
    private final Map<Player, TrackedMob> targetMapper = new HashMap<>();
    private final Map<TrackedMob, Player> trackedTargets = new HashMap<>();

    public void setTarget(TrackedMob wrapper, Player player) {
        wrapper.getBukkitEntity().setTarget(player);
        if (player == null) {
            Player prevTarget = trackedTargets.remove(wrapper);
            if (prevTarget != null) {
                targetMapper.remove(prevTarget);
            }
        } else {
            targetMapper.put(player, wrapper);
            Player prevTarget = trackedTargets.put(wrapper, player);
            if (prevTarget != null) {
                targetMapper.remove(prevTarget);
            }
        }
    }

    public void removeTarget(Player player) {
        TrackedMob wrapper = targetMapper.remove(player);
        if (wrapper == null) {
            return;
        }
        trackedTargets.remove(wrapper);
        wrapper.getBukkitEntity().setTarget(null);
    }

    public void removeTarget(TrackedMob wrapper) {
        Player target = trackedTargets.get(wrapper);
        if (target == null) {
            return;
        }
        targetMapper.remove(target);
        wrapper.getBukkitEntity().setTarget(null);
    }

    public Player getCurrentTarget(TrackedMob mob) {
        return trackedTargets.get(mob);
    }

    public TrackedMob getWrapperOf(Player target) {
        return targetMapper.get(target);
    }

    public void clear() {
        targetMapper.clear();
        trackedTargets.clear();
    }

}
