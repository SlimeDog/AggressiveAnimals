package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public record PlayerStateSettings(Setting<Boolean> attackStanding, Setting<Boolean> attackSneaking,
        Setting<Boolean> attackWalking, Setting<Boolean> attackSprinting, Setting<Boolean> attackLooking,
        Setting<Boolean> attackSleeping, Setting<Boolean> attackGliding) implements CheckingSettingBoundle {

    private static final double MAX_DISTANCE = 20; // TODO - read from data?
    private static final double MIN_ALLOWED_DOT_PRODUCT = 0.99D;

    @Override
    public void checkAllTypes() throws IllegalStateException {
        checkType(attackStanding, Boolean.class);
        checkType(attackSneaking, Boolean.class);
        checkType(attackWalking, Boolean.class);
        checkType(attackSprinting, Boolean.class);
        checkType(attackLooking, Boolean.class);
        checkType(attackSleeping, Boolean.class);
        checkType(attackGliding, Boolean.class);
    }

    public boolean shouldAttack(Entity mob, Player target) {
        if (isStanding(target) && attackStanding.value()) {
            return true;
        }
        if (target.isSneaking() && attackSneaking.value()) {
            return true;
        }
        if (isWalking(target) && attackWalking.value()) {
            return true;
        }
        if (target.isSprinting() && attackSprinting.value()) {
            return true;
        }
        if (target.isSleeping() && attackSleeping.value()) {
            return true;
        }
        if (target.isGliding() && attackGliding.value()) {
            return true;
        }
        if (isLookingAt(target, mob, MAX_DISTANCE, MIN_ALLOWED_DOT_PRODUCT) && attackLooking.value()) {
            return true;
        }
        return false;
    }

    private boolean isStanding(Player player) {
        if (player.isFlying()) {
            return false;
        }
        if (player.isSneaking()) {
            return false;
        }
        if (player.isGliding()) {
            return false;
        }
        return true;
    }

    private boolean isWalking(Player player) {
        if (!isStanding(player)) {
            return false;
        }
        if (player.isSprinting()) {
            return false;
        }
        return player.getVelocity().lengthSquared() > 0; // only if moving
    }

    private boolean isLookingAt(Player player, Entity mob, double maxDistance, double minAllowed) {
        Location from = player.getEyeLocation();
        Location to = mob.getLocation();
        if (from.getWorld() != to.getWorld()) {
            return false;
        }
        double md2 = maxDistance * maxDistance;
        if (from.distanceSquared(to) > md2) {
            return false;
        }
        Vector fromToDirection = to.toVector().subtract(from.toVector());
        Vector lookDirection = from.getDirection();
        return fromToDirection.dot(lookDirection) >= minAllowed;
    }

}
