package dev.ratas.aggressiveanimals.aggressive;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    private final Set<MobWrapper> aggressiveMobs = new HashSet<>();
    private final Passifier passifier;

    public AggressivityManager(AggressiveAnimals plugin, Settings settings, NPCHookManager npcHooks) {
        this.npcHooks = npcHooks;
        this.plugin = plugin;
        mobTypeManager = new MobTypeManager(plugin, settings);
        setter = new NMSAggressivitySetter(plugin);
        this.passifier = new Passifier(this, Collections.emptyList());
        plugin.getServer().getScheduler().runTaskTimer(plugin, passifier, PASSIFIER_PERIOD, PASSIFIER_PERIOD);
    }

    public void setAppropriateAggressivity(Mob entity) {
        MobTypeSettings settings = mobTypeManager.getSettings(entity.getType());
        if (settings == null) {
            throw new IllegalArgumentException(
                    "Entity of type " + entity.getType() + " is not currently managed by the plugin.");
        }
        MobWrapper wrapper = new MobWrapper(entity, settings);
        plugin.debug("Attempting to set aggressive: " + entity);
        setter.setAggressive(wrapper);
        if (wrapper.isAggressive()) {
            plugin.debug("The mob is now aggressive: " + entity);
            passifier.addTrackableMob(wrapper);
            aggressiveMobs.add(wrapper);
        }
    }

    public void setPassive(MobWrapper mob) {
        plugin.debug("Setting passive: " + mob.getBukkitEntity());
        setter.setPassive(mob);
        aggressiveMobs.remove(mob);
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
