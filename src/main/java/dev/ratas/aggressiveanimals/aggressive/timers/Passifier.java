package dev.ratas.aggressiveanimals.aggressive.timers;

import java.util.HashSet;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.reasons.ChangeReason;
import dev.ratas.aggressiveanimals.aggressive.reasons.StopTrackingReason;

public class Passifier implements Runnable {
    private final AggressivityManager aggressivityManager;

    public Passifier(AggressivityManager aggressivityManager) {
        this.aggressivityManager = aggressivityManager;
    }

    @Override
    public void run() {
        for (TrackedMob mob : new HashSet<>(aggressivityManager.getAllTrackedMobs())) {
            ChangeReason reason = mob.getSettings().shouldStopAttacking(mob);
            if (reason != null) {
                aggressivityManager.stopAttacking(mob, reason);
            }
            if (!mob.isLoaded()) {
                aggressivityManager.stopTracking(mob, StopTrackingReason.UNLOADED);
            }
        }
    }

}
