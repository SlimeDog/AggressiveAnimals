package dev.ratas.aggressiveanimals;

import dev.ratas.slimedogcore.api.SlimeDogPlugin;
import dev.ratas.slimedogcore.impl.SlimeDogCore;

public interface IAggressiveAnimals extends SlimeDogPlugin {

    void debug(String msg);

    default SlimeDogCore asPlugin() {
        return (SlimeDogCore) this;
    }

}
