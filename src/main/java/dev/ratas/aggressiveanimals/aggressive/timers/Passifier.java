package dev.ratas.aggressiveanimals.aggressive.timers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.aggressive.ChangeReason;
import dev.ratas.aggressiveanimals.aggressive.MobWrapper;

public class Passifier implements Runnable {
    private final AggressivityManager aggressivityManager;
    private final Set<MobWrapper> checkableMobs;

    public Passifier(AggressivityManager aggressivityManager, Collection<MobWrapper> checkableMobs) {
        this.aggressivityManager = aggressivityManager;
        this.checkableMobs = new HashSet<>(checkableMobs);
    }

    public void addTrackableMob(MobWrapper mob) {
        checkableMobs.add(mob);
    }

    @Override
    public void run() {
        for (MobWrapper mob : new HashSet<>(checkableMobs)) {
            ChangeReason reason = mob.getSettings().shouldBePassified(mob);
            if (reason != null) {
                aggressivityManager.setPassive(mob, reason);
                checkableMobs.remove(mob);
            }
            if (!mob.isLoaded()) {
                aggressivityManager.stopTracking(mob);
            }
        }
    }

}
