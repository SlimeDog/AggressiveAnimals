package dev.ratas.aggressiveanimals.hooks.npc;

import org.bukkit.entity.Entity;

public class MetadataNPCHook extends AbstractNPCHook {
    private final String metadataName;

    public MetadataNPCHook(String hookName, String metadataName) {
        super(hookName);
        this.metadataName = metadataName;
    }

    public boolean hasMetaData(Entity entity) {
        if (entity == null) {
            return false;
        }
        return entity.hasMetadata(metadataName);
    }

    @Override
    public boolean isNPC(Entity entity) {
        return hasMetaData(entity);
    }

}
