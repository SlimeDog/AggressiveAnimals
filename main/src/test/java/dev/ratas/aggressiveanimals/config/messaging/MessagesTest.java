package dev.ratas.aggressiveanimals.config.messaging;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.ratas.aggressiveanimals.MockPlugin;
import dev.ratas.aggressiveanimals.aggressive.settings.DefaultConfigTest;
import dev.ratas.aggressiveanimals.aggressive.settings.DefaultConfigTest.MockResourceProvider;
import dev.ratas.aggressiveanimals.config.MockCustomConfigManager;
import dev.ratas.slimedogcore.api.config.SDCCustomConfig;
import dev.ratas.slimedogcore.api.messaging.factory.SDCMessageFactory;
import dev.ratas.slimedogcore.impl.config.CustomYamlConfig;

public class MessagesTest {
    private File configFile;
    private SDCCustomConfig config;
    private MockPlugin plugin;

    @BeforeEach
    public void setup() {
        configFile = DefaultConfigTest.getFrom("src/main/resources/config.yml".split("/"));
        config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        plugin = new MockPlugin(configFile, new MockCustomConfigManager(config));
    }

    @Test
    public void test_messagesInvoked() {
        Messages messages;
        try {
            messages = new Messages(plugin);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertNotNull(messages);
    }

    @Test
    public void test_messagesNotNull() {
        Messages messages;
        try {
            messages = new Messages(plugin);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        for (Method method : messages.getClass().getMethods()) {
            if (SDCMessageFactory.class.isAssignableFrom(method.getReturnType())) {
                Object returned;
                try {
                    if (method.getParameterTypes().length == 0) {
                        returned = method.invoke(messages);
                    } else {
                        returned = method.invoke(messages, true);
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                Assertions.assertNotNull(returned);
                Assertions.assertTrue(returned instanceof SDCMessageFactory);
            }
        }
    }

}
