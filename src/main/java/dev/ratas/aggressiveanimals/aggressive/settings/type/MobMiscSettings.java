package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.entity.Fox;
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
        if (includeTamed) {
            // be aggressive regardless
            // this should not be the case if the mob is not tameable
            // an exeptio nshould be thrown at build time in this case
            return true;
        }
        // should ignore tamed
        if (mob instanceof Fox fox) {
            boolean hasTrusted = fox.getFirstTrustedPlayer() != null || fox.getSecondTrustedPlayer() != null;
            return !hasTrusted;
        } else if (!(mob instanceof Tameable)) {
            return true;
        }
        Tameable tameable = (Tameable) mob;
        return !tameable.isTamed();
    }

}
