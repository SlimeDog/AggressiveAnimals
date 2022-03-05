package dev.ratas.aggressiveanimals.aggressive.settings;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MobTypeTameabilityTest {

    @Test
    public void test_chickenNotTameable() {
        Assertions.assertFalse(MobType.chicken.isTameable(), "Chickens should not be tameabled");
    }

    @Test
    public void test_catIsTameable() {
        Assertions.assertTrue(MobType.cat.isTameable(), "Chickens should not be tameabled");
    }

}
