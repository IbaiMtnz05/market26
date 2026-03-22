package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import businessLogic.BLFacade;
import domain.Buyer;
import domain.Seller;
import exceptions.InvalidEmailException;
import exceptions.InvalidFieldException;
import exceptions.UserAlreadyExistsException;

/**
 * GUI window for new user registration.
 * Allows users to register as either a buyer or seller with appropriate fields.
 */
public class RegisterGUI extends JFrame {
    private JTextField txtEmail, txtName, txtPassword;
    private JTextField txtShippingAddress;  // Solo para Buyer
    private JTextField txtBankAccount;      // Solo para Seller
    private JRadioButton rbBuyer, rbSeller;
    private JButton btnRegister, btnCancel;
    private JPanel pnlSpecific;
    private JPanel pnlType;
    private JComboBox<String> cmbLanguage;
    private JLabel lblEmail, lblName, lblPassword;
    private ResourceBundle labels = ResourceBundle.getBundle("Etiquetas");
    
    /**
     * Creates the registration window.
     */
    public RegisterGUI() {
        setTitle(labels.getString("RegisterGUI.Title"));
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                           20, 20, 20, 20));
        
        // Panel superior con selector de idioma
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerPanel.add(new JLabel("🌐 "));
        cmbLanguage = new JComboBox<>(new String[]{"Español", "English", "Euskera"});
        cmbLanguage.setSelectedIndex(0);
        headerPanel.add(cmbLanguage);
        
        pnlType = new JPanel();
        pnlType.setBorder(BorderFactory.createTitledBorder(
                         labels.getString("LoginGUI.SelectRole")));
        ButtonGroup group = new ButtonGroup();
        
        rbBuyer = new JRadioButton("Comprador", true);
        rbSeller = new JRadioButton("Vendedor");
        group.add(rbBuyer);
        group.add(rbSeller);
        
        pnlType.add(rbBuyer);
        pnlType.add(rbSeller);
        
        JPanel pnlCommon = new JPanel(new GridLayout(3, 2, 5, 5));
        lblEmail = new JLabel(labels.getString("RegisterGUI.Email"));
        pnlCommon.add(lblEmail);
        txtEmail = new JTextField();
        pnlCommon.add(txtEmail);
        
        lblName = new JLabel(labels.getString("RegisterGUI.Name"));
        pnlCommon.add(lblName);
        txtName = new JTextField();
        pnlCommon.add(txtName);
        
        lblPassword = new JLabel(labels.getString("RegisterGUI.Password"));
        pnlCommon.add(lblPassword);
        txtPassword = new JPasswordField();
        pnlCommon.add(txtPassword);
        
        pnlSpecific = new JPanel(new GridLayout(1, 2, 5, 5));
        txtShippingAddress = new JTextField();
        txtBankAccount = new JTextField();
        updateSpecificPanel();
        
        JPanel pnlButtons = new JPanel();
        btnRegister = new JButton(labels.getString("RegisterGUI.Register"));
        btnCancel = new JButton(labels.getString("RegisterGUI.Cancel"));
        pnlButtons.add(btnRegister);
        pnlButtons.add(btnCancel);
        
        // ========= ENSAMBLAR =========
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(pnlType, BorderLayout.NORTH);
        contentPanel.add(pnlCommon, BorderLayout.CENTER);
        contentPanel.add(pnlSpecific, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(pnlButtons, BorderLayout.SOUTH);
        add(mainPanel);
        
        // ========= LISTENERS =========
        cmbLanguage.addActionListener(e -> changeLanguage());
        rbBuyer.addActionListener(e -> updateSpecificPanel());
        rbSeller.addActionListener(e -> updateSpecificPanel());
        
        btnRegister.addActionListener(e -> handleRegister());
        btnCancel.addActionListener(e -> {
            new LoginGUI().setVisible(true);
            dispose();
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
        setTitle(labels.getString("RegisterGUI.Title"));
        pnlType.setBorder(BorderFactory.createTitledBorder(
                         labels.getString("LoginGUI.SelectRole")));
        lblEmail.setText(labels.getString("RegisterGUI.Email"));
        lblName.setText(labels.getString("RegisterGUI.Name"));
        lblPassword.setText(labels.getString("RegisterGUI.Password"));
        btnRegister.setText(labels.getString("RegisterGUI.Register"));
        btnCancel.setText(labels.getString("RegisterGUI.Cancel"));
        updateSpecificPanel();
    }
    
    private void updateSpecificPanel() {
        pnlSpecific.removeAll();
        
        if (rbBuyer.isSelected()) {
            pnlSpecific.add(new JLabel(labels.getString("RegisterGUI.ShippingAddress")));
            pnlSpecific.add(txtShippingAddress);
        } else {
            pnlSpecific.add(new JLabel(labels.getString("RegisterGUI.BankAccount")));
            pnlSpecific.add(txtBankAccount);
        }
        
        pnlSpecific.revalidate();
        pnlSpecific.repaint();
    }
    
    private void handleRegister() {
        String email = txtEmail.getText().trim();
        String name = txtName.getText().trim();
        String password = txtPassword.getText().trim();
        
        // Validación básica de campos vacíos en GUI
        if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                labels.getString("RegisterGUI.FillAllFields"), 
                labels.getString("RegisterGUI.RegistrationError"), 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        BLFacade facade = MainGUI.getBusinessLogic();
        
        try {
            if (rbBuyer.isSelected()) {
                String address = txtShippingAddress.getText().trim();
                if (address.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        labels.getString("RegisterGUI.FillAllFields"), 
                        labels.getString("RegisterGUI.RegistrationError"), 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Intentar registrar comprador
                Buyer buyer = facade.registerBuyer(email, name, password, address);
                
                // Éxito
                JOptionPane.showMessageDialog(this, 
                    labels.getString("RegisterGUI.RegistrationSuccess"), 
                    labels.getString("Accept"), 
                    JOptionPane.INFORMATION_MESSAGE);
                openLoginGUI();
                
            } else {
                String bankAccount = txtBankAccount.getText().trim();
                if (bankAccount.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        labels.getString("RegisterGUI.FillAllFields"), 
                        labels.getString("RegisterGUI.RegistrationError"), 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Intentar registrar vendedor
                Seller seller = facade.registerSeller(email, name, password, bankAccount);
                
                // Éxito
                JOptionPane.showMessageDialog(this, 
                    labels.getString("RegisterGUI.RegistrationSuccess"), 
                    labels.getString("Accept"), 
                    JOptionPane.INFORMATION_MESSAGE);
                openLoginGUI();
            }
            
        } catch (UserAlreadyExistsException e) {
            // Email duplicado
            JOptionPane.showMessageDialog(this,
                "El email ya está registrado en el sistema.",
                labels.getString("RegisterGUI.RegistrationError"), 
                JOptionPane.ERROR_MESSAGE);
                
        } catch (InvalidEmailException e) {
            // Formato de email inválido
            JOptionPane.showMessageDialog(this,
                "El formato del email es inválido. Use formato: usuario@dominio.com",
                labels.getString("RegisterGUI.RegistrationError"), 
                JOptionPane.ERROR_MESSAGE);
                
        } catch (InvalidFieldException e) {
            // Campo inválido
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                labels.getString("RegisterGUI.RegistrationError"), 
                JOptionPane.WARNING_MESSAGE);
                
        } catch (Exception e) {
            // Error genérico
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                labels.getString("RegisterGUI.RegistrationError") + ": " + e.getMessage(), 
                labels.getString("RegisterGUI.RegistrationError"), 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openLoginGUI() {
        new LoginGUI().setVisible(true);
        dispose();
    }
}