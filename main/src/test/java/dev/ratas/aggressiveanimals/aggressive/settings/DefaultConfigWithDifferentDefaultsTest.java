package dev.ratas.aggressiveanimals.aggressive.settings;

import java.lang.reflect.Field;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.ratas.aggressiveanimals.aggressive.settings.type.Builder;
import dev.ratas.slimedogcore.api.config.SDCConfiguration;
import dev.ratas.slimedogcore.impl.config.CustomYamlConfig;

public class DefaultConfigWithDifferentDefaultsTest extends DefaultConfigTest {
    protected CustomYamlConfig config;

    @BeforeEach
    @Override
    public void setup() {
        configFile = getFrom("src/main/resources/config.yml".split("/"));
        config = new CustomYamlConfig(new MockResourceProvider(), configFile);
        config.getConfig(); // create lazy initialisation
        modify(config);
        defSection = config.getConfig().getConfigurationSection("defaults");
    }

    private void modify(CustomYamlConfig config) {
        YamlConfiguration yaml;
        try {
            Field field = config.getClass().getDeclaredField("customConfig");
            field.setAccessible(true);
            yaml = (YamlConfiguration) field.get(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        yaml.set("defaults.include-tamed-mobs", false);
    }

    @Test
    public void test_change_took() {
        Assertions.assertFalse(defSection.getBoolean("include-tamed-mobs"));
    }

    @Test
    public void test_untameableIsFine() {
        // make sure that an untameable mob is fine with the
        SDCConfiguration section = config.getConfig().getConfigurationSection("mobs.chicken");
        Builder chickenBuilder = new Builder(section, defSection, LOGGER);
        chickenBuilder.build();
    }

    @Test
    @Override
    public void test_defaultConfigLoads() {
        super.test_defaultConfigLoads();
    }

    @Test
    @Override
    public void test_defaultConfigHasMobsSection() {
        super.test_defaultConfigHasMobsSection();
    }

    @Test
    @Override
    public void test_defaultConfigDefinesChicken() {
        super.test_defaultConfigDefinesChicken();
    }

    @Test
    @Override
    public void test_defaultChickenSectionParses() {
        super.test_defaultChickenSectionParses();
    }

    @Test
    @Override
    public void test_defaultChickenSectionEnabled() {
        super.test_defaultChickenSectionEnabled();
    }

    @Test
    @Override
    public void test_MobTypeSettings_hasSimilarSettings_worksForSame() {
        super.test_MobTypeSettings_hasSimilarSettings_worksForSame();
    }

    @Test
    @Override
    public void test_defaultChickenSectionValuesMatchDefaultsForPig() {
        super.test_defaultChickenSectionValuesMatchDefaultsForPig();
    }

    @Test
    @Override
    public void test_defaultChickenHasAllSimilarSettings() {
        super.test_defaultChickenHasAllSimilarSettings();
    }

    @Test
    @Override
    public void test_defaultChickenHasDissimilarSettings() {
        super.test_defaultChickenHasDissimilarSettings();
    }

}
