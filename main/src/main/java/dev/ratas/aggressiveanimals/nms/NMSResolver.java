package dev.ratas.aggressiveanimals.nms;

import dev.ratas.aggressiveanimals.IAggressiveAnimals;
import dev.ratas.aggressiveanimals.aggressive.AggressivitySetter;

public final class NMSResolver {

    private NMSResolver() {
        // private constructor
    }

    public static AggressivitySetter getSetter(IAggressiveAnimals plugin) {
        Version version = Version.fromString(plugin.getPluginInformation().getCraftBukkitPackage());
        if (version == Version.v1_18_R1) {
            return new dev.ratas.aggressiveanimals.aggressive.nms_v1_18_R1.NMSAggressivitySetter(plugin);
        } else if (version == Version.v1_18_R2) {
            return new dev.ratas.aggressiveanimals.aggressive.nms_v1_18_R2.NMSAggressivitySetter(plugin);
        } else if (version == Version.v1_19_R1) {
            return new dev.ratas.aggressiveanimals.aggressive.nms_v1_19_R1.NMSAggressivitySetter(plugin);
        } else if (version == Version.v1_19_R2) {
            return new dev.ratas.aggressiveanimals.aggressive.nms_v1_19_R2.NMSAggressivitySetter(plugin);
        } else if (version == Version.v1_19_R3) {
            return new dev.ratas.aggressiveanimals.aggressive.nms_v1_19_R3.NMSAggressivitySetter(plugin);
        } else if (version == Version.v1_20_R1) {
            return new dev.ratas.aggressiveanimals.aggressive.nms_v1_20_R1.NMSAggressivitySetter(plugin);
        }
        throw new IllegalArgumentException("Plugin version not supported: " + version);
    }

    private static enum Version {
        v1_18_R1, v1_18_R2, v1_19_R1, v1_19_R2, v1_19_R3, v1_20_R1;

        private static Version fromString(String v) {
            try {
                return Version.valueOf(v);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

}
