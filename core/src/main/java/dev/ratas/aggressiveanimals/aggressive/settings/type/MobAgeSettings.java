package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;

public record MobAgeSettings(Setting<Boolean> attackAsAdult, Setting<Boolean> attackAsBaby) {

    public boolean shouldAttack(Entity entity) {
        if (!(entity instanceof Ageable)) {
            return true; // regardless
        }
        Ageable ageable = (Ageable) entity;
        if (ageable.isAdult() && attackAsAdult.value()) {
            return true;
        }
        if (!ageable.isAdult() && attackAsBaby.value()) {
            return true;
        }
        return false;
    }

}
