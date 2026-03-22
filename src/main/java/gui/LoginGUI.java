package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import businessLogic.BLFacade;
import domain.Buyer;
import domain.Seller;
import domain.User;

/**
 * GUI window for user authentication.
 * Allows users to login to the marketplace system or navigate to registration.
 */
public class LoginGUI extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private JLabel lblMessage;
    private JComboBox<String> cmbLanguage;
    private JLabel lblEmail;
    private JLabel lblPassword;
    private JLabel lblTitle;
    private ResourceBundle labels = ResourceBundle.getBundle("Etiquetas");
    
    /**
     * Creates the login window.
     */
    public LoginGUI() {
        setTitle(labels.getString("LoginGUI.Title"));
        setSize(460, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel superior con título e idioma
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setOpaque(false);
        
        lblTitle = new JLabel(labels.getString("LoginGUI.AppTitle"), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        topPanel.add(lblTitle, BorderLayout.CENTER);
        
        // Selector de idioma
        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        languagePanel.setOpaque(false);
        cmbLanguage = new JComboBox<>(new String[]{"Español", "English", "Euskera"});
        cmbLanguage.setSelectedIndex(0);
        languagePanel.add(cmbLanguage);
        topPanel.add(languagePanel, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 12));
        panel.setOpaque(false);
        
        // Email
        lblEmail = new JLabel(labels.getString("LoginGUI.Email"));
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblEmail);
        txtEmail = new JTextField();
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(txtEmail);
        
        // Password
        lblPassword = new JLabel(labels.getString("LoginGUI.Password"));
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(lblPassword);
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(txtPassword);
        
        // Botón Login
        panel.add(new JLabel(""));
        btnLogin = new JButton(labels.getString("LoginGUI.Login"));
        btnLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(btnLogin);
        
        // Botón Registro
        panel.add(new JLabel(""));
        btnRegister = new JButton(labels.getString("LoginGUI.Register"));
        btnRegister.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(btnRegister);
        
        // Mensaje
        lblMessage = new JLabel("", SwingConstants.CENTER);
        lblMessage.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMessage.setForeground(Color.RED);
        
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(lblMessage, BorderLayout.SOUTH);

        add(mainPanel);
        
        // ========= LISTENERS =========
        cmbLanguage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeLanguage();
            }
        });
        
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openRegisterGUI();
            }
        });
    }
    
    private void changeLanguage() {
        int index = cmbLanguage.getSelectedIndex();
        Locale newLocale;
        
        switch(index) {
            case 0: newLocale = Locale.forLanguageTag("es"); break;
            case 1: newLocale = Locale.forLanguageTag("en"); break;
            case 2: newLocale = Locale.forLanguageTag("eu"); break;
            default: newLocale = Locale.forLanguageTag("es");
        }
        
        Locale.setDefault(newLocale);
        labels = ResourceBundle.getBundle("Etiquetas", newLocale);
        updateLabels();
    }
    
    private void updateLabels() {
        setTitle(labels.getString("LoginGUI.Title"));
        lblTitle.setText(labels.getString("LoginGUI.AppTitle"));
        lblEmail.setText(labels.getString("LoginGUI.Email"));
        lblPassword.setText(labels.getString("LoginGUI.Password"));
        btnLogin.setText(labels.getString("LoginGUI.Login"));
        btnRegister.setText(labels.getString("LoginGUI.Register"));
    }
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            lblMessage.setText(labels.getString("RegisterGUI.FillAllFields"));
            return;
        }
        
        // Llamar a la lógica de negocio
        BLFacade facade = MainGUI.getBusinessLogic();
        User user = facade.login(email, password);
        
        if (user == null) {
            lblMessage.setForeground(Color.RED);
            lblMessage.setText(labels.getString("LoginGUI.InvalidCredentials"));
            return;
        }
        
        // Login exitoso
        lblMessage.setForeground(Color.GREEN);
        lblMessage.setText(labels.getString("BuyerMainGUI.Welcome") + " " + user.getName());
        
        // Abrir ventana correspondiente según tipo de usuario
        this.dispose();  // Cerrar ventana de login
        
        if (user instanceof Buyer) {
            // Abrir GUI de comprador
            JFrame buyerGUI = new BuyerMainGUI(user.getEmail());
            buyerGUI.setVisible(true);
        } else if (user instanceof Seller) {
            // Abrir GUI de vendedor
            JFrame sellerGUI = new MainGUI(user.getEmail());
            sellerGUI.setVisible(true);
        }
    }
    
    private void openRegisterGUI() {
        RegisterGUI registerGUI = new RegisterGUI();
        registerGUI.setVisible(true);
        this.dispose();
    }
}