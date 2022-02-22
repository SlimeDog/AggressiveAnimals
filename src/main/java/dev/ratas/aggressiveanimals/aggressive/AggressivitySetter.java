package dev.ratas.aggressiveanimals.aggressive;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public interface AggressivitySetter {
    public static final String AGGRESSIVE_ANIMAL_METADATA_TOKEN = "AgressiveAnimal";

    void setAggressive(MobWrapper mob);

    JavaPlugin getPlugin();

    default void markAsAggressive(MobWrapper wrapper) {
        wrapper.markAggressive();
        wrapper.getBukkitEntity().setMetadata(AGGRESSIVE_ANIMAL_METADATA_TOKEN,
                new FixedMetadataValue(getPlugin(), true));
    }

    default void markAsPassive(MobWrapper wrapper) {
        wrapper.markPassive();
        wrapper.getBukkitEntity().setMetadata(AGGRESSIVE_ANIMAL_METADATA_TOKEN,
                new FixedMetadataValue(getPlugin(), false));
    }

}
