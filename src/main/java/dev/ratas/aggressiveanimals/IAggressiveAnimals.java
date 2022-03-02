package dev.ratas.aggressiveanimals;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.config.ConfigLoadIssueResolver;
import dev.ratas.slimedogcore.api.SlimeDogPlugin;
import dev.ratas.slimedogcore.impl.SlimeDogCore;

public interface IAggressiveAnimals extends SlimeDogPlugin {

    void debug(String msg);

    ConfigLoadIssueResolver reload();

    AggressivityManager getAggressivityManager();

    default SlimeDogCore asPlugin() {
        return (SlimeDogCore) this;
    }

}
