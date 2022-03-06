package dev.ratas.aggressiveanimals.hooks.npc;

public class NPCHookManager extends OneOfDelegateNPCHook {
    private static final String COMBINED_HOOKS = "ALL HOOKS";
    private static final NPCHook CITIZENS_HOOK = new MetadataNPCHook("Citizens", "NPC");
    private static final NPCHook SHOPKEEPER_HOOK = new MetadataNPCHook("Shopkeeper", "shopkeeper");
    private static final NPCHook INFERNAL_MOBS_HOOK = new MetadataNPCHook("InfernalMobs", "infernalMetadata");
    private static final NPCHook ELITE_MOBS_HOOK = new OneOfDelegateNPCHook("EliteMobs",
            new MetadataNPCHook("Elitemobs_NPC", "Elitemobs_NPC"), new MetadataNPCHook("Elitemob", "Elitemob"));

    public NPCHookManager() {
        super(COMBINED_HOOKS, CITIZENS_HOOK, SHOPKEEPER_HOOK, INFERNAL_MOBS_HOOK, ELITE_MOBS_HOOK);
    }

}
