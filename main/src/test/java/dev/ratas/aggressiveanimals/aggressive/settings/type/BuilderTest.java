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
    private SDCConfiguration defSection;

    @BeforeEach
    public void setup() {
        testConfigFile = DefaultConfigTest.getFrom("src/test/resources/config.yml".split("/"));
        config = new CustomYamlConfig(new MockResourceProvider(), testConfigFile);
        File defConfigFile = DefaultConfigTest.getFrom("src/main/resources/config.yml".split("/"));
        CustomYamlConfig config = new CustomYamlConfig(new MockResourceProvider(), defConfigFile);
        defSection = config.getConfig().getConfigurationSection("defaults");
    }

    @Test
    public void test_builderBuildsFromEmpty() {
        SDCConfiguration config = this.config.getConfig().getConfigurationSection("mobs.wolf");
        Builder builder = new Builder(config, defSection);
        MobTypeSettings mts = builder.build();
        Assertions.assertTrue(mts.entityType().value() == MobType.wolf, "Expected wolf");
        Assertions.assertNotNull(mts, "Expected to build a non-null settings from empty (wolf)");
        Assertions.assertTrue(mts.miscSettings().includeTamed().value(), "Tameables are included by default");
    }

    @Test
    public void test_builderBuildsTameableWithIncludeTameable() {
        SDCConfiguration config = this.config.getConfig().getConfigurationSection("mobs.cat");
        Builder builder = new Builder(config, defSection);
        MobTypeSettings mts = builder.build();
        Assertions.assertTrue(mts.entityType().value() == MobType.cat, "Expected cat");
        Assertions.assertNotNull(mts, "Expected to build a non-null settings with including tameables (cat)");
        Assertions.assertTrue(mts.miscSettings().includeTamed().value(), "Expected to include tameables");
    }

    @Test
    public void test_builderBuildsFoxWithIncludeTameable() {
        SDCConfiguration config = this.config.getConfig().getConfigurationSection("mobs.fox");
        Builder builder = new Builder(config, defSection);
        MobTypeSettings mts = builder.build();
        Assertions.assertTrue(mts.entityType().value() == MobType.fox, "Expected fox");
        Assertions.assertNotNull(mts, "Expected to build a non-null settings with including tameables (fox)");
        Assertions.assertFalse(mts.miscSettings().includeTamed().value(), "Expected not to include tameables");
    }

    @Test
    public void test_builderBuildsOcelotWithIncludeTameable() {
        SDCConfiguration config = this.config.getConfig().getConfigurationSection("mobs.ocelot");
        Builder builder = new Builder(config, defSection);
        MobTypeSettings mts = builder.build();
        Assertions.assertTrue(mts.entityType().value() == MobType.ocelot, "Expected ocelot");
        Assertions.assertNotNull(mts, "Expected to build a non-null settings with including tameables (ocelot)");
        Assertions.assertFalse(mts.miscSettings().includeTamed().value(), "Expected not to include tameables");
    }

    @Test
    public void test_builderFailsNonTameableWithIncludeTameable() {
        SDCConfiguration config = this.config.getConfig().getConfigurationSection("mobs.pig");
        Builder builder = new Builder(config, defSection);
        Assertions.assertThrows(IllegalMobTypeSettingsException.class, () -> builder.build());
    }

    @Test
    public void test_builderSettingsCorrectType() {
        Builder builder = new Builder(defSection, defSection);
        MobTypeSettings mts = builder.build();
        for (Setting<?> setting : mts.getAllSettings()) {
            Assertions.assertSame(setting.value().getClass(), setting.def().getClass());
        }
    }

    @Test
    public void test_defBuilderSettingsCorrectType() {
        System.out.println("B4 BUILDER");
        Builder builder = new Builder(defSection, defSection);
        MobTypeSettings mts = builder.build();
        System.out.println("AFTER BUILD");
        mts.checkAllTypes();
    }

}
