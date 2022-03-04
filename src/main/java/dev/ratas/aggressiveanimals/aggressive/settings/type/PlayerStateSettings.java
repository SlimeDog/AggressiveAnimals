package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

// #   player-movement:                   Mob should attack only if the player is
// #     standing: true
// #     sneaking: true
// #     walking: true
// #     sprinting: true
// #     looking: true                    Like Enderman
// #     sleeping: true
// #     gliding: true

public record PlayerStateSettings(boolean attackStanding, boolean attackSneaking, boolean attackWalking,
        boolean attackSprinting, boolean attackLooking, boolean attackSleeping, boolean attackGliding) {

    private static final double MAX_DISTANCE = 20; // TODO - read from data?
    private static final double MIN_ALLOWED_DOT_PRODUCT = 0.99D;

    public boolean shouldAttack(Entity mob, Player target) {
        if (isStanding(target) && attackStanding) {
            return true;
        }
        if (target.isSneaking() && attackSneaking) {
            return true;
        }
        if (isWalking(target) && attackWalking) {
            return true;
        }
        if (target.isSprinting() && attackSprinting) {
            return true;
        }
        if (target.isSleeping() && attackSleeping) {
            return true;
        }
        if (target.isGliding() && attackGliding) {
            return true;
        }
        if (isLookingAt(target, mob, MAX_DISTANCE, MIN_ALLOWED_DOT_PRODUCT) && attackLooking) {
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
        return player.getVelocity().lengthSquared() == 0; // only if NOT moving
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
