package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Utility class for GUI operations.
 * Provides internationalized product status options based on current locale.
 */
public class Utils {
	/**
	 * Gets the list of product status options in the current locale.
	 * 
	 * @return ArrayList of status strings (New, Very Good, Acceptable, Very Used) in current language
	 */
	public static ArrayList<String> getStatus() {
		String lang=Locale.getDefault().getLanguage();
		if (lang.compareTo("en")==0) 
			return new ArrayList<String>(Arrays.asList("New","Very Good","Acceptable","Very Used"));
		if (lang.compareTo("es")==0) 
			return new ArrayList<String>(Arrays.asList("Nuevo","Muy Bueno","Aceptable","Lo ha dado todo"));
		if (lang.compareTo("eu")==0) 
			return new ArrayList<String>(Arrays.asList("Berria","Oso Ona","Egokia","Oso zaharra"));
		return new ArrayList<String>(Arrays.asList("Nuevo","Muy Bueno","Aceptable","Lo ha dado todo"));
	}
	
	/**
	 * Gets a specific product status by index in the current locale.
	 * 
	 * @param t Index of the status (0-3)
	 * @return Status string at the specified index
	 */
	public static String getStatus(int t) {
		ArrayList<String> status=getStatus();
		return status.get(t);
	}
}
