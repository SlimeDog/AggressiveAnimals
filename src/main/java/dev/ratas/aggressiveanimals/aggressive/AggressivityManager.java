package dev.ratas.aggressiveanimals.aggressive;

import java.util.Collection;

import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import dev.ratas.aggressiveanimals.IAggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.managed.MobWithTarget;
import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.managed.registry.GlobalRegistry;
import dev.ratas.aggressiveanimals.aggressive.managed.registry.TrackedMobRegistry;
import dev.ratas.aggressiveanimals.aggressive.reasons.AggressivityReason;
import dev.ratas.aggressiveanimals.aggressive.reasons.AttackReason;
import dev.ratas.aggressiveanimals.aggressive.reasons.ChangeReason;
import dev.ratas.aggressiveanimals.aggressive.reasons.PacificationReason;
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
    private final AggressivityMaintainer aggressivityMaintainer;
    private final GroupAggressivity groupAggressivity;

    public AggressivityManager(IAggressiveAnimals plugin, Settings settings, NPCHookManager npcHooks) {
        this.npcHooks = npcHooks;
        this.plugin = plugin;
        mobTypeManager = new MobTypeManager(plugin, settings);
        setter = new NMSAggressivitySetter(plugin);
        this.aggressivityMaintainer = new AggressivityMaintainer(this);
        this.groupAggressivity = new GroupAggressivity(this);
        this.registry = new GlobalRegistry(groupAggressivity);
        plugin.getScheduler().runTaskTimer(aggressivityMaintainer, PASSIFIER_PERIOD, PASSIFIER_PERIOD);
        plugin.getScheduler().runTaskTimer(groupAggressivity, GROUP_AGGRESSION_PERIOD, GROUP_AGGRESSION_PERIOD);
    }

    public void register(Mob mob, MobTypeSettings settings, AggressivityReason reason) {
        TrackedMob tracked = registry.register(mob, settings);
        setAggressivityAttributes(tracked, reason);
        if (shouldBeAggressiveAtSpawn(tracked)) {
            attemptAttacking(mob, null, AttackReason.AGGRESSIVE_AT_SPAWN);
        }
    }

    public void unregister(Mob mob, MobTypeSettings settings, PacificationReason reason) {
        setPassive(registry.getTrackedMob(mob), reason);
        registry.unregister(mob, settings);
    }

    private void setAggressivityAttributes(TrackedMob tracked, AggressivityReason reason) {
        plugin.getDebugLogger()
                .log("Setting aggressivity attributes for: " + tracked.getSettings().entityType() + " -> "
                        + tracked.getBukkitEntity().getEntityId() + " because of " + reason);
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
            plugin.getDebugLogger()
                    .log("No wrapper when attempting to attack: " + entity.getType() + " -> " + entity.getEntityId());
            setAggressivityAttributes(wrapper, AggressivityReason.ATTACK);
            wrapper = registry.getTrackedMob(entity);
        }
        if (wrapper.isAttacking()) {
            if (wrapper.getTarget() == target) {
                plugin.getDebugLogger().log("Attempting to mark mob attacking for the same target again");
                return; // already with same target
            }
            plugin.getDebugLogger().log("New attack target specified");
        }
        plugin.getDebugLogger()
                .log("Attempting to set attacking: " + wrapper.getSettings().entityType() + " -> "
                        + entity.getEntityId() + " because " + reason + " (target " + target + ")");
        if (settings.shouldAttack(entity, target)) {
            setter.setAttackingGoals(wrapper);
            wrapper.markAttacking(target, reason != AttackReason.GROUP_AGGRESSION);
            plugin.getDebugLogger().log("The mob is now attacking: " + wrapper.getSettings().entityType() + " -> "
                    + entity.getEntityId() + " -> " + entity.getTarget());
        }
    }

    public void stopTracking(TrackedMob mob, StopTrackingReason reason) {
        plugin.getDebugLogger().log("Stopping tracking: " + mob.getSettings().entityType() + " -> "
                + mob.getBukkitEntity().getEntityId() + " becaause " + reason);
        registry.unregister(mob);
    }

    public void stopAttacking(TrackedMob mob, ChangeReason reason) {
        plugin.getDebugLogger().log("Stopping attacking for: " + mob.getSettings().entityType() + " -> "
                + mob.getBukkitEntity().getEntityId() + " because of " + reason);
        mob.markNotAttacking();
    }

    public void resetTarget(TrackedMob mob) {
        if (mob.resetTarget()) {
            plugin.getDebugLogger().log("Reset target for: " + mob.getSettings().entityType() + " -> "
                    + mob.getBukkitEntity().getEntityId() + " (ruitenely)");
            groupAggressivity.checkMob(mob);
        }
    }

    public void setPassive(TrackedMob mob, PacificationReason reason) {
        plugin.getDebugLogger().log("Setting passive: " + mob.getSettings().entityType() + " -> "
                + mob.getBukkitEntity().getEntityId() + " because of " + reason);
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
        unregisterAll(PacificationReason.RELOAD_PLUGIN);
        registry.clear();
        mobTypeManager.reload(settings);
    }

    public MobTypeManager getMobTypeManager() {
        return mobTypeManager;
    }

    public void untargetPlayer(Player player) {
        MobWithTarget.removeTarget(player);
    }

    public Player getRegisteredTarget(TrackedMob mob) {
        return mob.getTarget();
    }

    public void unregisterAll(PacificationReason reason) {
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
