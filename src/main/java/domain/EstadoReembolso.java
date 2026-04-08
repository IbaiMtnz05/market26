package domain;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public enum EstadoReembolso {
    PENDIENTE,
    APROBADO,
    RECHAZADO,
    COMPLETADO;

    @Override
    public String toString() {
        String key = "EstadoReembolso." + name();
        try {
            return ResourceBundle.getBundle("Etiquetas").getString(key);
        } catch (MissingResourceException e) {
            return name();
        }
    }
}
