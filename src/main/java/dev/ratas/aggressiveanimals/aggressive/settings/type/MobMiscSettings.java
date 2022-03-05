package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;

public record MobMiscSettings(boolean includeNpcs, boolean targetAsNamedOnly, boolean includeTamed) {

    public boolean shouldBeAggressive(NPCHookManager npcHooks, Mob mob, Player target) {
        if (!includeNpcs && npcHooks.isNPC(target)) {
            return false;
        }
        if (targetAsNamedOnly && mob.getCustomName() == null) {
            return false;
        }
        if (!shouldBeAggressiveRegardingTamability(mob)) {
            return false;
        }
        return true;
    }

    private boolean shouldBeAggressiveRegardingTamability(Mob mob) {
        if (!(mob instanceof Tameable tameable)) {
            return true;
        }
        if (includeTamed) {
            return true; // be aggressive regardless
        } else { // aggressive only if mob is NOT tamed
            return !tameable.isTamed();
        }
    }

}
