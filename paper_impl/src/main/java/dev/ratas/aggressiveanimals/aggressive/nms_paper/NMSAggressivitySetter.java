package dev.ratas.aggressiveanimals.aggressive.nms_paper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dev.ratas.aggressiveanimals.IAggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.AggressivitySetter;
import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.managed.addon.AddonType;
import dev.ratas.aggressiveanimals.aggressive.managed.addon.MobAddon;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.slimedogcore.impl.SlimeDogCore;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

public class NMSAggressivitySetter implements AggressivitySetter {
    private static final NMSResolver NMS_RESOLVER = new NMSResolver();
    private static final double ATTACK_GOAL_SPEED_MODIFIER = 1.0D;
    private final IAggressiveAnimals plugin;

    public NMSAggressivitySetter(IAggressiveAnimals plugin) {
        this.plugin = plugin;
    }

    public SlimeDogCore getPlugin() {
        return plugin.asPlugin();
    }

    @Override
    public void setAggressivityAttributes(TrackedMob wrapper) {
        if (!wrapper.hasAddon(AddonType.GOAL)) {
            wrapper.addAddon(new GoalAddon());
        }
        GoalAddon addon = (GoalAddon) wrapper.getAddon(AddonType.GOAL);
        MobTypeSettings settings = wrapper.getSettings();
        plugin.getDebugLogger().log("[NMS Setter] Setting aggressivivity attributes to: " + settings);
        org.bukkit.entity.Mob entity = wrapper.getBukkitEntity();
        Mob mob = NMS_RESOLVER.getNMSEntity(entity);
        MobAttributes savedAttributes = new MobAttributes();
        AttributeInstance moveSpeedAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (moveSpeedAttr != null) {
            savedAttributes.prevValues.put(Attributes.MOVEMENT_SPEED, moveSpeedAttr.getBaseValue());
            moveSpeedAttr.setBaseValue(moveSpeedAttr.getBaseValue() * settings.speedMultiplier().value());
        }

        AttributeInstance followRangeAttr = mob.getAttribute(Attributes.FOLLOW_RANGE);
        if (followRangeAttr != null) {
            savedAttributes.prevValues.put(Attributes.FOLLOW_RANGE, followRangeAttr.getBaseValue());
            followRangeAttr.setBaseValue(settings.acquisitionSettings().acquisitionRange().value());
        }

        AttributeInstance attackDamageAttr = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamageAttr != null) {
            savedAttributes.prevValues.put(Attributes.ATTACK_DAMAGE, attackDamageAttr.getBaseValue());
            attackDamageAttr.setBaseValue(settings.attackSettings().damage().value());
        } else {
            savedAttributes.prevValues.put(Attributes.ATTACK_DAMAGE, 0.0);
            NMS_RESOLVER.setAttribute(mob, new AttributeInstance(Attributes.ATTACK_DAMAGE,
                    attr -> attr.setBaseValue(settings.attackSettings().damage().value())));
        }

        AttributeInstance attackSpeedAttribute = mob.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeedAttribute != null) {
            savedAttributes.prevValues.put(Attributes.ATTACK_SPEED, attackSpeedAttribute.getBaseValue());
            attackSpeedAttribute
                    .setBaseValue(attackSpeedAttribute.getBaseValue() * settings.attackSettings().speed().value());
        }
        addon.attributes = savedAttributes;
        plugin.getDebugLogger().log("[NMS Setter] Previous attributes: " + savedAttributes);
    }

    @Override
    public void setAttackingGoals(TrackedMob wrapper) {
        if (!wrapper.hasAddon(AddonType.GOAL)) {
            wrapper.addAddon(new GoalAddon());
        }
        GoalAddon addon = (GoalAddon) wrapper.getAddon(AddonType.GOAL);
        plugin.getDebugLogger().log("[NMS Setter] Setting aggressive/attacking goals");
        org.bukkit.entity.Mob entity = wrapper.getBukkitEntity();
        Mob mob = NMS_RESOLVER.getNMSEntity(entity);
        MobTypeSettings settings = wrapper.getSettings();

        float range = (float) (double) settings.acquisitionSettings().acquisitionRange().value();
        mob.targetSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof PanicGoal);
        // if (settings.overrideTargets().value()) {
        //     mob.targetSelector.getAvailableGoals().clear();
        // }

        Goal cur;
        mob.targetSelector.addGoal(2,
                cur = new MeleeAttackGoal((PathfinderMob) mob, ATTACK_GOAL_SPEED_MODIFIER, false));
        addon.goals.add(cur);
        mob.targetSelector.addGoal(8, cur = new LookAtPlayerGoal(mob, Player.class, range));
        addon.goals.add(cur);
        mob.targetSelector.addGoal(8, cur = new RandomLookAroundGoal(mob));
        addon.goals.add(cur);

        mob.targetSelector.addGoal(1, cur = new HurtByTargetGoal((PathfinderMob) mob));
        addon.goals.add(cur);
        mob.targetSelector.addGoal(2, cur = new NearestAttackableTargetGoal<Player>(mob, Player.class, true));
        addon.goals.add(cur);
        float leapHeight = (float) (double) settings.attackSettings().attackLeapHeight().value();
        if (leapHeight > 0) {
            mob.targetSelector.addGoal(9, cur = new LeapAtTargetGoal(mob, leapHeight));
            addon.goals.add(cur);
        }
        if (settings.largerWhenAggressive().value()) {
            AttributeInstance scaleAttribute = mob.getAttribute(Attributes.SCALE);
            if (scaleAttribute != null) {
                scaleAttribute.setBaseValue(SIZE_WHEN_AGGRO);
            }
        }
    }

    @Override
    public void stopTracking(TrackedMob wrapper) {
        plugin.getDebugLogger().log("[NMS Setter] Removing previous goals and resetting attributes");
        Mob mob = NMS_RESOLVER.getNMSEntity(wrapper.getBukkitEntity());
        GoalAddon addon = (GoalAddon) wrapper.getAddon(AddonType.GOAL);
        MobAttributes saved = addon.attributes;
        if (saved == null) {
            plugin.getLogger().warning("No previously saved attributes for mob  " + wrapper.getBukkitEntity()
                    + " - cannot properly pacify");
            return;
        }
        for (Map.Entry<Holder<Attribute>, Double> entry : saved.prevValues.entrySet()) {
            AttributeInstance attrInst = mob.getAttribute(entry.getKey());
            if (attrInst == null) {
                plugin.getLogger()
                        .warning("Could not reset the attribute '" + entry.getKey() +
                                "' for mob " + wrapper.getTrackedMob().getSettings().entityType() +
                                " and attribute '" + entry.getKey().getRegisteredName() +
                                "' to the default value '"
                                + entry.getValue() + "' because the attribute instance was null!");
                continue;
            }
            attrInst.setBaseValue(entry.getValue());
        }
        saved.prevValues.clear();
    }

    @Override
    public void removeAttackingGoals(TrackedMob wrapper) {
        Mob mob = NMS_RESOLVER.getNMSEntity(wrapper.getBukkitEntity());
        GoalAddon addon = (GoalAddon) wrapper.getAddon(AddonType.GOAL);
        for (Goal goal : addon.goals) {
            mob.targetSelector.removeGoal(goal);
        }
        addon.goals.clear();
        if (wrapper.getSettings().largerWhenAggressive().value()) {
            AttributeInstance scaleAttribute = mob.getAttribute(Attributes.SCALE);
            if (scaleAttribute != null) {
                scaleAttribute.setBaseValue(1.0);
            }
        }
    }

    private class MobAttributes {
        private final Map<Holder<Attribute>, Double> prevValues = new HashMap<>();

        @Override
        public String toString() {
            return String.format("{MobAttributes: %s}",
                    prevValues.entrySet().stream().map(e -> e.getKey().getRegisteredName() + ": " + e.getValue())
                            .collect(Collectors.toList()));
        }

    }

    private static final class NMSResolver {
        private static final String CB_PACKAGE = "org.bukkit.craftbukkit";
        private static final String MIDDLE_PACKAGE = "entity";
        private static final String CRAFT_LIVING_ENTITY_CLASS_NAME = "CraftLivingEntity";
        private final Class<?> craftLivingEntityClass;
        private final Method getHandleMethod;
        private final Field attributeMapField; // field in NMS LivingEntity class of type AttributeMap
        private final Field attributesField; // field in NMS AttributeMap class of type Map

        private NMSResolver() {
            try {
                craftLivingEntityClass = Class
                        .forName(String.format("%s.%s.%s", CB_PACKAGE, MIDDLE_PACKAGE,
                                CRAFT_LIVING_ENTITY_CLASS_NAME));
                getHandleMethod = craftLivingEntityClass.getMethod("getHandle");
                attributeMapField = LivingEntity.class.getDeclaredField("attributes");
                attributesField = AttributeMap.class.getDeclaredField("attributes");
                attributeMapField.setAccessible(true);
                attributesField.setAccessible(true);
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        private Mob getNMSEntity(org.bukkit.entity.Mob bukkit) {
            try {
                return (Mob) getHandleMethod.invoke(bukkit);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        @SuppressWarnings("unchecked")
        private void setAttribute(LivingEntity nms, AttributeInstance instance) {
            Map<Holder<Attribute>, AttributeInstance> attributes;
            try {
                attributes = (Map<Holder<Attribute>, AttributeInstance>) attributesField.get(attributeMapField.get(nms)); // unchecked
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            attributes.put(instance.getAttribute(), instance);
        }
    }

    private static final class GoalAddon implements MobAddon {
        private final Set<Goal> goals = new HashSet<>();
        private MobAttributes attributes;

        @Override
        public AddonType getAddonType() {
            return AddonType.GOAL;
        }

        @Override
        public boolean isEmpty() {
            // this is used to determine whether or not attacking goals have been set
            return goals.isEmpty();
        }

    }

}
