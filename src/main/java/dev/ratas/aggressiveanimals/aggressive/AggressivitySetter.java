package dev.ratas.aggressiveanimals.aggressive;

import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.slimedogcore.impl.SlimeDogCore;

public interface AggressivitySetter {

    void setAggressivityAttributes(TrackedMob mob);

    void setAttackingGoals(TrackedMob mob);

    void setPassive(TrackedMob mob);

    SlimeDogCore getPlugin();

}
