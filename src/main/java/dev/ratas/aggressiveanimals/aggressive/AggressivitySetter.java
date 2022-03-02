package dev.ratas.aggressiveanimals.aggressive;

import org.bukkit.metadata.FixedMetadataValue;

import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.slimedogcore.impl.SlimeDogCore;

public interface AggressivitySetter {
    String AGGRESSIVE_ANIMAL_METADATA_TOKEN = "AggressiveAnimal";

    void setAggressivityAttributes(TrackedMob mob);

    void setAttackingGoals(TrackedMob mob);

    void setPassive(TrackedMob mob);

    SlimeDogCore getPlugin();

    default void markAsAttacking(TrackedMob wrapper) {
        wrapper.markAttacking();
        wrapper.getBukkitEntity().setMetadata(AGGRESSIVE_ANIMAL_METADATA_TOKEN,
                new FixedMetadataValue(getPlugin(), true));
    }

    default void markAsPassive(TrackedMob wrapper) {
        wrapper.markPassive();
        wrapper.getBukkitEntity().setMetadata(AGGRESSIVE_ANIMAL_METADATA_TOKEN,
                new FixedMetadataValue(getPlugin(), false));
    }

}
