package dev.ratas.aggressiveanimals.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.aggressive.AggressivityReason;
import dev.ratas.aggressiveanimals.aggressive.AttackReason;

public class MobSpawnListener implements Listener {
    private final AggressivityManager aggressivityManager;

    public MobSpawnListener(AggressivityManager aggressivityManager) {
        this.aggressivityManager = aggressivityManager;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Mob)) {
            return;
        }
        Mob mob = (Mob) entity;
        if (!aggressivityManager.isManaged(mob)) {
            return;
        }
        aggressivityManager.setAggressivityAttributes(mob, AggressivityReason.SPAWN);
        if (!aggressivityManager.shouldBeAggressiveAtSpawn(mob)) {
            return;
        }
        aggressivityManager.attemptAttacking(mob, null, AttackReason.AGGRESSIVE_AT_SPAWN);
    }

    private Player getDamagingPlayer(Entity entity) {
        if (entity instanceof Player) {
            return (Player) entity;
        }
        if (entity instanceof Projectile projectile) {
            ProjectileSource source = projectile.getShooter();
            if (source instanceof Entity soruceEntity) {
                return getDamagingPlayer(soruceEntity);
            }
            // TODO try to identify shooter of redstone or something?
            return null;
        }
        return null;
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        Entity targetEntity = event.getEntity();
        if (!(targetEntity instanceof Mob)) {
            return; // currently only managing living entities
        }
        Mob target = (Mob) targetEntity;
        if (!aggressivityManager.isManaged(target)) {
            return;
        }
        Player damagingPlayer = getDamagingPlayer(event.getDamager());
        if (damagingPlayer == null) {
            return;
        }
        if (!aggressivityManager.shouldBeAggressiveOnAttack(target, damagingPlayer)) {
            return;
        }
        aggressivityManager.attemptAttacking(target, damagingPlayer, AttackReason.RETALIATE);
    }

}
