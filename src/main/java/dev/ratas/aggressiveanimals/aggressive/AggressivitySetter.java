package dev.ratas.aggressiveanimals.aggressive;

import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;

public interface AggressivitySetter {
    public static final String AGGRESSIVE_ANIMAL_METADATA_TOKEN = "AgressiveAnimal";

    void setFor(MobTypeSettings settings, LivingEntity entity);

    JavaPlugin getPlugin();

    default void markAsAggressive(LivingEntity entity) {
        entity.setMetadata(AGGRESSIVE_ANIMAL_METADATA_TOKEN, new FixedMetadataValue(getPlugin(), true));
    }

}
