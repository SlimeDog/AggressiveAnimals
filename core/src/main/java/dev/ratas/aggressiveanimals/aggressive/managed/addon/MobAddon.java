package dev.ratas.aggressiveanimals.aggressive.managed.addon;

public interface MobAddon {

    /**
     * Checks if addon is carrying any infrormation.
     *
     * @return true if addon has no information, false otherwise
     */
    boolean isEmpty();

    AddonType getAddonType();

}
