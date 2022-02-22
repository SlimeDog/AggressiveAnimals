package dev.ratas.aggressiveanimals.aggressive;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.bukkit.entity.Mob;

import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;

public class MobWrapper {
    private final Mob bukkitEntity;
    private final MobTypeSettings settings;
    private boolean isAggressive = false;
    private final Set<Object> goals = new HashSet<>();

    public MobWrapper(Mob bukkitEntity, MobTypeSettings settings) {
        this.bukkitEntity = bukkitEntity;
        this.settings = settings;
    }

    public Mob getBukkitEntity() {
        return bukkitEntity;
    }

    public MobTypeSettings getSettings() {
        return settings;
    }

    public boolean isAggressive() {
        return isAggressive;
    }

    public void markAggressive() {
        isAggressive = true;
    }

    public void markPassive() {
        isAggressive = false;
    }

    public boolean isLoaded() {
        return bukkitEntity.isValid() && !bukkitEntity.isDead()
                && bukkitEntity.getLocation().getChunk().isLoaded();
    }

    Set<Object> getGoals() {
        return goals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bukkitEntity, settings);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MobWrapper)) {
            return false;
        }
        MobWrapper o = (MobWrapper) other;
        return bukkitEntity.equals(o.bukkitEntity) && settings.equals(o.settings);
    }

}