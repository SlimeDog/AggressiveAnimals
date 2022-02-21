package dev.ratas.aggressiveanimals.aggressive;

import org.bukkit.craftbukkit.v1_18_R1.entity.CraftLivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

import dev.ratas.aggressiveanimals.AggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;

public class NMSAggressivitySetter implements AggressivitySetter {
    private static final String AGGRESSIVE_ANIMAL = "AgressiveAnimal";
    private final AggressiveAnimals plugin;

    public NMSAggressivitySetter(AggressiveAnimals plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setFor(MobTypeSettings settings, org.bukkit.entity.LivingEntity entity) {
        // TODO - manage metadata elsewhere since it needs to be set regardless of
        // aggressivity setter implementation
        entity.setMetadata(AGGRESSIVE_ANIMAL, new FixedMetadataValue(this.plugin, true));

        LivingEntity livingEntity = ((CraftLivingEntity) entity).getHandle();
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

        if (livingEntity.getAttribute(Attributes.FOLLOW_RANGE) != null) {
            livingEntity.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(settings.attackSettings().range());
        }

        if (livingEntity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            livingEntity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(settings.attackSettings().damage());
        } else {
            livingEntity.getAttributes().getSyncableAttributes().add(new AttributeInstance(Attributes.ATTACK_DAMAGE,
                    attr -> attr.setBaseValue(settings.attackSettings().damage())));
        }

        AttributeInstance speedAttribute = livingEntity.getAttribute(Attributes.ATTACK_SPEED);
        if (speedAttribute != null) {
            speedAttribute.setBaseValue(speedAttribute.getBaseValue() * settings.attackSettings().speed());
        }
    }

}
