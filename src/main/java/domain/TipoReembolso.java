package domain;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public enum TipoReembolso {
	PARCIAL,
	TOTAL;

	@Override
	public String toString() {
		String key = "TipoReembolso." + name();
		try {
			return ResourceBundle.getBundle("Etiquetas").getString(key);
		} catch (MissingResourceException e) {
			return name();
		}
	}
}
