package dev.ratas.aggressiveanimals.aggressive;

import org.bukkit.entity.LivingEntity;

import dev.ratas.aggressiveanimals.AggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.settings.MobTypeManager;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;

public class AggressivityManager {
    private final MobTypeManager mobTypeManager;
    private final AggressivitySetter setter;

    public AggressivityManager(AggressiveAnimals plugin) {
        mobTypeManager = new MobTypeManager(plugin, plugin.getSettings());
        setter = new NMSAggressivitySetter(plugin);
    }

    public void setAppropriateAggressivity(LivingEntity entity) {
        MobTypeSettings settings = mobTypeManager.getSettings(entity.getType());
        if (settings == null) {
            throw new IllegalArgumentException(
                    "Entity of type " + entity.getType() + " is not currently managed by the plugin.");
        }
        setter.setFor(settings, entity);
    }

    public boolean isManaged(LivingEntity entity) {
        return mobTypeManager.isManaged(entity.getType());
    }

}
