package domain;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public enum CriterioDecision {
	PRECIO,
	VALORACION,
	ANTIGUEDAD,
	OTRO;

	@Override
	public String toString() {
		String key = "CriterioDecision." + name();
		try {
			return ResourceBundle.getBundle("Etiquetas").getString(key);
		} catch (MissingResourceException e) {
			return name();
		}
	}
}
