package dev.ratas.aggressiveanimals.aggressive.settings.type;

public record MobAttackSettings(Setting<Double> damage, Setting<Double> attackDamageLimit, Setting<Double> speed,
        Setting<Double> attackLeapHeight) implements CheckingSettingBoundle {

    @Override
    public void checkAllTypes() throws IllegalStateException {
        checkType(damage, Double.class);
        checkType(attackDamageLimit, Double.class);
        checkType(speed, Double.class);
        checkType(attackLeapHeight, Double.class);
    }

}
