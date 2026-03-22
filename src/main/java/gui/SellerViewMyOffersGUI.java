package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import businessLogic.BLFacade;
import domain.AcceptedOffer;
import domain.Sale;

/**
 * GUI window for sellers to view their created sales offers
 * and see which ones have been accepted by buyers.
 */
public class SellerViewMyOffersGUI extends JFrame {
    private String sellerEmail;
    private JTable tableOffers;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private ResourceBundle labels = ResourceBundle.getBundle("Etiquetas");
    
    /**
     * Creates the seller offers view window.
     * 
     * @param sellerEmail the email of the seller viewing their offers
     */
    public SellerViewMyOffersGUI(String sellerEmail) {
        this.sellerEmail = sellerEmail;
        
        setTitle(labels.getString("SellerViewMyOffersGUI.Title"));
        setSize(1000, 450);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitle = new JLabel(labels.getString("SellerViewMyOffersGUI.MyOffers"));
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        
        String[] columns = {labels.getString("SellerViewMyOffersGUI.ColumnNumber"), 
                           labels.getString("SellerViewMyOffersGUI.ColumnTitle"), 
                           labels.getString("SellerViewMyOffersGUI.ColumnPrice"), 
                           labels.getString("SellerViewMyOffersGUI.ColumnStatus"), 
                           labels.getString("SellerViewMyOffersGUI.ColumnBuyer"), 
                           labels.getString("SellerViewMyOffersGUI.ColumnFinalPrice"), 
                           labels.getString("SellerViewMyOffersGUI.ColumnAcceptDate")};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableOffers = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableOffers);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel pnlButtons = new JPanel();
        btnRefresh = new JButton(labels.getString("SellerViewMyOffersGUI.RefreshButton"));
        pnlButtons.add(btnRefresh);
        mainPanel.add(pnlButtons, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        btnRefresh.addActionListener(e -> loadMyOffers());
        
        loadMyOffers();
    }
    
    private void loadMyOffers() {
        tableModel.setRowCount(0);
        
        BLFacade facade = MainGUI.getBusinessLogic();
        
        List<Sale> mySales = facade.getSalesBySellerEmail(sellerEmail);
        
        for (Sale sale : mySales) {
            List<AcceptedOffer> acceptances = 
                facade.getAcceptedOffersBySeller(sellerEmail);
            
            AcceptedOffer accepted = acceptances.stream().filter(a -> a.getSale().getSaleNumber().equals(sale.getSaleNumber())).findFirst().orElse(null);
            
            if (accepted != null) {
                tableModel.addRow(new Object[]{
                    sale.getSaleNumber(),
                    sale.getTitle(),
                    "€" + sale.getPrice(),
                    "✓",
                    accepted.getBuyer().getName(),
                    "€" + accepted.getFinalPrice(),
                    dateFormat.format(accepted.getAcceptanceDate())
                });
            } else {
                tableModel.addRow(new Object[]{
                    sale.getSaleNumber(),
                    sale.getTitle(),
                    "€" + sale.getPrice(),
                    "✗",
                    "-",
                    "-",
                    "-"
                });
            }
        }
    }
}