package dev.ratas.aggressiveanimals.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;

public class MobSpawnListener implements Listener {
    private final AggressivityManager aggressivityManager;

    public MobSpawnListener(AggressivityManager aggressivityManager) {
        this.aggressivityManager = aggressivityManager;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (!aggressivityManager.shouldBeAggressive(entity)) {
            return;
        }
        aggressivityManager.setAppropriateAggressivity(entity);
    }

}
