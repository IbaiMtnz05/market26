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
    private JButton btnBrowseAvailable;
    private JButton btnBuyFromList;
    private JButton btnViewAccepted;
    private JButton btnPersonalizedFeed;
    private JButton btnOpportunityAlerts;
    private JButton btnLogout;
    private JLabel lblWelcome;
    private ResourceBundle labels = ResourceBundle.getBundle("Etiquetas");
    
    public BuyerMainGUI(String buyerEmail) {
        this.buyerEmail = buyerEmail;
        
        setTitle(labels.getString("BuyerMainGUI.Title"));
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(21, 24, 30));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel superior con bienvenida
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(21, 24, 30));
        lblWelcome = new JLabel(labels.getString("BuyerMainGUI.Welcome") + " " + buyerEmail);
        lblWelcome.setFont(new Font("Dialog", Font.BOLD, 18));
        lblWelcome.setForeground(new Color(232, 236, 242));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(lblWelcome);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Panel central con botones
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(21, 24, 30));
        centerPanel.setLayout(new GridLayout(7, 1, 10, 10));
        
        btnAcceptOffers = new JButton(labels.getString("BuyerMainGUI.AcceptOffers"));
        btnAcceptOffers.setFont(new Font("Dialog", Font.BOLD, 14));
        btnAcceptOffers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame acceptGUI = new AcceptOfferGUI(buyerEmail);
                acceptGUI.setVisible(true);
            }
        });
        centerPanel.add(btnAcceptOffers);

        btnBrowseAvailable = new JButton(labels.getString("BuyerMainGUI.ViewAvailable"));
        btnBrowseAvailable.setFont(new Font("Dialog", Font.BOLD, 14));
        btnBrowseAvailable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame viewSalesGUI = new QuerySalesGUI();
                viewSalesGUI.setVisible(true);
            }
        });
        centerPanel.add(btnBrowseAvailable);

        btnBuyFromList = new JButton(labels.getString("BuyerMainGUI.BuyFromList"));
        btnBuyFromList.setFont(new Font("Dialog", Font.BOLD, 14));
        btnBuyFromList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame viewSalesGUI = new QuerySalesGUI(buyerEmail);
                viewSalesGUI.setVisible(true);
            }
        });
        centerPanel.add(btnBuyFromList);
        
        btnViewAccepted = new JButton(labels.getString("BuyerMainGUI.ViewAccepted"));
        btnViewAccepted.setFont(new Font("Dialog", Font.BOLD, 14));
        btnViewAccepted.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame viewGUI = new ViewAcceptedOffersGUI(buyerEmail);
                viewGUI.setVisible(true);
            }
        });
        centerPanel.add(btnViewAccepted);

        btnPersonalizedFeed = new JButton("Feed personalizado (IA)");
        btnPersonalizedFeed.setFont(new Font("Dialog", Font.BOLD, 14));
        btnPersonalizedFeed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame feedGUI = new FeedRecommendationsGUI(buyerEmail);
                feedGUI.setVisible(true);
            }
        });
        centerPanel.add(btnPersonalizedFeed);

        btnOpportunityAlerts = new JButton("Alertas de oportunidades");
        btnOpportunityAlerts.setFont(new Font("Dialog", Font.BOLD, 14));
        btnOpportunityAlerts.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame alertsGUI = new OpportunityAlertsGUI(buyerEmail);
                alertsGUI.setVisible(true);
            }
        });
        centerPanel.add(btnOpportunityAlerts);
        
        btnLogout = new JButton(labels.getString("BuyerMainGUI.Logout"));
        btnLogout.setFont(new Font("Dialog", Font.BOLD, 14));
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
