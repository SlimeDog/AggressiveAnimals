package dev.ratas.aggressiveanimals.aggressive;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.AggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.settings.MobTypeManager;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.aggressive.timers.Passifier;
import dev.ratas.aggressiveanimals.config.Settings;
import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;

public class AggressivityManager {
    private static final long PASSIFIER_PERIOD = 10L; // TODO - make configurable?
    private final AggressiveAnimals plugin;
    private final NPCHookManager npcHooks;
    private final MobTypeManager mobTypeManager;
    private final AggressivitySetter setter;
    private final Map<Mob, MobWrapper> trackedMobs = new HashMap<>();
    private final Passifier passifier;

    public AggressivityManager(AggressiveAnimals plugin, Settings settings, NPCHookManager npcHooks) {
        this.npcHooks = npcHooks;
        this.plugin = plugin;
        mobTypeManager = new MobTypeManager(plugin, settings);
        setter = new NMSAggressivitySetter(plugin);
        this.passifier = new Passifier(this, Collections.emptyList());
        plugin.getServer().getScheduler().runTaskTimer(plugin, passifier, PASSIFIER_PERIOD, PASSIFIER_PERIOD);
    }

    public void setAggressivityAttributes(Mob entity) {
        MobTypeSettings settings = mobTypeManager.getSettings(entity.getType());
        if (settings == null) {
            throw new IllegalArgumentException(
                    "Entity of type " + entity.getType() + " is not currently managed by the plugin.");
        }
        MobWrapper wrapper = trackedMobs.computeIfAbsent(entity, e -> new MobWrapper(e, settings));
        plugin.debug("Setting aggressivity attributes for: " + entity);
        setter.setAggressivityAttributes(wrapper);
    }

    public void attemptAttacking(Mob entity, Player target) {
        MobTypeSettings settings = mobTypeManager.getSettings(entity.getType());
        if (settings == null) {
            throw new IllegalArgumentException(
                    "Entity of type " + entity.getType() + " is not currently managed by the plugin.");
        }
        MobWrapper wrapper = trackedMobs.get(entity);
        if (wrapper == null) {
            plugin.debug("No wrapper when attempting to attack: " + entity);
            setAggressivityAttributes(entity);
            wrapper = trackedMobs.get(entity);
        }
        plugin.debug("Attempting to set attacking: " + entity);
        if (settings.shouldAttack(entity, target)) {
            setter.setAttackingGoals(wrapper);
            entity.setTarget(target);
            plugin.debug("The mob is now attacking: " + entity + " -> " + entity.getTarget());
            passifier.addTrackableMob(wrapper);
        }
    }

    public void stopTracking(MobWrapper mob) {
        plugin.debug("Stopping tracking: " + mob.getBukkitEntity());
        trackedMobs.remove(mob.getBukkitEntity());
    }

    public void setPassive(MobWrapper mob, ChangeReason reason) {
        plugin.debug("Setting passive: " + mob.getBukkitEntity() + " because of " + reason);
        setter.setPassive(mob);
    }

    public boolean isManaged(Mob entity) {
        return mobTypeManager.isManaged(entity.getType());
    }

    public boolean shouldBeAggressiveAtSpawn(Mob entity) {
        MobTypeSettings settings = mobTypeManager.getSettings(entity.getType());
        if (settings == null) {
            return false;
        }
        if (!settings.shouldApplyTo(entity, null, npcHooks)) {
            return false;
        }
        return !settings.retaliateOnly();
    }

    public boolean shouldBeAggressiveOnAttack(Mob entity, Player target) {
        MobTypeSettings settings = mobTypeManager.getSettings(entity.getType());
        if (settings == null) {
            return false;
        }
        if (!settings.shouldApplyTo(entity, target, npcHooks)) {
            return false;
        }
        return true;
    }

}
