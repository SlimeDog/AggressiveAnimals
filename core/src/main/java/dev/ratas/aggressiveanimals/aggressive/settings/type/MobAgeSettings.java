package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

public record MobAgeSettings(boolean attackAsAdult, boolean attackAsBaby) {

    public boolean shouldAttack(Entity entity) {
        if (!(entity instanceof Ageable)) {
            return true; // regardless
        }
        Ageable ageable = (Ageable) entity;
        if (ageable.isAdult() && attackAsAdult) {
            return true;
        }
        if (!ageable.isAdult() && attackAsBaby) {
            return true;
        }
        return false;
    }

}
