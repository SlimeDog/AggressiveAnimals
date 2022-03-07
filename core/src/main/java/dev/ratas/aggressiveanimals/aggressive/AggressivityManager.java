package dev.ratas.aggressiveanimals.aggressive;

import java.util.Collection;

import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
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
    private static final long CHECK_MOBS_AT_ONCE = 1000L; // TODO - make configurable?
    private final IAggressiveAnimals plugin;
    private final NPCHookManager npcHooks;
    private final MobTypeManager mobTypeManager;
    private final AggressivitySetter setter;
    private final TrackedMobRegistry registry;
    private final AggressivityMaintainer aggressivityMaintainer;
    private final GroupAggressivity groupAggressivity;

    public AggressivityManager(IAggressiveAnimals plugin, Settings settings, NPCHookManager npcHooks,
            AggressivitySetter setter) {
        this.npcHooks = npcHooks;
        this.plugin = plugin;
        mobTypeManager = new MobTypeManager(plugin, settings);
        this.setter = setter;
        this.aggressivityMaintainer = new AggressivityMaintainer(this, CHECK_MOBS_AT_ONCE);
        this.groupAggressivity = new GroupAggressivity(this, CHECK_MOBS_AT_ONCE);
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
        TrackedMob tracked = registry.getTrackedMob(mob);
        if (tracked == null) {
            plugin.getLogger().warning("Unable to unregister mob " + mob.getType() + "(" + mob.getEntityId()
                    + ") (unregister reason " + reason + ") because the mob was not registered");
            return;
        }
        setPassive(tracked, reason);
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
            wrapper = registry.register(entity, settings);
            setAggressivityAttributes(wrapper, AggressivityReason.ATTACK);
        }
        if (wrapper.hasTarget()) {
            if (wrapper.getTarget() == target) {
                plugin.getDebugLogger().log("Attempting to mark mob attacking for the same target again");
                return; // already with same target
            }
            plugin.getDebugLogger().log("New attack target specified");
        }
        plugin.getDebugLogger()
                .log("Attempting to set attacking: " + wrapper.getSettings().entityType() + " -> "
                        + entity.getEntityId() + " because " + reason + " (target " + target + ")");
        if (settings.shouldAttack(entity, target, npcHooks)) {
            setter.setAttackingGoals(wrapper);
            wrapper.markAttacking(target, reason != AttackReason.GROUP_AGGRESSION);
            plugin.getDebugLogger().log("The mob is now attacking: " + wrapper.getSettings().entityType() + " -> "
                    + entity.getEntityId() + " -> " + entity.getTarget());
        }
    }

    public void stopTracking(TrackedMob mob, StopTrackingReason reason) {
        plugin.getDebugLogger().log("Stopping tracking: " + mob.getSettings().entityType() + " -> "
                + mob.getBukkitEntity().getEntityId() + " because " + reason);
        registry.unregister(mob);
        setter.stopTracking(mob);
    }

    public void stopAttacking(TrackedMob mob, ChangeReason reason) {
        plugin.getDebugLogger().log("Stopping attacking for: " + mob.getSettings().entityType() + " -> "
                + mob.getBukkitEntity().getEntityId() + " because of " + reason);
        setter.removeAttackingGoals(mob);
        mob.markAttacking(null, false);
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
        setter.stopTracking(mob);
        mob.markAttacking(null, false);
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
        return settings.alwaysAggressive().value();
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

    /**
     * Find a new target for always aggressive mobs.
     *
     * @param mob the mob in question
     * @throws IllegalArgumentException if the mob is not always aggressive
     */
    public void findNewTarget(TrackedMob mob) {
        Validate.isTrue(mob.getSettings().alwaysAggressive().value(),
                "Mobs that are not always aggressive will not look for a new target");
        Location bukkitLoc = mob.getBukkitEntity().getLocation();
        double dist = mob.getSettings().acquisitionSettings().acquisitionRange().value();
        for (Entity entity : bukkitLoc.getWorld().getNearbyEntities(bukkitLoc, dist, dist, dist,
                e -> e instanceof Player)) {
            Player player = (Player) entity;
            if (mob.getSettings().shouldAttack(mob.getBukkitEntity(), player, npcHooks)) {
                attemptAttacking(mob.getBukkitEntity(), player, AttackReason.ALWAYS_AGGRESSIVE_NEW_TARGET);
                return;
            }
        }
    }

}
