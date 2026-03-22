package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import businessLogic.BLFacade;
import configuration.UtilDate;
import domain.AcceptedOffer;
import domain.Sale;
import exceptions.InvalidPriceException;

/**
 * GUI window for buyers to view and accept available sales offers.
 * Allows optional price negotiation when accepting an offer.
 */
public class AcceptOfferGUI extends JFrame {
    private String buyerEmail;
    private JTable tableSales;
    private DefaultTableModel tableModel;
    private JButton btnAccept, btnRefresh;
    private JTextField txtNegotiatedPrice;
    private ResourceBundle labels = ResourceBundle.getBundle("Etiquetas");
    
    /**
     * Creates the accept offer window for the specified buyer.
     * 
     * @param buyerEmail the email of the buyer viewing offers
     */
    public AcceptOfferGUI(String buyerEmail) {
        this.buyerEmail = buyerEmail;
        
        setTitle(labels.getString("AcceptOfferGUI.Title"));
        setSize(900, 450);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitle = new JLabel(labels.getString("AcceptOfferGUI.AvailableOffers"));
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        
        String[] columns = {labels.getString("AcceptOfferGUI.ColumnNumber"), 
                           labels.getString("AcceptOfferGUI.ColumnTitle"), 
                           labels.getString("AcceptOfferGUI.ColumnDescription"), 
                           labels.getString("AcceptOfferGUI.ColumnPrice"), 
                           labels.getString("AcceptOfferGUI.ColumnSeller")};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableSales = new JTable(tableModel);
        tableSales.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tableSales);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel pnlBottom = new JPanel(new BorderLayout(10, 10));
        
        JPanel pnlNegotiate = new JPanel();
        pnlNegotiate.add(new JLabel(labels.getString("AcceptOfferGUI.NegotiatedPrice")));
        txtNegotiatedPrice = new JTextField(10);
        txtNegotiatedPrice.setToolTipText(labels.getString("AcceptOfferGUI.NegotiateTip"));
        pnlNegotiate.add(txtNegotiatedPrice);
        pnlBottom.add(pnlNegotiate, BorderLayout.WEST);
        
        JPanel pnlButtons = new JPanel();
        btnAccept = new JButton(labels.getString("AcceptOfferGUI.AcceptButton"));
        btnRefresh = new JButton(labels.getString("AcceptOfferGUI.RefreshButton"));
        pnlButtons.add(btnAccept);
        pnlButtons.add(btnRefresh);
        pnlBottom.add(pnlButtons, BorderLayout.EAST);
        
        mainPanel.add(pnlBottom, BorderLayout.SOUTH);
        add(mainPanel);
        
        btnRefresh.addActionListener(e -> loadAvailableSales());
        btnAccept.addActionListener(e -> handleAcceptOffer());
        
        loadAvailableSales();
    }
    
    private void loadAvailableSales() {
        tableModel.setRowCount(0);
        
        BLFacade facade = MainGUI.getBusinessLogic();
        Date today = UtilDate.trim(new Date());
        
        List<Sale> available = facade.getAvailableSalesForBuyer(today);
        
        for (Sale sale : available) {
            tableModel.addRow(new Object[]{
                sale.getSaleNumber(),
                sale.getTitle(),
                sale.getDescription(),
                "€" + sale.getPrice(),
                sale.getSeller().getName()
            });
        }
    }
    
    private void handleAcceptOffer() {
        int selectedRow = tableSales.getSelectedRow();
        
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                labels.getString("AcceptOfferGUI.SelectOffer"), 
                labels.getString("AcceptOfferGUI.AcceptError"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Integer saleNumber = (Integer) tableModel.getValueAt(selectedRow, 0);
        
        String negotiatedStr = txtNegotiatedPrice.getText().trim();
        Float negotiatedPrice = null;
        
        if (!negotiatedStr.isEmpty()) {
            try {
                negotiatedPrice = Float.parseFloat(negotiatedStr);
                if (negotiatedPrice <= 0) {
                    JOptionPane.showMessageDialog(this, 
                        labels.getString("AcceptOfferGUI.InvalidPrice"), 
                        labels.getString("AcceptOfferGUI.AcceptError"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    labels.getString("AcceptOfferGUI.InvalidPrice"), 
                    labels.getString("AcceptOfferGUI.AcceptError"), JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        BLFacade facade = MainGUI.getBusinessLogic();
        
        try {
            AcceptedOffer result = facade.acceptOffer(buyerEmail, saleNumber, 
                                                      negotiatedPrice);
            
            if (result != null) {
                String msg = labels.getString("AcceptOfferGUI.OfferAccepted") + 
                            (negotiatedPrice != null ? ": €" + negotiatedPrice : "");
                
                JOptionPane.showMessageDialog(this, msg, 
                    labels.getString("Accept"), JOptionPane.INFORMATION_MESSAGE);
                
                txtNegotiatedPrice.setText("");
                loadAvailableSales();
            } else {
                JOptionPane.showMessageDialog(this, 
                    labels.getString("AcceptOfferGUI.OfferAlreadyAccepted"), 
                    labels.getString("AcceptOfferGUI.AcceptError"), JOptionPane.ERROR_MESSAGE);
            }
        } catch (InvalidPriceException e) {
            // Precio negociado inválido
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                labels.getString("AcceptOfferGUI.AcceptError"), 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al aceptar oferta: " + e.getMessage(), 
                labels.getString("AcceptOfferGUI.AcceptError"), JOptionPane.ERROR_MESSAGE);
        }
    }
}