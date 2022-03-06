package dev.ratas.aggressiveanimals.hooks.npc;

public abstract class AbstractNPCHook implements NPCHook {
    private final String hookName;

    protected AbstractNPCHook(String hookName) {
        this.hookName = hookName;
    }

    public String getHookName() {
        return hookName;
    }

}
