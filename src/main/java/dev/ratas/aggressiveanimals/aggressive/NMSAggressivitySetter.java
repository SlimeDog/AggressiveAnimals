package dev.ratas.aggressiveanimals.aggressive;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import dev.ratas.aggressiveanimals.AggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

public class NMSAggressivitySetter implements AggressivitySetter {
    private static final NMSResolver NMS_RESOLVER = new NMSResolver();
    private final AggressiveAnimals plugin;

    public NMSAggressivitySetter(AggressiveAnimals plugin) {
        this.plugin = plugin;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public void setAggressive(MobWrapper wrapper) {
        org.bukkit.entity.Mob entity = wrapper.getBukkitEntity();
        Mob mob = NMS_RESOLVER.getNMSEntity(entity);
        MobTypeSettings settings = wrapper.getSettings();

        float range = (float) settings.attackSettings().range();

        if (settings.ageSettings().shouldAttack(entity)) { // not ageable -> attack, otherwise depends on baby/adult
                                                           // state
            this.markAsAggressive(wrapper);
            mob.targetSelector.getAvailableGoals().removeIf(goal -> {
                return goal.getGoal() instanceof PanicGoal;
            });

            Goal cur;
            mob.targetSelector.addGoal(2,
                    cur = new MeleeAttackGoal((PathfinderMob) mob, settings.attackSettings().speed(), false));
            wrapper.getGoals().add(cur);
            mob.targetSelector.addGoal(8, cur = new LookAtPlayerGoal(mob, Player.class, range));
            wrapper.getGoals().add(cur);
            mob.targetSelector.addGoal(8, cur = new RandomLookAroundGoal(mob));
            wrapper.getGoals().add(cur);

            mob.targetSelector.addGoal(1, cur = new HurtByTargetGoal((PathfinderMob) mob));
            wrapper.getGoals().add(cur);
            mob.targetSelector.addGoal(2, cur = new NearestAttackableTargetGoal<Player>(mob, Player.class, true));
            wrapper.getGoals().add(cur);
        }
        AttributeInstance speedAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(speedAttr.getBaseValue() * settings.speedMultiplier());
        }

        if (mob.getAttribute(Attributes.FOLLOW_RANGE) != null) {
            mob.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(settings.attackSettings().range());
        }

        if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            mob.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(settings.attackSettings().damage());
        } else {
            NMS_RESOLVER.setAttribute(mob, new AttributeInstance(Attributes.ATTACK_DAMAGE,
                    attr -> attr.setBaseValue(settings.attackSettings().damage())));
        }

        AttributeInstance speedAttribute = mob.getAttribute(Attributes.ATTACK_SPEED);
        if (speedAttribute != null) {
            speedAttribute.setBaseValue(speedAttribute.getBaseValue() * settings.attackSettings().speed());
        }
    }

    private static class NMSResolver {
        private static final String PACKAGE_BASE = "org.bukkit.craftbukkit";
        private static final String VERSION = org.bukkit.Bukkit.getServer().getClass().getPackage().getName()
                .split("\\.")[3];
        private static final String MIDDLE_PACKAGE = "entity";
        private static final String CRAFT_LIVING_ENTITY_CLASS_NAME = "CraftLivingEntity";
        private final Class<?> craftLivingEntityClass;
        private final Method getHandleMethod;
        private final Field attributeMapField; // field in NMS LivingEntity class of type AttributeMap
        private final Field attributesField; // field in NMS AttributeMap class of type Map

        private NMSResolver() {
            try {
                craftLivingEntityClass = Class
                        .forName(String.format("%s.%s.%s.%s", PACKAGE_BASE, VERSION, MIDDLE_PACKAGE,
                                CRAFT_LIVING_ENTITY_CLASS_NAME));
                getHandleMethod = craftLivingEntityClass.getMethod("getHandle");
                attributeMapField = LivingEntity.class.getDeclaredField("bQ"); // mojang-mapped as "attributes"
                attributesField = AttributeMap.class.getDeclaredField("b"); // mojang-mapped as "attributes"
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
            Map<Attribute, AttributeInstance> attributes;
            try {
                attributes = (Map<Attribute, AttributeInstance>) attributesField.get(attributeMapField.get(nms)); // unchecked
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            attributes.put(instance.getAttribute(), instance);
        }
    }

}
