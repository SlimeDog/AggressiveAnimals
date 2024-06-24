package dev.ratas.aggressiveanimals.nms;

import dev.ratas.aggressiveanimals.IAggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.AggressivitySetter;

public final class NMSResolver {

    private NMSResolver() {
        // private constructor
    }

    public static AggressivitySetter getSetter(IAggressiveAnimals plugin) {
        String rawVersion = plugin.getPluginInformation().getCraftBukkitFullPackage();
        Version version = Version.fromString(rawVersion);
        if (version == Version.v1_18_R2) {
            return new dev.ratas.aggressiveanimals.aggressive.nms_v1_18_R2.NMSAggressivitySetter(plugin);
        } else if (version == Version.v1_19_R3) {
            return new dev.ratas.aggressiveanimals.aggressive.nms_v1_19_R3.NMSAggressivitySetter(plugin);
        } else if (version == Version.v1_20_R1) {
            return new dev.ratas.aggressiveanimals.aggressive.nms_v1_20_R1.NMSAggressivitySetter(plugin);
        } else if (version == Version.v1_20_R2) {
            return new dev.ratas.aggressiveanimals.aggressive.nms_v1_20_R2.NMSAggressivitySetter(plugin);
        } else if (version == Version.v1_20_R3) {
            return new dev.ratas.aggressiveanimals.aggressive.nms_v1_20_R3.NMSAggressivitySetter(plugin);
        }
        // last resort - try Paper implementation
        try {
            return new dev.ratas.aggressiveanimals.aggressive.nms_paper.NMSAggressivitySetter(plugin);
        } catch (Exception e) {
            // TODO
        }
        throw new IllegalArgumentException("Plugin version not supported: " + rawVersion);
    }

    private static enum Version {
        v1_18_R2, v1_19_R3, v1_20_R1, v1_20_R2, v1_20_R3;

        private static Version fromString(String v) {
            try {
                return Version.valueOf(v);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

}
