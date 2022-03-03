package dev.ratas.aggressiveanimals.aggressive;

import java.util.Collection;

import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.IAggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.managed.registry.GlobalRegistry;
import dev.ratas.aggressiveanimals.aggressive.managed.registry.TrackedMobRegistry;
import dev.ratas.aggressiveanimals.aggressive.reasons.AggressivityReason;
import dev.ratas.aggressiveanimals.aggressive.reasons.AttackReason;
import dev.ratas.aggressiveanimals.aggressive.reasons.ChangeReason;
import dev.ratas.aggressiveanimals.aggressive.reasons.PassifyReason;
import dev.ratas.aggressiveanimals.aggressive.reasons.StopTrackingReason;
import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.aggressive.settings.MobTypeManager;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.aggressive.timers.GroupAggressivity;
import dev.ratas.aggressiveanimals.aggressive.timers.AggressivityMaintainer;
import dev.ratas.aggressiveanimals.config.Settings;
import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;

public class AggressivityManager {
    private static final long PASSIFIER_PERIOD = 10L; // TODO - make configurable?
    private static final long GROUP_AGGRESSION_PERIOD = 40L; // TODO - make configurable?
    private final IAggressiveAnimals plugin;
    private final NPCHookManager npcHooks;
    private final MobTypeManager mobTypeManager;
    private final AggressivitySetter setter;
    private final TrackedMobRegistry registry;
    private final AggressivityMaintainer passifier;
    private final GroupAggressivity groupAggressivity;

    public AggressivityManager(IAggressiveAnimals plugin, Settings settings, NPCHookManager npcHooks) {
        this.npcHooks = npcHooks;
        this.plugin = plugin;
        mobTypeManager = new MobTypeManager(plugin, settings);
        setter = new NMSAggressivitySetter(plugin);
        this.passifier = new AggressivityMaintainer(this);
        this.groupAggressivity = new GroupAggressivity(this);
        this.registry = new GlobalRegistry(groupAggressivity);
        plugin.getScheduler().runTaskTimer(passifier, PASSIFIER_PERIOD, PASSIFIER_PERIOD);
        plugin.getScheduler().runTaskTimer(groupAggressivity, GROUP_AGGRESSION_PERIOD, GROUP_AGGRESSION_PERIOD);
    }

    public void register(Mob mob, MobTypeSettings settings, AggressivityReason reason) {
        TrackedMob tracked = registry.register(mob, settings);
        setAggressivityAttributes(tracked, reason);
        if (shouldBeAggressiveAtSpawn(tracked)) {
            attemptAttacking(mob, null, AttackReason.AGGRESSIVE_AT_SPAWN);
        }
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
            registry.markAttacking(wrapper, target, reason != AttackReason.GROUP_AGGRESSION);
            plugin.debug("The mob is now attacking: " + entity + " -> " + entity.getTarget());
        }
    }

    public void stopTracking(TrackedMob mob, StopTrackingReason reason) {
        plugin.debug("Stopping tracking: " + mob.getBukkitEntity() + " becaause " + reason);
        registry.unregister(mob);
    }

    public void stopAttacking(TrackedMob mob, ChangeReason reason) {
        plugin.debug("Stopping attacking for: " + mob.getBukkitEntity() + " because of " + reason);
        registry.markNotAttacking(mob);
    }

    public void resetTarget(TrackedMob mob) {
        if (registry.resetTarget(mob)) {
            plugin.debug("Reset target for: " + mob.getBukkitEntity() + " (ruitenely)");
            groupAggressivity.checkMob(mob);
        }
    }

    public void setPassive(TrackedMob mob, PassifyReason reason) {
        plugin.debug("Setting passive: " + mob.getBukkitEntity() + " because of " + reason);
        setter.setPassive(mob);
    }

    public boolean isManaged(Mob entity) {
        return mobTypeManager.isManaged(MobType.fromBukkit(entity.getType()));
    }

    public boolean shouldBeAggressiveAtSpawn(TrackedMob tracked) {
        Mob entity = tracked.getBukkitEntity();
        MobTypeSettings settings = tracked.getSettings();
        if (!settings.shouldAttack(entity, null, npcHooks)) {
            return false;
        }
        return settings.alwaysAggressive();
    }

    public boolean shouldBeAggressiveOnAttack(Mob entity, Player target) {
        MobTypeSettings settings = mobTypeManager.getEnabledSettings((MobType.fromBukkit(entity.getType())));
        if (settings == null) {
            return false;
        }
        if (!settings.shouldAttack(entity, target, npcHooks)) {
            return false;
        }
        return true;
    }

    public void reload(Settings settings) {
        unregisterAll(PassifyReason.RELOAD_PLUGIN);
        registry.clear();
        mobTypeManager.reload(settings);
    }

    public MobTypeManager getMobTypeManager() {
        return mobTypeManager;
    }

    public void untargetPlayer(Player player) {
        registry.stopAttacking(player);
    }

    public Player getRegisteredTarget(TrackedMob mob) {
        return registry.getTargetOf(mob);
    }

    public void unregisterAll(PassifyReason reason) {
        for (TrackedMob mob : registry.getAllTrackedMobs()) {
            unregister(mob.getBukkitEntity(), mob.getSettings(), reason);
        }
    }

    public Collection<TrackedMob> getAllTrackedMobs() {
        return registry.getAllTrackedMobs();
    }

    public TrackedMobRegistry getRegistry() {
        return registry;
    }

}
