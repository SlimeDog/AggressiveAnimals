package dev.ratas.aggressiveanimals.aggressive.timers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
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
            if (shouldBePassified(mob)) {
                passify(mob);
                checkableMobs.remove(mob);
            }
        }
    }

    private boolean shouldBePassified(MobWrapper mob) {
        if (!mob.isLoaded()) {
            return true;
        }
        return mob.getSettings().shouldBePassified(mob.getBukkitEntity());
    }

    private void passify(MobWrapper mob) {
        aggressivityManager.setPassive(mob);
    }

}
