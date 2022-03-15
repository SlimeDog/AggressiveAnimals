package dev.ratas.aggressiveanimals.aggressive.settings;

import java.io.File;
import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.ratas.aggressiveanimals.aggressive.settings.type.Builder;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.aggressive.settings.type.Setting;
import dev.ratas.slimedogcore.api.wrappers.SDCResourceProvider;
import dev.ratas.slimedogcore.impl.config.CustomYamlConfig;

public class DefaultConfigTest {
    private File configFile;

    @BeforeEach
    public void setup() {
        configFile = getFrom("src/main/resources/config.yml".split("/"));
    }

    @Test
    public void test_defaultConfigLoads() {
        CustomYamlConfig config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        Assertions.assertFalse(config.getConfig().getKeys(true).isEmpty(), "Default config should not be empty");
    }

    @Test
    public void test_defaultConfigHasMobsSection() {
        CustomYamlConfig config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        Assertions.assertTrue(config.getConfig().isSet("mobs"), "Default config should define 'mobs' section");
        Assertions.assertNotNull(config.getConfig().getConfigurationSection("mobs"),
                "Default config should define 'mobs' section as a configuration section");
        Assertions.assertFalse(config.getConfig().getConfigurationSection("mobs").getKeys(true).isEmpty(),
                "Default config should define 'mobs' with some keys");
    }

    @Test
    public void test_defaultConfigDefinesChicken() {
        CustomYamlConfig config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        Assertions.assertTrue(config.getConfig().isSet("mobs.chicken"),
                "Default config should define 'mobs.chicken' section");
        Assertions.assertNotNull(config.getConfig().getConfigurationSection("mobs.chicken"),
                "Default config should define 'mobs.chicken' section as a configuration section");
        Assertions.assertFalse(config.getConfig().getConfigurationSection("mobs.chicken").getKeys(true).isEmpty(),
                "Default config should define 'mobs.chicken' with some keys");
    }

    @Test
    public void test_defaultChickenSectionParses() {
        CustomYamlConfig config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        Builder builder = new Builder(config.getConfig().getConfigurationSection("mobs.chicken"));
        MobTypeSettings settings = builder.build();
        Assertions.assertNotNull(settings, "Should build non-null settings");
    }

    @Test
    public void test_defaultChickenSectionEnabled() {
        CustomYamlConfig config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        Builder builder = new Builder(config.getConfig().getConfigurationSection("mobs.chicken"));
        MobTypeSettings settings = builder.build();
        Assertions.assertFalse(settings.enabled().value(), "Default chicken section should not be enabled");
    }

    @Test
    public void test_MobTypeSettings_hasSimilarSettings_worksForSame() {
        CustomYamlConfig config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        Builder chickenBuilder = new Builder(config.getConfig().getConfigurationSection("mobs.chicken"));
        MobTypeSettings chicken = chickenBuilder.build();
        Assertions.assertTrue(chicken.hasSimilarSettings(chicken), "Settings should be similar to self");
    }

    @Test
    public void test_defaultChickenSectionValuesMatchDefaultsForPig() {
        CustomYamlConfig config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        Builder chickenBuilder = new Builder(config.getConfig().getConfigurationSection("mobs.chicken"));
        MobTypeSettings chicken = chickenBuilder.build();
        Builder pigBuilder = new Builder(config.getConfig().getConfigurationSection("mobs.pig"));
        MobTypeSettings pig = pigBuilder.build();
        int maxSims = chicken.getSettingSimilarities(chicken);
        Assertions.assertEquals(maxSims - 1, pig.getSettingSimilarities(chicken),
                "Pig and chicken settings should be similar (1)");
        Assertions.assertEquals(maxSims - 1, chicken.getSettingSimilarities(pig),
                "Pig and chicken settings should be similar (2)");
    }

    @Test
    public void test_defaultChickenHasAllSimilarSettings() {
        CustomYamlConfig config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        Builder chickenBuilder = new Builder(config.getConfig().getConfigurationSection("mobs.chicken"));
        MobTypeSettings chicken = chickenBuilder.build();
        for (Setting<?> setting : chicken.getAllSettings()) {
            Assertions.assertTrue(chicken.hasSameSetting(setting));
        }
    }

    @Test
    public void test_defaultChickenHasDissimilarSettings() {
        CustomYamlConfig config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        Builder chickenBuilder = new Builder(config.getConfig().getConfigurationSection("mobs.chicken"));
        Builder pigBuilder = new Builder(config.getConfig().getConfigurationSection("mobs.pig"));
        MobTypeSettings chicken = chickenBuilder.build();
        MobTypeSettings pig = pigBuilder.build();
        Assertions.assertFalse(chicken.hasSameSetting(pig.speedMultiplier()));
    }

    @Test
    public void test_defaultsSameInGameAndInConfig() {
        CustomYamlConfig config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        Builder defaultsBuilder = new Builder(config.getConfig().getConfigurationSection("defaults"));
        MobTypeSettings inConfigDefaults = defaultsBuilder.build();
        MobTypeSettings inCodeDefaults = Builder.getDefaultSettings().getSettings();
        int maxSims = inConfigDefaults.getSettingSimilarities(inConfigDefaults);
        Assertions.assertEquals(maxSims, inConfigDefaults.getSettingSimilarities(inConfigDefaults),
                "Defaults in code and in default config should be similar");
        Assertions.assertTrue(inConfigDefaults.hasSimilarSettings(inCodeDefaults),
                "Defaults in code and in default config should be similar");
    }

    public static final class MockResourceProvider implements SDCResourceProvider {

        @Override
        public InputStream getResource(String filename) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void saveResource(String resourcePath, boolean replace) {
            // TODO Auto-generated method stub

        }

    }

    public static File getFrom(String... paths) {
        File folder = null;
        for (String path : paths) {
            if (folder == null) {
                folder = new File(path);
            } else {
                folder = new File(folder, path);
            }
        }
        return folder;
    }

}
