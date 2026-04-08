package domain;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public enum EstadoPago {
	CONFIRMADO,
	PENDIENTE,
	COMPLETADO,
	FALLIDO,
	REEMBOLSADO;

	@Override
	public String toString() {
		String key = "EstadoPago." + name();
		try {
			return ResourceBundle.getBundle("Etiquetas").getString(key);
		} catch (MissingResourceException e) {
			return name();
		}
	}
}
