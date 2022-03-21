package dev.ratas.aggressiveanimals.aggressive.settings.type;

public record MobHealthSettings(Setting<Double> healthPercentage) implements CheckingSettingBoundle {

    @Override
    public void checkAllTypes() throws IllegalStateException {
        checkType(healthPercentage, Double.class);
    }

}
