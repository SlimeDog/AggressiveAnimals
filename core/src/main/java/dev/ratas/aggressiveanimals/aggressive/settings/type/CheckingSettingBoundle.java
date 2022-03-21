package dev.ratas.aggressiveanimals.aggressive.settings.type;

public interface CheckingSettingBoundle {

    void checkAllTypes() throws IllegalStateException;

    default <T> void checkType(Setting<T> setting, Class<T> clazz) throws IllegalStateException {
        if (!clazz.isAssignableFrom(setting.value().getClass())) {
            throw new IllegalStateException(
                    "Problem with " + setting.path() + ", value of wrong type: " + setting.value().getClass().getName()
                            + " (Expected " + clazz.getName() + ")");
        }
        if (!clazz.isAssignableFrom(setting.def().getClass())) {
            throw new IllegalStateException(
                    "Problem with " + setting.path() + ", default of wrong type: " + setting.def().getClass().getName()
                            + " (Expected " + clazz.getName() + ")");
        }
    }

}
