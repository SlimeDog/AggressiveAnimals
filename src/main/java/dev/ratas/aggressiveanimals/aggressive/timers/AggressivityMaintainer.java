package dev.ratas.aggressiveanimals.aggressive.timers;

import java.util.HashSet;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.reasons.ChangeReason;
import dev.ratas.aggressiveanimals.aggressive.reasons.StopTrackingReason;

public class AggressivityMaintainer implements Runnable {
    private final AggressivityManager aggressivityManager;

    public AggressivityMaintainer(AggressivityManager aggressivityManager) {
        this.aggressivityManager = aggressivityManager;
    }

    @Override
    public void run() {
        for (TrackedMob mob : new HashSet<>(aggressivityManager.getAllTrackedMobs())) {
            if (!mob.isLoaded()) {
                aggressivityManager.stopTracking(mob, StopTrackingReason.UNLOADED);
                continue;
            }
            if (!mob.hasAttackingGoals()) {
                continue;
            }
            ChangeReason reason = mob.getSettings().shouldStopAttacking(mob);
            if (reason != null) {
                aggressivityManager.stopAttacking(mob, reason);
            } else {
                aggressivityManager.resetTarget(mob);
            }
        }
    }

}
