package dev.ratas.aggressiveanimals.aggressive;

import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.slimedogcore.impl.SlimeDogCore;

public interface AggressivitySetter {
    public static double SIZE_WHEN_AGGRO = 1.25;

    void setAggressivityAttributes(TrackedMob mob);

    void setAttackingGoals(TrackedMob mob);

    void removeAttackingGoals(TrackedMob mob);

    void stopTracking(TrackedMob mob);

    SlimeDogCore getPlugin();

}
