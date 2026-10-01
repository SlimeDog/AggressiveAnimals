package dev.ratas.aggressiveanimals.aggressive.paper;

import java.util.EnumSet;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;

/**
 * Moves towards, looks at, and attacks the target that the plugin has assigned
 * to the mob (via {@link Mob#setTarget(LivingEntity)}). Target acquisition is
 * handled by the plugin itself; this goal only acts on the current target.
 * <p>
 * Built only on Paper API (no server internals), so the same class works on
 * every supported server version.
 */
public final class AttackTargetGoal implements Goal<Mob> {
    /** Vanilla melee reach: the attacker's box grows by this much horizontally. */
    private static final double MELEE_REACH = Math.sqrt(2.04) - 0.6;
    private static final double PATH_SPEED_MODIFIER = 1.0;
    private static final int REPATH_INTERVAL_TICKS = 10;
    // leaping (mirrors vanilla LeapAtTargetGoal)
    private static final double LEAP_MIN_DISTANCE_SQUARED = 4.0;
    private static final double LEAP_MAX_DISTANCE_SQUARED = 16.0;
    private static final int LEAP_CHANCE_ONE_IN = 5;
    private static final double LEAP_HORIZONTAL_SPEED = 0.4;
    private static final double LEAP_KEEP_VELOCITY = 0.2;
    // hopping movement for slime-like mobs that do not follow paths
    private static final int HOP_INTERVAL_TICKS = 15;
    private static final double HOP_HORIZONTAL_SPEED = 0.35;
    private static final double HOP_VERTICAL_SPEED = 0.42;

    private final GoalKey<Mob> key;
    private final Mob mob;
    private final int attackCooldownTicks;
    private final double leapHeight;
    private final boolean hopping;

    private int nextAttackTick;
    private int nextRepathTick;
    private int nextMovementTick;

    /**
     * @param key                 the goal key
     * @param mob                 the mob running the goal
     * @param attackCooldownTicks minimum ticks between two attacks
     * @param leapHeight          vertical leap velocity; 0 or below disables leaping
     * @param hopping             true for mobs that move by hopping (slime-like)
     *                            rather than by following paths
     */
    public AttackTargetGoal(GoalKey<Mob> key, Mob mob, int attackCooldownTicks, double leapHeight,
            boolean hopping) {
        this.key = key;
        this.mob = mob;
        this.attackCooldownTicks = Math.max(1, attackCooldownTicks);
        this.leapHeight = leapHeight;
        this.hopping = hopping;
    }

    public static GoalKey<Mob> createKey(NamespacedKey namespacedKey) {
        return GoalKey.of(Mob.class, namespacedKey);
    }

    private LivingEntity getValidTarget() {
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isValid() || target.isDead()) {
            return null;
        }
        if (!target.getWorld().equals(mob.getWorld())) {
            return null;
        }
        // e.g. a sulfur cube holding an absorbed block has its AI switched off
        if (!mob.hasAI() || !mob.isAware()) {
            return null;
        }
        return target;
    }

    @Override
    public boolean shouldActivate() {
        return getValidTarget() != null;
    }

    @Override
    public boolean shouldStayActive() {
        return getValidTarget() != null;
    }

    @Override
    public void start() {
        int now = Bukkit.getCurrentTick();
        nextRepathTick = now;
        nextMovementTick = now;
    }

    @Override
    public void stop() {
        if (!hopping) {
            mob.getPathfinder().stopPathfinding();
        }
    }

    @Override
    public void tick() {
        LivingEntity target = getValidTarget();
        if (target == null) {
            return;
        }
        int now = Bukkit.getCurrentTick();
        mob.lookAt(target);
        if (hopping) {
            hopTowards(target, now);
        } else {
            if (now >= nextRepathTick || !mob.getPathfinder().hasPath()) {
                mob.getPathfinder().moveTo(target, PATH_SPEED_MODIFIER);
                nextRepathTick = now + REPATH_INTERVAL_TICKS;
            }
            maybeLeap(target, now);
        }
        if (now >= nextAttackTick && isWithinMeleeRange(target) && mob.hasLineOfSight(target)) {
            mob.attack(target);
            nextAttackTick = now + attackCooldownTicks;
        }
    }

    private boolean isWithinMeleeRange(LivingEntity target) {
        BoundingBox reach = mob.getBoundingBox().clone().expand(MELEE_REACH, 0.0, MELEE_REACH);
        return reach.overlaps(target.getBoundingBox());
    }

    private Vector horizontalDirectionTo(LivingEntity target) {
        Vector direction = target.getLocation().toVector().subtract(mob.getLocation().toVector()).setY(0);
        if (direction.lengthSquared() < 1.0E-7) {
            return null;
        }
        return direction.normalize();
    }

    private void maybeLeap(LivingEntity target, int now) {
        if (leapHeight <= 0 || now < nextMovementTick || !mob.isOnGround()) {
            return;
        }
        double distSquared = mob.getLocation().distanceSquared(target.getLocation());
        if (distSquared < LEAP_MIN_DISTANCE_SQUARED || distSquared > LEAP_MAX_DISTANCE_SQUARED) {
            return;
        }
        if (ThreadLocalRandom.current().nextInt(LEAP_CHANCE_ONE_IN) != 0) {
            return;
        }
        Vector direction = horizontalDirectionTo(target);
        if (direction == null) {
            return;
        }
        Vector velocity = direction.multiply(LEAP_HORIZONTAL_SPEED)
                .add(mob.getVelocity().multiply(LEAP_KEEP_VELOCITY));
        mob.setVelocity(velocity.setY(leapHeight));
        nextMovementTick = now + attackCooldownTicks;
    }

    private void hopTowards(LivingEntity target, int now) {
        if (now < nextMovementTick || !mob.isOnGround()) {
            return;
        }
        Vector direction = horizontalDirectionTo(target);
        if (direction == null) {
            return;
        }
        mob.setVelocity(direction.multiply(HOP_HORIZONTAL_SPEED).setY(HOP_VERTICAL_SPEED));
        nextMovementTick = now + HOP_INTERVAL_TICKS;
    }

    @Override
    public GoalKey<Mob> getKey() {
        return key;
    }

    @Override
    public EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE, GoalType.LOOK);
    }

}
