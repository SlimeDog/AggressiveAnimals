package dev.ratas.aggressiveanimals.aggressive;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public interface AggressivitySetter {
    String AGGRESSIVE_ANIMAL_METADATA_TOKEN = "AggressiveAnimal";

    void setAggressivityAttributes(MobWrapper mob);

    void setAttackingGoals(MobWrapper mob);

    void setPassive(MobWrapper mob);

    JavaPlugin getPlugin();

    default void markAsAttacking(MobWrapper wrapper) {
        wrapper.markAttacking();
        wrapper.getBukkitEntity().setMetadata(AGGRESSIVE_ANIMAL_METADATA_TOKEN,
                new FixedMetadataValue(getPlugin(), true));
    }

    default void markAsPassive(MobWrapper wrapper) {
        wrapper.markPassive();
        wrapper.getBukkitEntity().setMetadata(AGGRESSIVE_ANIMAL_METADATA_TOKEN,
                new FixedMetadataValue(getPlugin(), false));
    }

}
