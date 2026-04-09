package gui;

import java.util.Locale;
import java.util.ResourceBundle;

public final class I18n {

    private static final String BASE_NAME = "Etiquetas";
    private static ResourceBundle labels = ResourceBundle.getBundle(BASE_NAME, Locale.getDefault());

    private I18n() {
    }

    public static void setLocale(Locale locale) {
        Locale.setDefault(locale);
        labels = ResourceBundle.getBundle(BASE_NAME, locale);
    }

    public static String t(String key) {
        if (labels.containsKey(key)) {
            return labels.getString(key);
        }
        return "!" + key + "!";
    }

    public static Locale localeFromIndex(int index) {
        switch (index) {
            case 0:
                return Locale.forLanguageTag("es");
            case 1:
                return Locale.forLanguageTag("en");
            case 2:
                return Locale.forLanguageTag("eu");
            default:
                return Locale.forLanguageTag("es");
        }
    }
}