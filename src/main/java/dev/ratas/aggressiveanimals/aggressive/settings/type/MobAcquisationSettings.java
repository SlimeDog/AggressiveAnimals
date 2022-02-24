package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

// #   acquisition-range: 12              How close will mobs acquire a player and start an attack?
// #   deacquisition-range: 20            How far away must the player run to stop an attack?

public record MobAcquisationSettings(double acquisitionRange, double deacquisitionRange) {

    public boolean isInRange(Entity mob, Player target) {
        double max2 = deacquisitionRange * deacquisitionRange;
        Location mobLoc = mob.getLocation();
        Location targetLoc = target.getLocation();
        if (mobLoc.getWorld() != targetLoc.getWorld()) {
            return false;
        }
        return mobLoc.distanceSquared(targetLoc) <= max2;
    }

}
