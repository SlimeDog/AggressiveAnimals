package dev.ratas.aggressiveanimals.aggressive.timers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.aggressive.managed.TrackedMob;
import dev.ratas.aggressiveanimals.aggressive.reasons.ChangeReason;

public class Passifier implements Runnable {
    private final AggressivityManager aggressivityManager;
    private final Set<TrackedMob> checkableMobs;

    public Passifier(AggressivityManager aggressivityManager, Collection<TrackedMob> checkableMobs) {
        this.aggressivityManager = aggressivityManager;
        this.checkableMobs = new HashSet<>(checkableMobs);
    }

    public void addTrackableMob(TrackedMob mob) {
        checkableMobs.add(mob);
    }

    @Override
    public void run() {
        for (TrackedMob mob : new HashSet<>(checkableMobs)) {
            ChangeReason reason = mob.getSettings().shouldStopAttacking(mob);
            if (reason != null) {
                aggressivityManager.stopAttacking(mob, reason);
                checkableMobs.remove(mob);
            }
            if (!mob.isLoaded()) {
                aggressivityManager.stopTracking(mob);
                checkableMobs.remove(mob);
            }
        }
    }

    public void reload() {
        checkableMobs.clear();
    }

}
