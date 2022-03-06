package dev.ratas.aggressiveanimals.listeners;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.WorldInitEvent;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.aggressive.reasons.AggressivityReason;
import dev.ratas.aggressiveanimals.aggressive.reasons.PacificationReason;
import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.slimedogcore.api.SlimeDogPlugin;

public class MobRegistrationListener implements Listener {
    private final SlimeDogPlugin plugin;
    private final AggressivityManager aggressivityManager;

    public MobRegistrationListener(SlimeDogPlugin plugin, AggressivityManager aggressivityManager) {
        this.plugin = plugin;
        this.aggressivityManager = aggressivityManager;
        registerAllExistingMobs();
    }

    private void registerAllExistingMobs() {
        for (World world : plugin.getWorldProvider().getAllWorlds()) {
            registerAllInWorld(world);
        }
    }

    private void registerAllInWorld(World world) {
        for (Entity entity : world.getEntities()) {
            attemptRegister(entity, AggressivityReason.PLUGIN_INIT);
        }
    }

    private MobTypeSettings getSettingsFor(Entity entity) {
        if (!(entity instanceof Mob)) {
            return null;
        }
        MobTypeSettings settings = aggressivityManager.getMobTypeManager()
                .getEnabledSettings(MobType.fromBukkit(((Mob) entity).getType()));
        if (settings == null)  {
            return null;
        }
        if (!settings.isApplicableAt(entity.getLocation())) {
            return null;
        }
        return settings;
    }

    private void attemptRegister(Entity entity, AggressivityReason reason) {
        MobTypeSettings settings = getSettingsFor(entity);
        if (settings == null) {
            return;
        }
        register((Mob) entity, settings, reason);
    }

    private void register(Mob mob, MobTypeSettings settings, AggressivityReason reason) {
        aggressivityManager.register(mob, settings, reason);
    }

    private void attemptUnregister(Entity entity, PacificationReason reason) {
        MobTypeSettings settings = getSettingsFor(entity);
        if (settings == null) {
            return;
        }
        unregister((Mob) entity, settings, reason);
    }

    private void unregister(Mob mob, MobTypeSettings settings, PacificationReason reason) {
        aggressivityManager.unregister(mob, settings, reason);
    }

    @EventHandler(priority = EventPriority.NORMAL) // higher priority in AggressionListener so it gets called later
    public void onSpawn(CreatureSpawnEvent event) {
        attemptRegister(event.getEntity(), AggressivityReason.SPAWN);
    }

    // TODO - different handling for Spigot and Paper?
    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            attemptRegister(entity, AggressivityReason.CHUNKLOAD);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            attemptUnregister(entity, PacificationReason.UNLOAD_ENTITY);
        }
    }

    @EventHandler
    public void worldInit(WorldInitEvent event) {
        registerAllInWorld(event.getWorld());
    }

    public void onReload() {
        registerAllExistingMobs();
    }

}
