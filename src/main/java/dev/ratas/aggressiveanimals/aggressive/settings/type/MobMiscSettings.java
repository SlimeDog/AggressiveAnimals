package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.entity.LivingEntity;

import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;

public record MobMiscSettings(boolean includeNpcs, boolean targetAsNamedOnly, boolean includeTamed) {

    public boolean shouldBeAggressive(NPCHookManager npcHooks, LivingEntity mob, LivingEntity target) {
        if (!includeNpcs && npcHooks.isNPC(target)) {
            return false;
        }
        if (targetAsNamedOnly && mob.getCustomName() == null) {
            return false;
        }
        if (!includeTamed && MobType.fromBukkit(mob.getType()).isTameable()) {
            return false;
        }
        return true;
    }

}
