package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Utils {

    public static ArrayList<String> getStatus() {
        ResourceBundle labels = ResourceBundle.getBundle("Etiquetas");
        List<String> values = Arrays.asList(
            labels.getString("Status.New"),
            labels.getString("Status.VeryGood"),
            labels.getString("Status.Acceptable"),
            labels.getString("Status.VeryUsed")
        );
        return new ArrayList<String>(values);
    }

    public static String getStatus(int t) {
        return getStatus().get(t);
    }
}