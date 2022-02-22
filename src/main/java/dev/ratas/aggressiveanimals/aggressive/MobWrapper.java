package dev.ratas.aggressiveanimals.aggressive;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.bukkit.entity.Mob;

import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;

public class MobWrapper {
    private static final long MIN_ATTACK_MS = 10 * 1000L; // aggressive for at least 10 seconds // TODO - configurable
    private final Mob bukkitEntity;
    private final MobTypeSettings settings;
    private boolean isAttacking = false;
    private final Set<Object> goals = new HashSet<>();
    private Object savedAttributes;
    private long startedAttacking = 0L;

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

    public boolean isAttacking() {
        return isAttacking;
    }

    public void markAttacking() {
        isAttacking = true;
        startedAttacking = System.currentTimeMillis();
    }

    public boolean hasOutlivedAggression() {
        return System.currentTimeMillis() > startedAttacking + MIN_ATTACK_MS;
    }

    public void markPassive() {
        isAttacking = false;
    }

    public boolean isLoaded() {
        return bukkitEntity.isValid() && !bukkitEntity.isDead()
                && bukkitEntity.getLocation().getChunk().isLoaded();
    }

    Set<Object> getGoals() {
        return goals;
    }

    void setAttributes(Object attributes) {
        savedAttributes = attributes;
    }

    Object getSavedAttributes() {
        return savedAttributes;
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
