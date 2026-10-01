package dev.ratas.aggressiveanimals.aggressive.paper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Mob;

import com.destroystokyo.paper.entity.ai.GoalKey;

import dev.ratas.aggressiveanimals.IAggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.AggressivitySetter;
import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.managed.addon.AddonType;
import dev.ratas.aggressiveanimals.aggressive.managed.addon.MobAddon;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.slimedogcore.impl.SlimeDogCore;

/**
 * Makes mobs aggressive using only Paper API: attribute changes through
 * {@link org.bukkit.attribute.Attributable} and attacking through a custom
 * {@link AttackTargetGoal} registered with Paper's mob goal API.
 */
public class PaperAggressivitySetter implements AggressivitySetter {
    /** Higher priority (lower number) than vanilla goals such as panicking. */
    private static final int ATTACK_GOAL_PRIORITY = 0;
    private final IAggressiveAnimals plugin;
    private final GoalKey<Mob> attackGoalKey;

    public PaperAggressivitySetter(IAggressiveAnimals plugin) {
        this.plugin = plugin;
        this.attackGoalKey = AttackTargetGoal.createKey(new NamespacedKey(plugin.asPlugin(), "attack_target"));
    }

    @Override
    public SlimeDogCore getPlugin() {
        return plugin.asPlugin();
    }

    private static AggressionAddon getOrCreateAddon(TrackedMob wrapper) {
        if (!wrapper.hasAddon(AddonType.GOAL)) {
            wrapper.addAddon(new AggressionAddon());
        }
        return (AggressionAddon) wrapper.getAddon(AddonType.GOAL);
    }

    @Override
    public void setAggressivityAttributes(TrackedMob wrapper) {
        AggressionAddon addon = getOrCreateAddon(wrapper);
        Mob mob = wrapper.getBukkitEntity();
        // never stack modifications on top of modified values
        addon.restoreAttributes(mob);
        MobTypeSettings settings = wrapper.getSettings();
        plugin.getDebugLogger().log("[Paper Setter] Setting aggressivity attributes to: " + settings);

        double speedMultiplier = settings.speedMultiplier().value();
        addon.modifyBase(mob, Attribute.MOVEMENT_SPEED, base -> base * speedMultiplier);
        addon.modifyBase(mob, Attribute.FLYING_SPEED, base -> base * speedMultiplier);
        double range = settings.acquisitionSettings().acquisitionRange().value();
        addon.modifyBase(mob, Attribute.FOLLOW_RANGE, base -> range);

        // passive mobs normally have no attack damage attribute
        if (mob.getAttribute(Attribute.ATTACK_DAMAGE) == null) {
            mob.registerAttribute(Attribute.ATTACK_DAMAGE);
            addon.savedAttributes.put(Attribute.ATTACK_DAMAGE, 0.0);
            mob.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(settings.attackSettings().damage().value());
        } else {
            double damage = settings.attackSettings().damage().value();
            addon.modifyBase(mob, Attribute.ATTACK_DAMAGE, base -> damage);
        }
        plugin.getDebugLogger().log("[Paper Setter] Previous attributes: " + addon.savedAttributes);
    }

    @Override
    public void setAttackingGoals(TrackedMob wrapper) {
        AggressionAddon addon = getOrCreateAddon(wrapper);
        if (addon.attackGoal != null) {
            return; // already attacking; the goal follows target changes by itself
        }
        plugin.getDebugLogger().log("[Paper Setter] Setting aggressive/attacking goals");
        Mob mob = wrapper.getBukkitEntity();
        MobTypeSettings settings = wrapper.getSettings();
        int cooldownTicks = (int) Math.round(settings.attackSettings().speed().value());
        double leapHeight = settings.attackSettings().attackLeapHeight().value();
        boolean hopping = settings.entityType().value().isHoppingMob();
        addon.attackGoal = new AttackTargetGoal(attackGoalKey, mob, cooldownTicks, leapHeight, hopping);
        Bukkit.getMobGoals().addGoal(mob, ATTACK_GOAL_PRIORITY, addon.attackGoal);

        if (settings.largerWhenAggressive().value()) {
            AttributeInstance scale = mob.getAttribute(Attribute.SCALE);
            if (scale != null) {
                addon.savedScale = scale.getBaseValue();
                scale.setBaseValue(SIZE_WHEN_AGGRO);
            }
        }
    }

    @Override
    public void removeAttackingGoals(TrackedMob wrapper) {
        if (!wrapper.hasAddon(AddonType.GOAL)) {
            return;
        }
        AggressionAddon addon = (AggressionAddon) wrapper.getAddon(AddonType.GOAL);
        Mob mob = wrapper.getBukkitEntity();
        if (addon.attackGoal != null) {
            Bukkit.getMobGoals().removeGoal(mob, addon.attackGoal);
            addon.attackGoal = null;
        }
        if (addon.savedScale != null) {
            AttributeInstance scale = mob.getAttribute(Attribute.SCALE);
            if (scale != null) {
                scale.setBaseValue(addon.savedScale);
            }
            addon.savedScale = null;
        }
    }

    @Override
    public void stopTracking(TrackedMob wrapper) {
        plugin.getDebugLogger().log("[Paper Setter] Removing goals and resetting attributes");
        if (!wrapper.hasAddon(AddonType.GOAL)) {
            plugin.getLogger().warning("No previously saved attributes for mob " + wrapper.getBukkitEntity()
                    + " - cannot properly pacify");
            return;
        }
        removeAttackingGoals(wrapper);
        AggressionAddon addon = (AggressionAddon) wrapper.getAddon(AddonType.GOAL);
        addon.restoreAttributes(wrapper.getBukkitEntity());
    }

    private static final class AggressionAddon implements MobAddon {
        private final Map<Attribute, Double> savedAttributes = new LinkedHashMap<>();
        private AttackTargetGoal attackGoal;
        private Double savedScale;

        private void modifyBase(Mob mob, Attribute attribute, java.util.function.DoubleUnaryOperator change) {
            AttributeInstance instance = mob.getAttribute(attribute);
            if (instance == null) {
                return;
            }
            savedAttributes.put(attribute, instance.getBaseValue());
            instance.setBaseValue(change.applyAsDouble(instance.getBaseValue()));
        }

        private void restoreAttributes(Mob mob) {
            for (Map.Entry<Attribute, Double> entry : savedAttributes.entrySet()) {
                AttributeInstance instance = mob.getAttribute(entry.getKey());
                if (instance != null) {
                    instance.setBaseValue(entry.getValue());
                }
            }
            savedAttributes.clear();
        }

        @Override
        public AddonType getAddonType() {
            return AddonType.GOAL;
        }

        @Override
        public boolean isEmpty() {
            // used to determine whether or not attacking goals have been set
            return attackGoal == null;
        }

    }

}
