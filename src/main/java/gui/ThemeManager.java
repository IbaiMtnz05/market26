package gui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIManager;

public final class ThemeManager {

    private ThemeManager() {
    }

    public static void applyDarkTheme() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Keep default look and feel if Nimbus is unavailable.
        }

        Color bg = new Color(24, 27, 34);
        Color panel = new Color(30, 34, 42);
        Color text = new Color(232, 236, 242);
        Color muted = new Color(155, 166, 180);
        Color accent = new Color(74, 144, 226);
        Color input = new Color(40, 44, 54);

        UIManager.put("control", panel);
        UIManager.put("info", panel);
        UIManager.put("nimbusBase", new Color(26, 30, 38));
        UIManager.put("nimbusBlueGrey", new Color(57, 68, 86));
        UIManager.put("nimbusLightBackground", bg);
        UIManager.put("text", text);

        UIManager.put("Panel.background", bg);
        UIManager.put("Label.foreground", text);
        UIManager.put("Button.background", new Color(58, 95, 144));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.select", accent);
        UIManager.put("TextField.background", input);
        UIManager.put("TextField.foreground", text);
        UIManager.put("TextField.caretForeground", text);
        UIManager.put("PasswordField.background", input);
        UIManager.put("PasswordField.foreground", text);
        UIManager.put("ComboBox.background", input);
        UIManager.put("ComboBox.foreground", text);
        UIManager.put("Table.background", input);
        UIManager.put("Table.foreground", text);
        UIManager.put("Table.selectionBackground", new Color(70, 92, 122));
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", new Color(73, 79, 89));
        UIManager.put("TableHeader.background", new Color(52, 58, 69));
        UIManager.put("TableHeader.foreground", text);
        UIManager.put("ScrollPane.background", bg);
        UIManager.put("OptionPane.messageForeground", text);
        UIManager.put("ToolTip.background", new Color(54, 59, 68));
        UIManager.put("ToolTip.foreground", text);

        UIManager.put("TitledBorder.border", BorderFactory.createLineBorder(muted));
    }
}
