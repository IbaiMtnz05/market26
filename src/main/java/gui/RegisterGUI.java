package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import businessLogic.BLFacade;
import exceptions.InvalidEmailException;
import exceptions.InvalidFieldException;
import exceptions.UserAlreadyExistsException;

/**
 * GUI window for new user registration.
 * Allows users to register as either a buyer or seller with appropriate fields.
 */
public class RegisterGUI extends JFrame {
    private JTextField txtEmail, txtName;
    private JPasswordField txtPassword;
    private JTextField txtShippingAddress;  // Solo para Buyer
    private JTextField txtBankAccount;      // Solo para Seller
    private JRadioButton rbBuyer, rbSeller;
    private JButton btnRegister, btnCancel;
    private JPanel pnlSpecific;
    private JPanel pnlType;
    private JLabel lblEmail, lblName, lblPassword;
    
    /**
     * Creates the registration window.
     */
    public RegisterGUI() {
        setTitle(I18n.t("RegisterGUI.Title"));
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                           20, 20, 20, 20));
        
        pnlType = new JPanel();
        pnlType.setBorder(BorderFactory.createTitledBorder(
                         I18n.t("LoginGUI.SelectRole")));
        ButtonGroup group = new ButtonGroup();
        
        rbBuyer = new JRadioButton(I18n.t("RegisterGUI.Buyer"), true);
        rbSeller = new JRadioButton(I18n.t("RegisterGUI.Seller"));
        group.add(rbBuyer);
        group.add(rbSeller);
        
        pnlType.add(rbBuyer);
        pnlType.add(rbSeller);
        
        JPanel pnlCommon = new JPanel(new GridLayout(3, 2, 5, 5));
        lblEmail = new JLabel(I18n.t("RegisterGUI.Email"));
        pnlCommon.add(lblEmail);
        txtEmail = new JTextField();
        pnlCommon.add(txtEmail);
        
        lblName = new JLabel(I18n.t("RegisterGUI.Name"));
        pnlCommon.add(lblName);
        txtName = new JTextField();
        pnlCommon.add(txtName);
        
        lblPassword = new JLabel(I18n.t("RegisterGUI.Password"));
        pnlCommon.add(lblPassword);
        txtPassword = new JPasswordField();
        pnlCommon.add(txtPassword);
        
        pnlSpecific = new JPanel(new GridLayout(1, 2, 5, 5));
        txtShippingAddress = new JTextField();
        txtBankAccount = new JTextField();
        updateSpecificPanel();
        
        JPanel pnlButtons = new JPanel();
        btnRegister = new JButton(I18n.t("RegisterGUI.Register"));
        btnCancel = new JButton(I18n.t("RegisterGUI.Cancel"));
        pnlButtons.add(btnRegister);
        pnlButtons.add(btnCancel);
        
        // ========= ENSAMBLAR =========
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(pnlType, BorderLayout.NORTH);
        contentPanel.add(pnlCommon, BorderLayout.CENTER);
        contentPanel.add(pnlSpecific, BorderLayout.SOUTH);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(pnlButtons, BorderLayout.SOUTH);
        add(mainPanel);
        
        // ========= LISTENERS =========
        rbBuyer.addActionListener(e -> updateSpecificPanel());
        rbSeller.addActionListener(e -> updateSpecificPanel());
        
        btnRegister.addActionListener(e -> handleRegister());
        btnCancel.addActionListener(e -> {
            new LoginGUI().setVisible(true);
            dispose();
        });
    }
    
    private void updateSpecificPanel() {
        pnlSpecific.removeAll();
        
        if (rbBuyer.isSelected()) {
            pnlSpecific.add(new JLabel(I18n.t("RegisterGUI.ShippingAddress")));
            pnlSpecific.add(txtShippingAddress);
        } else {
            pnlSpecific.add(new JLabel(I18n.t("RegisterGUI.BankAccount")));
            pnlSpecific.add(txtBankAccount);
        }
        
        pnlSpecific.revalidate();
        pnlSpecific.repaint();
    }
    
    private void handleRegister() {
        String email = txtEmail.getText().trim();
        String name = txtName.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        
        // Validación básica de campos vacíos en GUI
        if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                I18n.t("RegisterGUI.FillAllFields"), 
                I18n.t("RegisterGUI.RegistrationError"), 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        BLFacade facade = MainGUI.getBusinessLogic();
        
        try {
            if (rbBuyer.isSelected()) {
                String address = txtShippingAddress.getText().trim();
                if (address.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        I18n.t("RegisterGUI.FillAllFields"), 
                        I18n.t("RegisterGUI.RegistrationError"), 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Intentar registrar comprador
                facade.registerBuyer(email, name, password, address);
                
                // Éxito
                JOptionPane.showMessageDialog(this, 
                    I18n.t("RegisterGUI.RegistrationSuccess"), 
                    I18n.t("Accept"), 
                    JOptionPane.INFORMATION_MESSAGE);
                openLoginGUI();
                
            } else {
                String bankAccount = txtBankAccount.getText().trim();
                if (bankAccount.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        I18n.t("RegisterGUI.FillAllFields"), 
                        I18n.t("RegisterGUI.RegistrationError"), 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Intentar registrar vendedor
                facade.registerSeller(email, name, password, bankAccount);
                
                // Éxito
                JOptionPane.showMessageDialog(this, 
                    I18n.t("RegisterGUI.RegistrationSuccess"), 
                    I18n.t("Accept"), 
                    JOptionPane.INFORMATION_MESSAGE);
                openLoginGUI();
            }
            
        } catch (UserAlreadyExistsException e) {
            // Email duplicado
            JOptionPane.showMessageDialog(this,
                I18n.t("RegisterGUI.ErrorEmailInUse"),
                I18n.t("RegisterGUI.RegistrationError"), 
                JOptionPane.ERROR_MESSAGE);
                
        } catch (InvalidEmailException e) {
            // Formato de email inválido
            JOptionPane.showMessageDialog(this,
                I18n.t("RegisterGUI.ErrorInvalidEmail"),
                I18n.t("RegisterGUI.RegistrationError"), 
                JOptionPane.ERROR_MESSAGE);
                
        } catch (InvalidFieldException e) {
            // Campo inválido
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                I18n.t("RegisterGUI.RegistrationError"), 
                JOptionPane.WARNING_MESSAGE);
                
        } catch (Exception e) {
            // Error genérico
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                I18n.t("RegisterGUI.RegistrationError") + ": " + e.getMessage(), 
                I18n.t("RegisterGUI.RegistrationError"), 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openLoginGUI() {
        new LoginGUI().setVisible(true);
        dispose();
    }
}