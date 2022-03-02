package dev.ratas.aggressiveanimals.aggressive.managed;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import org.bukkit.entity.Mob;

import dev.ratas.aggressiveanimals.aggressive.managed.addon.AddonType;
import dev.ratas.aggressiveanimals.aggressive.managed.addon.MobAddon;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;

public class TrackedMob {
    private static final long MIN_ATTACK_MS = 10 * 1000L; // aggressive for at least 10 seconds // TODO - configurable
    private final Mob bukkitEntity;
    private final MobTypeSettings settings;
    private boolean isAttacking = false;
    private final Map<AddonType, MobAddon> addons = new EnumMap<>(AddonType.class);
    private long startedAttacking = 0L;

    public TrackedMob(Mob bukkitEntity, MobTypeSettings settings) {
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

    public void addAddon(MobAddon addon) {
        if (addons.get(addon.getAddonType()) != null) {
            throw new IllegalArgumentException("Addon of type " + addon.getAddonType() + " already set");
        }
        addons.put(addon.getAddonType(), addon);
    }

    public void removeAddon(MobAddon addon) {
        MobAddon prev = addons.get(addon.getAddonType());
        if (prev == null) {
            throw new IllegalArgumentException("Addon of type " + addon.getAddonType() + " not set");
        }
        if (prev != addon) {
            throw new IllegalArgumentException("Duplicat addon of same type");
        }
        addons.remove(addon.getAddonType());
    }

    public boolean hasAddon(AddonType type) {
        return getAddon(type) != null;
    }

    public MobAddon getAddon(AddonType type) {
        return addons.get(type);
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
        if (!(other instanceof TrackedMob)) {
            return false;
        }
        TrackedMob o = (TrackedMob) other;
        return bukkitEntity.equals(o.bukkitEntity) && settings.equals(o.settings);
    }

}
