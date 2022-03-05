package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public record MobAcquisitionSettings(double acquisitionRange, double deacquisitionRange) {

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
