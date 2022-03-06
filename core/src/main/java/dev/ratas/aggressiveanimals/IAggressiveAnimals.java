package dev.ratas.aggressiveanimals;

import dev.ratas.aggressiveanimals.aggressive.AggressivityManager;
import dev.ratas.aggressiveanimals.config.ConfigLoadIssueResolver;
import dev.ratas.aggressiveanimals.hooks.npc.NPCHookManager;
import dev.ratas.slimedogcore.api.SlimeDogPlugin;
import dev.ratas.slimedogcore.impl.SlimeDogCore;

public interface IAggressiveAnimals extends SlimeDogPlugin {

    ConfigLoadIssueResolver reload();

    AggressivityManager getAggressivityManager();

    NPCHookManager getNPCHookManager();

    default SlimeDogCore asPlugin() {
        return (SlimeDogCore) this;
    }

}
