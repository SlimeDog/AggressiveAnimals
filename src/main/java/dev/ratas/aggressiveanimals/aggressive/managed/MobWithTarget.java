package dev.ratas.aggressiveanimals.aggressive.managed;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.aggressive.timers.GroupAggressivity;

public abstract class MobWithTarget {
    private static final Map<Player, MobWithTarget> TARGET_MAPPER = new HashMap<>();
    private final GroupAggressivity groupAggro;
    private Player target;

    public MobWithTarget(GroupAggressivity groupAggro) {
        this.groupAggro = groupAggro;
    }

    public abstract Mob getBukkitEntity();

    public abstract TrackedMob getTrackedMob();

    public abstract void markAttacking();

    private void setTarget(Player player) {
        getBukkitEntity().setTarget(player);
        if (player == null) {
            Player prevTarget = target;
            if (prevTarget != null) {
                TARGET_MAPPER.remove(prevTarget);
            }
        } else {
            target = player;
            TARGET_MAPPER.put(player, this);
        }
    }

    public void markAttacking(Player target, boolean triggerNeighbours) {
        setTarget(target);
        if (triggerNeighbours) {
            groupAggro.checkMob(getTrackedMob());
        }
    }

    public void markNotAttacking() {
        setTarget(null);
    }

    public Player getTarget() {
        return target;
    }

    public boolean resetTarget() {
        LivingEntity prevTarget = getBukkitEntity().getTarget();
        setTarget(target);
        return prevTarget != target;
    }

    public static void removeTarget(Player player) {
        MobWithTarget wrapper = TARGET_MAPPER.get(player);
        if (wrapper != null) {
            wrapper.setTarget(null);
        }
    }

}
