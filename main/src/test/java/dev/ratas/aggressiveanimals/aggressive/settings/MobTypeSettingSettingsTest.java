package dev.ratas.aggressiveanimals.aggressive.settings;

import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.ratas.aggressiveanimals.aggressive.settings.DefaultConfigTest.MockResourceProvider;
import dev.ratas.aggressiveanimals.aggressive.settings.type.Builder;
import dev.ratas.aggressiveanimals.aggressive.settings.type.MobTypeSettings;
import dev.ratas.aggressiveanimals.aggressive.settings.type.Setting;
import dev.ratas.slimedogcore.api.config.SDCConfiguration;
import dev.ratas.slimedogcore.impl.config.CustomYamlConfig;

public class MobTypeSettingSettingsTest {
    private static final Logger LOGGER = Logger.getLogger("MobTypeSettingSettingsTest");
    private File configFile;
    private CustomYamlConfig config;
    private SDCConfiguration defSection;

    @BeforeEach
    public void setup() {
        configFile = DefaultConfigTest.getFrom("src/test/resources/config.yml".split("/"));
        config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        File defConfigFile = DefaultConfigTest.getFrom("src/main/resources/config.yml".split("/"));
        CustomYamlConfig config = new CustomYamlConfig(new MockResourceProvider(), defConfigFile);
        defSection = config.getConfig().getConfigurationSection("defaults");
    }

    @Test
    public void test_mobTypeSettingsHasAllSettings() {
        SDCConfiguration section = config.getConfig().getConfigurationSection("mobs.chicken");
        Builder builder = new Builder(section, defSection, LOGGER);
        MobTypeSettings settings = builder.build();
        List<Setting<?>> allSettings = settings.getAllSettings();
        Assertions.assertEquals(26, allSettings.size());
    }

    @Test
    public void test_mobTypeSettingsHasAllSettings_aquatic() {
        SDCConfiguration section = config.getConfig().getConfigurationSection("mobs.axolotl");
        Builder builder = new Builder(section, defSection, LOGGER);
        MobTypeSettings settings = builder.build();
        List<Setting<?>> allSettings = settings.getAllSettings();
        boolean found = false;
        for (Setting<?> set : allSettings) {
            String path = set.path();
            if (path.equals("attack-only-in-water")) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found);
        Assertions.assertEquals(27, allSettings.size());
    }

    @Test
    public void test_mobTypeSettingsHasNoDuplicateSettings() {
        SDCConfiguration section = config.getConfig().getConfigurationSection("mobs.chicken");
        Builder builder = new Builder(section, defSection, LOGGER);
        MobTypeSettings settings = builder.build();
        List<Setting<?>> allSettings = settings.getAllSettings();
        for (int i = 0; i < allSettings.size() - 1; i++) {
            for (int j = i + 1; j < allSettings.size(); j++) {
                Setting<?> s1 = allSettings.get(i);
                Setting<?> s2 = allSettings.get(j);
                Assertions.assertNotSame(s1, s2);
                Assertions.assertNotEquals(s1, s2);
            }
        }
    }

    @Test
    public void test_mobTypeSettingsTameableHasAllSettings() {
        SDCConfiguration section = config.getConfig().getConfigurationSection("mobs.ocelot");
        Builder builder = new Builder(section, defSection, LOGGER);
        MobTypeSettings settings = builder.build();
        List<Setting<?>> allSettings = settings.getAllSettings();
        Assertions.assertEquals(27, allSettings.size()); // has tamability one
    }

    @Test
    public void test_mobTypeSettingsTameableHasNoDuplicateSettings() {
        SDCConfiguration section = config.getConfig().getConfigurationSection("mobs.ocelot");
        Builder builder = new Builder(section, defSection, LOGGER);
        MobTypeSettings settings = builder.build();
        List<Setting<?>> allSettings = settings.getAllSettings();
        for (int i = 0; i < allSettings.size() - 1; i++) {
            for (int j = i + 1; j < allSettings.size(); j++) {
                Setting<?> s1 = allSettings.get(i);
                Setting<?> s2 = allSettings.get(j);
                Assertions.assertNotSame(s1, s2);
                Assertions.assertNotEquals(s1, s2);
            }
        }
    }

    @Test
    public void test_realConfigBuildingAllMobsSuccessful() {
        configFile = DefaultConfigTest.getFrom("src/main/resources/config.yml".split("/"));
        config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        SDCConfiguration section = config.getConfig().getConfigurationSection("mobs");
        Assertions.assertNotNull(section);
        for (String key : section.getKeys(false)) {
            if ((key.equals("frog") || key.equals("tadpole")) && MobType.matchType(key) != null
                    && MobType.matchType(key).getBukkitType() == null) {
                System.out.println(
                        "The following mob section is ignored since it is not supported in the current version of MC: "
                                + key);
                continue;
            }
            Builder builder = new Builder(section.getConfigurationSection(key), defSection, LOGGER);
            // can thrhow Builder.IllegalMobTypeSettingsException e)
            builder.build();
        }
    }

    @Test
    public void test_allMobsInDefaultConfigExactlyOnce() {
        Set<MobType> allMobTypes = EnumSet.noneOf(MobType.class);
        for (MobType type : MobType.values()) {
            if (type.getBukkitType() == null) {
                continue; // ignore
            }
            allMobTypes.add(type);
        }
        configFile = DefaultConfigTest.getFrom("src/main/resources/config.yml".split("/"));
        config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        SDCConfiguration section = config.getConfig().getConfigurationSection("mobs");
        Assertions.assertNotNull(section);
        for (String key : section.getKeys(false)) {
            if ((key.equals("frog") || key.equals("tadpole")) && MobType.matchType(key) != null
                    && MobType.matchType(key).getBukkitType() == null) {
                continue;
            }
            Builder builder = new Builder(section.getConfigurationSection(key), defSection, LOGGER);
            MobTypeSettings settings = builder.build();
            Assertions.assertTrue(allMobTypes.contains(settings.entityType().value()));
            allMobTypes.remove(settings.entityType().value());
        }
        Assertions.assertTrue(allMobTypes.isEmpty(),
                "Expected all mob types to appear in config, missing: " + allMobTypes);
    }

}
