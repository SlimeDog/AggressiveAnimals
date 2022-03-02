package dev.ratas.aggressiveanimals.aggressive;

import java.util.Collections;

import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.AggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.managed.registry.GlobalRegistry;
import dev.ratas.aggressiveanimals.aggressive.managed.registry.TrackedMobRegistry;
import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.aggressive.settings.MobTypeManager;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.aggressive.timers.GroupAggressivity;
import dev.ratas.aggressiveanimals.aggressive.timers.NearDeathChecker;
import dev.ratas.aggressiveanimals.aggressive.timers.Passifier;
import dev.ratas.aggressiveanimals.config.Settings;
import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;

public class AggressivityManager {
    private static final long PASSIFIER_PERIOD = 10L; // TODO - make configurable?
    private static final long GROUP_AGGRESSION_PERIOD = 40L; // TODO - make configurable?
    private static final long NEAR_DEATH_PERIOD = 10L; // TODO - make configurable?
    private final AggressiveAnimals plugin;
    private final NPCHookManager npcHooks;
    private final MobTypeManager mobTypeManager;
    private final AggressivitySetter setter;
    private final TrackedMobRegistry registry = new GlobalRegistry();
    private final Passifier passifier;
    private final GroupAggressivity groupAggressivity;
    private final NearDeathChecker nearDeathChecker;

    public AggressivityManager(AggressiveAnimals plugin, Settings settings, NPCHookManager npcHooks) {
        this.npcHooks = npcHooks;
        this.plugin = plugin;
        mobTypeManager = new MobTypeManager(plugin, settings);
        setter = new NMSAggressivitySetter(plugin);
        this.passifier = new Passifier(this, Collections.emptyList());
        this.groupAggressivity = new GroupAggressivity(this, Collections.emptyList());
        this.nearDeathChecker = new NearDeathChecker(plugin, Collections.emptyList());
        plugin.getServer().getScheduler().runTaskTimer(plugin, passifier, PASSIFIER_PERIOD, PASSIFIER_PERIOD);
        plugin.getServer().getScheduler().runTaskTimer(plugin, groupAggressivity, GROUP_AGGRESSION_PERIOD,
                GROUP_AGGRESSION_PERIOD);
        plugin.getServer().getScheduler().runTaskTimer(plugin, nearDeathChecker,
                NEAR_DEATH_PERIOD + NEAR_DEATH_PERIOD / 2, NEAR_DEATH_PERIOD);
    }

    public void register(Mob mob, MobTypeSettings settings, AggressivityReason reason) {
        TrackedMob tracked = registry.register(mob, settings);
        setAggressivityAttributes(tracked, reason);
    }

    public void unregister(Mob mob, MobTypeSettings settings, PassifyReason reason) {
        setPassive(registry.getTrackedMob(mob), reason);
        registry.unregister(mob, settings);
    }

    private void setAggressivityAttributes(TrackedMob tracked, AggressivityReason reason) {
        plugin.debug("Setting aggressivity attributes for: " + tracked.getBukkitEntity() + " because of " + reason);
        setter.setAggressivityAttributes(tracked);
    }

    public void attemptAttacking(Mob entity, Player target, AttackReason reason) {
        MobTypeSettings settings = mobTypeManager.getEnabledSettings((MobType.fromBukkit(entity.getType())));
        if (settings == null) {
            throw new IllegalArgumentException(
                    "Entity of type " + entity.getType() + " is not currently managed by the plugin.");
        }
        TrackedMob wrapper = registry.getTrackedMob(entity);
        if (wrapper == null) {
            plugin.debug("No wrapper when attempting to attack: " + entity);
            setAggressivityAttributes(wrapper, AggressivityReason.ATTACK);
            wrapper = registry.getTrackedMob(entity);
        }
        if (wrapper.isAttacking()) {
            plugin.debug("Attempting to mark mob attacking while it is already doing so");
            return;
        }
        plugin.debug("Attempting to set attacking: " + entity + " because " + reason);
        if (settings.shouldAttack(entity, target)) {
            setter.setAttackingGoals(wrapper);
            entity.setTarget(target);
            plugin.debug("The mob is now attacking: " + entity + " -> " + entity.getTarget());
            passifier.addTrackableMob(wrapper);
            groupAggressivity.addTrackableMob(wrapper);
        }
    }

    public void stopTracking(TrackedMob mob) {
        plugin.debug("Stopping tracking: " + mob.getBukkitEntity());
        registry.unregister(mob);
    }

    public void stopAttacking(TrackedMob mob, ChangeReason reason) {
        plugin.debug("Stopping attacking for: " + mob.getBukkitEntity());
        registry.markNotAttacking(mob);
    }

    public void setPassive(TrackedMob mob, PassifyReason reason) {
        plugin.debug("Setting passive: " + mob.getBukkitEntity() + " because of " + reason);
        setter.setPassive(mob);
    }

    public boolean isManaged(Mob entity) {
        return mobTypeManager.isManaged(MobType.fromBukkit(entity.getType()));
    }

    public boolean shouldBeAggressiveAtSpawn(Mob entity) {
        MobTypeSettings settings = mobTypeManager.getEnabledSettings((MobType.fromBukkit(entity.getType())));
        if (settings == null) {
            return false;
        }
        if (!settings.shouldApplyTo(entity, null, npcHooks)) {
            return false;
        }
        return settings.alwaysAggressive();
    }

    public boolean shouldBeAggressiveOnAttack(Mob entity, Player target) {
        MobTypeSettings settings = mobTypeManager.getEnabledSettings((MobType.fromBukkit(entity.getType())));
        if (settings == null) {
            return false;
        }
        if (!settings.shouldApplyTo(entity, target, npcHooks)) {
            return false;
        }
        return true;
    }

    public void reload(Settings settings) {

        registry.clear();
        mobTypeManager.reload(settings);
        passifier.reload();
    }

    public MobTypeManager getMobTypeManager() {
        return mobTypeManager;
    }

    public void untargetPlayer(Player player) {
        registry.stopAttacking(player);
    }

}
