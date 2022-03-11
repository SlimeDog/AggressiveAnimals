package dev.ratas.aggressiveanimals.config;

import java.io.File;

import dev.ratas.slimedogcore.api.config.SDCCustomConfig;
import dev.ratas.slimedogcore.api.config.SDCCustomConfigManager;

public class MockCustomConfigManager implements SDCCustomConfigManager {
    private final SDCCustomConfig delegate;

    public MockCustomConfigManager(SDCCustomConfig delegate) {
        this.delegate = delegate;
    }

    @Override
    public SDCCustomConfig getConfig(String fileName) {
        return delegate;
    }

    @Override
    public SDCCustomConfig getConfig(File file) {
        return delegate;
    }

    @Override
    public SDCCustomConfig getDefaultConfig() {
        return delegate;
    }

}
