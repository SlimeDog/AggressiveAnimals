package dev.ratas.aggressiveanimals.hooks.npc;

import org.bukkit.entity.Entity;

public interface NPCHook {

    String getHookName();

    boolean isNPC(Entity entity);

}
