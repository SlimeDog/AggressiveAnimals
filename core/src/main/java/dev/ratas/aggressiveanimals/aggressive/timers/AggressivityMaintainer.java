package dev.ratas.aggressiveanimals.aggressive.timers;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.reasons.ChangeReason;
import dev.ratas.aggressiveanimals.aggressive.reasons.StopTrackingReason;

public class AggressivityMaintainer extends AbstractQueuedRunnable<TrackedMob> {
    private final AggressivityManager aggressivityManager;

    public AggressivityMaintainer(AggressivityManager aggressivityManager, long processAtOnce) {
        super(processAtOnce, () -> aggressivityManager.getAllTrackedMobs());
        this.aggressivityManager = aggressivityManager;
    }

    @Override
    public void process(TrackedMob mob) {
        if (!mob.isLoaded()) {
            aggressivityManager.stopTracking(mob, StopTrackingReason.UNLOADED);
            return;
        }
        if (!mob.hasAttackingGoals()) {
            return;
        }
        ChangeReason reason = mob.getSettings().shouldStopAttacking(mob);
        if (reason != null) {
            aggressivityManager.stopAttacking(mob, reason);
        } else if (mob.getTarget() == null) {
            aggressivityManager.findNewTarget(mob);
        }
    }

}
