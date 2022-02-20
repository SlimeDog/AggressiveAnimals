package dev.ratas.aggressiveanimals.hooks.npc;

import org.bukkit.entity.Entity;

public class OneOfDelegateNPCHook extends AbstractNPCHook {
    private NPCHook[] hooks;

    public OneOfDelegateNPCHook(String hookName, NPCHook... hooks) {
        super(hookName);
        this.hooks = hooks;
    }

    @Override
    public boolean isNPC(Entity entity) {
        for (NPCHook hook : hooks) {
            if (hook.isNPC(entity)) {
                return true;
            }
        }
        return false;
    }

}
