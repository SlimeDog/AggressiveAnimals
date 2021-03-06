package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public record MobAcquisitionSettings(Setting<Double> acquisitionRange, Setting<Double> deacquisitionRange)
        implements CheckingSettingBoundle {

    public boolean isInRange(Entity mob, Player target) {
        double max2 = deacquisitionRange.value() * deacquisitionRange.value();
        Location mobLoc = mob.getLocation();
        Location targetLoc = target.getLocation();
        if (mobLoc.getWorld() != targetLoc.getWorld()) {
            return false;
        }
        return mobLoc.distanceSquared(targetLoc) <= max2;
    }

    @Override
    public void checkAllTypes() throws IllegalStateException {
        checkType(acquisitionRange, Double.class);
        checkType(deacquisitionRange, Double.class);
    }

}
