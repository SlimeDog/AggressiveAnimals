package dev.ratas.aggressiveanimals.aggressive.settings.type;

public record MobAttackSettings(Setting<Double> damage, Setting<Double> attackDamageLimit, Setting<Double> speed,
        Setting<Float> attackLeapHeight) {

}
