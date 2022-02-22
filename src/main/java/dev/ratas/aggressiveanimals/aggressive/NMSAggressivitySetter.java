package dev.ratas.aggressiveanimals.aggressive;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.metadata.FixedMetadataValue;

import dev.ratas.aggressiveanimals.AggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

public class NMSAggressivitySetter implements AggressivitySetter {
    private static final NMSResolver NMS_RESOLVER = new NMSResolver();
    private static final String AGGRESSIVE_ANIMAL_METADATA_TOKEN = "AgressiveAnimal";
    private final AggressiveAnimals plugin;

    public NMSAggressivitySetter(AggressiveAnimals plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setFor(MobTypeSettings settings, org.bukkit.entity.LivingEntity entity) {
        // TODO - manage metadata elsewhere since it needs to be set regardless of
        // aggressivity setter implementation
        entity.setMetadata(AGGRESSIVE_ANIMAL_METADATA_TOKEN, new FixedMetadataValue(this.plugin, true));

        LivingEntity livingEntity = NMS_RESOLVER.getNMSEntity(entity);
        Mob mob = (Mob) livingEntity;

        float range = (float) settings.acquisitionSettings().acquisitionRange();

        if (settings.ageSettings().shouldAttack(entity)) { // not ageable -> attack, otherwise depends on baby/adult
                                                           // state
            mob.targetSelector.getAvailableGoals().removeIf(goal -> {
                return goal.getGoal() instanceof PanicGoal;
            });

            mob.targetSelector.addGoal(2,
                    new MeleeAttackGoal((PathfinderMob) livingEntity, settings.attackSettings().speed(), false));
            mob.targetSelector.addGoal(8, new LookAtPlayerGoal(mob, Player.class, range));
            mob.targetSelector.addGoal(8, new RandomLookAroundGoal(mob));

            mob.targetSelector.addGoal(1, new HurtByTargetGoal((PathfinderMob) livingEntity));
            mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Player>(mob, Player.class, true));
            // no idea what that was referring to
            // mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<Npc>(mob,
            // Npc.class, true));
        }
        AttributeInstance speedAttr = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            speedAttr.setBaseValue(speedAttr.getBaseValue() * settings.speedMultiplier());
        }

        if (livingEntity.getAttribute(Attributes.FOLLOW_RANGE) != null) {
            livingEntity.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(settings.attackSettings().range());
        }

        if (livingEntity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            livingEntity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(settings.attackSettings().damage());
        } else {
            NMS_RESOLVER.setAttribute(livingEntity, new AttributeInstance(Attributes.ATTACK_DAMAGE,
                    attr -> attr.setBaseValue(settings.attackSettings().damage())));
        }

        AttributeInstance speedAttribute = livingEntity.getAttribute(Attributes.ATTACK_SPEED);
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

        private LivingEntity getNMSEntity(org.bukkit.entity.LivingEntity bukkit) {
            try {
                return (LivingEntity) getHandleMethod.invoke(bukkit);
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
