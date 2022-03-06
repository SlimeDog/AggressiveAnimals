package dev.ratas.aggressiveanimals.aggressive.settings.type;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.ratas.aggressiveanimals.aggressive.settings.DefaultConfigTest;
import dev.ratas.aggressiveanimals.aggressive.settings.MobType;
import dev.ratas.aggressiveanimals.aggressive.settings.DefaultConfigTest.MockResourceProvider;
import dev.ratas.aggressiveanimals.aggressive.settings.type.Builder.IllegalMobTypeSettingsException;
import dev.ratas.slimedogcore.api.config.SDCConfiguration;
import dev.ratas.slimedogcore.impl.config.CustomYamlConfig;

public class BuilderTest {
    private File testConfigFile;
    private CustomYamlConfig config;

    @BeforeEach
    public void setup() {
        testConfigFile = DefaultConfigTest.getFrom("src/test/resources/config.yml".split("/"));
        config = new CustomYamlConfig(new MockResourceProvider(), testConfigFile);
    }

    @Test
    public void test_builderBuildsFromEmpty() {
        SDCConfiguration config = this.config.getConfig().getConfigurationSection("mobs.wolf");
        Builder builder = new Builder(config);
        MobTypeSettings mts = builder.build();
        Assertions.assertTrue(mts.entityType() == MobType.wolf, "Expected wolf");
        Assertions.assertNotNull(mts, "Expected to build a non-null settings from empty (wolf)");
        Assertions.assertTrue(mts.miscSettings().includeTamed(), "Tameables should beincluded by default");
    }

    @Test
    public void test_builderBuildsTameableWithIncludeTameable() {
        SDCConfiguration config = this.config.getConfig().getConfigurationSection("mobs.cat");
        Builder builder = new Builder(config);
        MobTypeSettings mts = builder.build();
        Assertions.assertTrue(mts.entityType() == MobType.cat, "Expected cat");
        Assertions.assertNotNull(mts, "Expected to build a non-null settings with including tameables (cat)");
        Assertions.assertTrue(mts.miscSettings().includeTamed(), "Expected to include tameables");
    }

    @Test
    public void test_builderBuildsFoxWithIncludeTameable() {
        SDCConfiguration config = this.config.getConfig().getConfigurationSection("mobs.fox");
        Builder builder = new Builder(config);
        MobTypeSettings mts = builder.build();
        Assertions.assertTrue(mts.entityType() == MobType.fox, "Expected fox");
        Assertions.assertNotNull(mts, "Expected to build a non-null settings with including tameables (fox)");
        Assertions.assertTrue(mts.miscSettings().includeTamed(), "Expected to include tameables");
    }

    @Test
    public void test_builderFailsNonTameableWithIncludeTameable() {
        SDCConfiguration config = this.config.getConfig().getConfigurationSection("mobs.pig");
        Builder builder = new Builder(config);
        Assertions.assertThrows(IllegalMobTypeSettingsException.class, () -> builder.build());
    }

}
