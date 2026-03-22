package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

/**
 * Main GUI window for buyer users.
 * Provides access to accept offers, view available sales, and view accepted offers.
 */
public class BuyerMainGUI extends JFrame {
    private String buyerEmail;
    private JButton btnAcceptOffers;
    private JButton btnViewAvailable;
    private JButton btnViewAccepted;
    private JButton btnLogout;
    private JLabel lblWelcome;
    private ResourceBundle labels = ResourceBundle.getBundle("Etiquetas");
    
    public BuyerMainGUI(String buyerEmail) {
        this.buyerEmail = buyerEmail;
        
        setTitle(labels.getString("BuyerMainGUI.Title"));
        setSize(560, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel superior con bienvenida
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 250));
        lblWelcome = new JLabel(labels.getString("BuyerMainGUI.Welcome") + " " + buyerEmail);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(lblWelcome);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Panel central con botones
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(245, 247, 250));
        centerPanel.setLayout(new GridLayout(4, 1, 10, 10));
        
        btnAcceptOffers = new JButton(labels.getString("BuyerMainGUI.AcceptOffers"));
        btnAcceptOffers.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAcceptOffers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame acceptGUI = new AcceptOfferGUI(buyerEmail);
                acceptGUI.setVisible(true);
            }
        });
        centerPanel.add(btnAcceptOffers);

        btnViewAvailable = new JButton(labels.getString("BuyerMainGUI.ViewAvailable"));
        btnViewAvailable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnViewAvailable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame viewSalesGUI = new QuerySalesGUI();
                viewSalesGUI.setVisible(true);
            }
        });
        centerPanel.add(btnViewAvailable);
        
        btnViewAccepted = new JButton(labels.getString("BuyerMainGUI.ViewAccepted"));
        btnViewAccepted.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnViewAccepted.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame viewGUI = new ViewAcceptedOffersGUI(buyerEmail);
                viewGUI.setVisible(true);
            }
        });
        centerPanel.add(btnViewAccepted);
        
        btnLogout = new JButton(labels.getString("BuyerMainGUI.Logout"));
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame loginGUI = new LoginGUI();
                loginGUI.setVisible(true);
                dispose();
            }
        });
        centerPanel.add(btnLogout);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
}
