package dev.ratas.aggressiveanimals.aggressive.managed;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import dev.ratas.aggressiveanimals.IAggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.timers.GroupAggressivity;

public abstract class MobWithTarget {
    private static final IAggressiveAnimals PLUGIN = (IAggressiveAnimals) JavaPlugin
            .getProvidingPlugin(MobWithTarget.class);
    public String AGGRESSIVE_ANIMAL_METADATA_TOKEN = "AggressiveAnimal";
    private static final Map<Player, MobWithTarget> TARGET_MAPPER = new HashMap<>();
    private final GroupAggressivity groupAggro;
    private Player target;

    public MobWithTarget(GroupAggressivity groupAggro) {
        this.groupAggro = groupAggro;
    }

    public abstract Mob getBukkitEntity();

    public abstract TrackedMob getTrackedMob();

    public abstract boolean hasAttackingGoals();

    private void setTarget(Player player) {
        getBukkitEntity().setTarget(player);
        if (player == null) {
            Player prevTarget = target;
            if (prevTarget != null) {
                // only drop the mapping if it still points at this mob
                TARGET_MAPPER.remove(prevTarget, this);
            }
            tagEntityNotAttacking();
            target = null;
        } else {
            target = player;
            TARGET_MAPPER.put(player, this);
            tagEntityAttacking();
        }
    }

    public void markAttacking(Player target, boolean triggerNeighbours) {
        setTarget(target);
        if (triggerNeighbours) {
            groupAggro.checkMob(getTrackedMob());
        }
    }

    /**
     * Forget the current target without touching the Bukkit entity. Used when
     * the entity is no longer loaded, so that the static target map does not
     * keep the entity (and its world) or the player reachable.
     */
    public void forgetTarget() {
        if (target != null) {
            TARGET_MAPPER.remove(target, this);
            target = null;
        }
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

    private void tagEntityAttacking() {
        getBukkitEntity().setMetadata(AGGRESSIVE_ANIMAL_METADATA_TOKEN,
                new FixedMetadataValue(PLUGIN.asPlugin(), true));
    }

    private void tagEntityNotAttacking() {
        getBukkitEntity().setMetadata(AGGRESSIVE_ANIMAL_METADATA_TOKEN,
                new FixedMetadataValue(PLUGIN.asPlugin(), false));
    }

}
