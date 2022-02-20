package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

// #   acquisition-range: 12              How close will mobs acquire a player and start an attack?
// #   deacquisition-range: 20            How far away must the player run to stop an attack?

public record MobAcquisationSettings(double acquisitionRange, double deacquisitionRange) {

    public boolean isInRange(Entity mob, Player target) {
        double max2 = acquisitionRange * acquisitionRange;
        return mob.getLocation().distanceSquared(target.getLocation()) <= max2;
    }

}
