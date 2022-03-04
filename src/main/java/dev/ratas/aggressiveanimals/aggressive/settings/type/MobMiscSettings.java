package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.entity.LivingEntity;

import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;

public record MobMiscSettings(boolean includeNpcs, boolean targetAsNamedOnly) {

    public boolean shouldBeAggressive(NPCHookManager npcHooks, LivingEntity mob, LivingEntity target) {
        if (!includeNpcs && npcHooks.isNPC(target)) {
            return false;
        }
        if (targetAsNamedOnly && mob.getCustomName() == null) {
            return false;
        }
        return true;
    }

}
