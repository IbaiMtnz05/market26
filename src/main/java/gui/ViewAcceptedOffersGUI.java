package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import businessLogic.BLFacade;
import domain.AcceptedOffer;
import domain.Reembolso;
import domain.TransaccionPago;

/**
 * GUI window for buyers to view all the offers they have accepted.
 * Displays offer details including final price and acceptance date.
 */
public class ViewAcceptedOffersGUI extends JFrame {
    private String buyerEmail;
    private JTable tableAccepted;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;
    private ResourceBundle labels = ResourceBundle.getBundle("Etiquetas");
    
    /**
     * Creates the accepted offers view window for the specified buyer.
     * 
     * @param buyerEmail the email of the buyer viewing their accepted offers
     */
    public ViewAcceptedOffersGUI(String buyerEmail) {
        this.buyerEmail = buyerEmail;
        
        setTitle(labels.getString("ViewAcceptedOffersGUI.Title"));
        setSize(800, 400);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(
                           10, 10, 10, 10));
        
        JLabel lblTitle = new JLabel(
            labels.getString("ViewAcceptedOffersGUI.MyAcceptedOffers") + " " + buyerEmail);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblTitle, BorderLayout.NORTH);
        
        String[] columns = {labels.getString("ViewAcceptedOffersGUI.ColumnOffer"), 
                           labels.getString("ViewAcceptedOffersGUI.ColumnPrice"), 
                           labels.getString("ViewAcceptedOffersGUI.ColumnStatus"),
                           labels.getString("ViewAcceptedOffersGUI.ColumnRefundStatus"),
                           labels.getString("ViewAcceptedOffersGUI.ColumnRefundAmount"),
                           labels.getString("ViewAcceptedOffersGUI.ColumnBuyer"), 
                           labels.getString("ViewAcceptedOffersGUI.ColumnBuyerEmail"), 
                           labels.getString("ViewAcceptedOffersGUI.ColumnAcceptDate")};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableAccepted = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableAccepted);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel pnlButtons = new JPanel();
        btnRefresh = new JButton(labels.getString("ViewAcceptedOffersGUI.RefreshButton"));
        pnlButtons.add(btnRefresh);
        mainPanel.add(pnlButtons, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        btnRefresh.addActionListener(e -> loadAcceptedOffers());
        
        loadAcceptedOffers();
    }
    
    private void loadAcceptedOffers() {
        tableModel.setRowCount(0);
        
        BLFacade facade = MainGUI.getBusinessLogic();
        List<AcceptedOffer> accepted = 
            facade.getAcceptedOffersByBuyer(buyerEmail);
        List<Reembolso> reembolsos = facade.getReembolsosByBuyer(buyerEmail);

        Map<Integer, Reembolso> reembolsoByTransaccion = new HashMap<>();
        for (Reembolso reembolso : reembolsos) {
            if (reembolso.getTransaccionPagoId() == null) {
                continue;
            }
            Reembolso current = reembolsoByTransaccion.get(reembolso.getTransaccionPagoId());
            if (current == null || (reembolso.getId() != null && current.getId() != null && reembolso.getId() > current.getId())) {
                reembolsoByTransaccion.put(reembolso.getTransaccionPagoId(), reembolso);
            }
        }
        
        SimpleDateFormat dateFormat = 
            new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (AcceptedOffer offer : accepted) {
            Reembolso reembolsoOferta = null;
            List<TransaccionPago> transacciones = facade.getTransaccionesBySale(offer.getSale().getSaleNumber());
            for (TransaccionPago transaccion : transacciones) {
                if (!buyerEmail.equalsIgnoreCase(transaccion.getBuyerEmail())) {
                    continue;
                }
                Reembolso encontrado = reembolsoByTransaccion.get(transaccion.getId());
                if (encontrado != null) {
                    if (reembolsoOferta == null ||
                        (encontrado.getId() != null && reembolsoOferta.getId() != null && encontrado.getId() > reembolsoOferta.getId()) ||
                        (reembolsoOferta.getId() == null && encontrado.getId() != null)) {
                        reembolsoOferta = encontrado;
                    }
                }
            }

            Object[] row = {
                offer.getSale().getTitle(),
                String.format("%.2f€", offer.getFinalPrice()),
                displayOfferState(offer.getEstado()),
                displayRefundState(reembolsoOferta),
                reembolsoOferta != null ? String.format("%.2f€", reembolsoOferta.getImporte()) : "-",
                offer.getBuyer().getName(),
                offer.getBuyer().getEmail(),
                dateFormat.format(offer.getAcceptanceDate())
            };
            tableModel.addRow(row);
        }

        if (accepted.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                labels.getString("ViewAcceptedOffersGUI.NoOffers"), 
                labels.getString("Accept"), JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String displayRefundState(Reembolso reembolso) {
        if (reembolso == null) {
            return "-";
        }

        String estado = reembolso.getEstado();
        if (estado == null || estado.trim().isEmpty()) {
            return labels.getString("EstadoReembolso.PENDIENTE");
        }

        if (labels.containsKey(estado)) {
            return labels.getString(estado);
        }

        String estadoKey = "EstadoReembolso." + estado;
        if (labels.containsKey(estadoKey)) {
            return labels.getString(estadoKey);
        }

        return estado;
    }

    private String displayOfferState(AcceptedOffer.EstadoOferta estado) {
        if (estado == null || estado == AcceptedOffer.EstadoOferta.PENDIENTE) {
            return labels.getString("SellerViewMyOffersGUI.Pending");
        }
        if (estado == AcceptedOffer.EstadoOferta.ACEPTADA) {
            return labels.getString("SellerViewMyOffersGUI.Accepted");
        }
        return labels.getString("SellerViewMyOffersGUI.NotAccepted");
    }
}