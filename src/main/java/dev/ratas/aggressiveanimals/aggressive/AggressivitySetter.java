package dev.ratas.aggressiveanimals.aggressive;

import org.bukkit.entity.LivingEntity;

import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;

public interface AggressivitySetter {

    void setFor(MobTypeSettings settings, LivingEntity entity);

}
