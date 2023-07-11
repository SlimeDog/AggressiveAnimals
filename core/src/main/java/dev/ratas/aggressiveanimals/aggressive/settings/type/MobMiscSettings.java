package dev.ratas.aggressiveanimals.aggressive.settings.type;

import org.bukkit.entity.Fox;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;

public record MobMiscSettings(Setting<Boolean> includeNpcs, Setting<Boolean> includeNamedMobs,
        Setting<Boolean> includeTamed, Setting<Boolean> protectTeamMembers) implements CheckingSettingBoundle {

    private static final LazyPluginGetter PLUGIN_GETTER = new LazyPluginGetter();

    public boolean shouldBeAggressive(NPCHookManager npcHooks, Mob mob, Player target) {
        if (!includeNpcs.value() && npcHooks.isNPC(target)) {
            return false;
        }
        if (!includeNamedMobs.value() && mob.getCustomName() != null) {
            return false;
        }
        if (!shouldBeAggressiveRegardingTamability(mob)) {
            return false;
        }
        if (!canAttackRegardingTeam(mob, target)) {
            return false;
        }
        return true;
    }

    public boolean canAttackRegardingTeam(Mob mob, Player target) {
        if (!protectTeamMembers.value()) {
            // don't need to worry about protecting team member
            return true;
        }
        return !isSameTeam(mob, target);
    }

    public boolean isSameTeam(Mob mob, Player target) {
        // Get main scoreboard?
        // Scoreboard sb = target.getScoreboard();
        if (target == null) {
            return false;
        }
        Scoreboard sb = PLUGIN_GETTER.get().getServer().getScoreboardManager().getMainScoreboard();
        Team mobTeam = sb.getEntryTeam(mob.getUniqueId().toString());
        Team targetTeam = sb.getEntryTeam(target.getName());
        if (mobTeam == null) {
            return false;
        }
        return mobTeam.equals(targetTeam);
    }

    private boolean shouldBeAggressiveRegardingTamability(Mob mob) {
        if (includeTamed.value()) {
            // be aggressive regardless
            // this should not be the case if the mob is not tameable
            // an exeptio nshould be thrown at build time in this case
            return true;
        }
        // should ignore tamed
        if (mob instanceof Fox fox) {
            boolean hasTrusted = fox.getFirstTrustedPlayer() != null || fox.getSecondTrustedPlayer() != null;
            return !hasTrusted;
        } else if (mob instanceof Ocelot ocelot) {
            return !ocelot.isTrusting();
        } else if (!(mob instanceof Tameable)) {
            return true;
        }
        Tameable tameable = (Tameable) mob;
        return !tameable.isTamed();
    }

    @Override
    public void checkAllTypes() throws IllegalStateException {
        checkType(includeNpcs, Boolean.class);
        checkType(includeNamedMobs, Boolean.class);
        checkType(includeTamed, Boolean.class);
    }

    private static final class LazyPluginGetter {
        private JavaPlugin plugin;

        public JavaPlugin get() {
            if (this.plugin == null) {
                loadPlugin();
            }
            return plugin;
        }

        private void loadPlugin() {
            plugin = JavaPlugin.getProvidingPlugin(MobMiscSettings.class);
        }
    }

}
