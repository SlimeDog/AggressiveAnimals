package dev.ratas.aggressiveanimals.aggressive;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.LivingEntity;

import dev.ratas.aggressiveanimals.AggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.settings.MobTypeManager;
import dev.ratas.aggressiveanimals.aggressive.settings.MobWrapper;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.config.Settings;
import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;

public class AggressivityManager {
    private final NPCHookManager npcHooks;
    private final MobTypeManager mobTypeManager;
    private final AggressivitySetter setter;
    private final Set<MobWrapper> aggressiveMobs = new HashSet<>();

    public AggressivityManager(AggressiveAnimals plugin, Settings settings, NPCHookManager npcHooks) {
        this.npcHooks = npcHooks;
        mobTypeManager = new MobTypeManager(plugin, settings);
        setter = new NMSAggressivitySetter(plugin);
    }

    public void setAppropriateAggressivity(LivingEntity entity) {
        MobTypeSettings settings = mobTypeManager.getSettings(entity.getType());
        if (settings == null) {
            throw new IllegalArgumentException(
                    "Entity of type " + entity.getType() + " is not currently managed by the plugin.");
        }
        MobWrapper wrapper = new MobWrapper(entity, settings);
        setter.setFor(wrapper);
        if (wrapper.isAggressive()) {
            aggressiveMobs.add(wrapper);
        }
    }

    public boolean isManaged(LivingEntity entity) {
        return mobTypeManager.isManaged(entity.getType());
    }

    public boolean shouldBeAggressiveAtSpawn(LivingEntity entity) {
        MobTypeSettings settings = mobTypeManager.getSettings(entity.getType());
        if (settings == null) {
            return false;
        }
        if (!settings.shouldApplyTo(entity, npcHooks)) {
            return false;
        }
        return !settings.retaliateOnly();
    }

    public boolean shouldBeAggressiveOnAttack(LivingEntity entity) {
        MobTypeSettings settings = mobTypeManager.getSettings(entity.getType());
        if (settings == null) {
            return false;
        }
        if (!settings.shouldApplyTo(entity, npcHooks)) {
            return false;
        }
        return true;
    }

}
