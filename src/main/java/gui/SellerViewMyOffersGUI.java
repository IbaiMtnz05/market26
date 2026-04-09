package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import domain.CriterioDecision;
import domain.DecisionVenta;
import domain.Sale;
import domain.TransaccionPago;

/**
 * GUI window for sellers to view their created sales offers
 * and see which ones have been accepted by buyers.
 */
public class SellerViewMyOffersGUI extends JFrame {
    private String sellerEmail;
    private JTable tableOffers;
    private DefaultTableModel tableModel;
    private JButton btnRefresh, btnDecideBuyer;
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
                   labels.getString("SellerViewMyOffersGUI.ColumnBuyerEmail"),
                   labels.getString("SellerViewMyOffersGUI.ColumnFinalPrice"),
                   labels.getString("SellerViewMyOffersGUI.ColumnAcceptDate"),
                   labels.getString("SellerViewMyOffersGUI.ColumnDecision"),
                   labels.getString("SellerViewMyOffersGUI.ColumnPayment"),
                   labels.getString("SellerViewMyOffersGUI.ColumnOfferId")};
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
        btnDecideBuyer = new JButton(labels.getString("SellerViewMyOffersGUI.DecideButton"));
        pnlButtons.add(btnRefresh);
        pnlButtons.add(btnDecideBuyer);
        mainPanel.add(pnlButtons, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        btnRefresh.addActionListener(e -> loadMyOffers());
        btnDecideBuyer.addActionListener(e -> handleDecideBuyerAndCharge());
        
        loadMyOffers();
    }
    
    private void loadMyOffers() {
        tableModel.setRowCount(0);

        BLFacade facade = MainGUI.getBusinessLogic();
        List<Sale> mySales = facade.getSalesBySellerEmail(sellerEmail);
        List<AcceptedOffer> acceptances = facade.getAcceptedOffersBySeller(sellerEmail);
        List<DecisionVenta> decisions = facade.getDecisionVentasBySeller(sellerEmail);

        Map<Integer, List<AcceptedOffer>> offersBySale = new HashMap<>();
        for (AcceptedOffer acceptedOffer : acceptances) {
            Integer saleNumber = acceptedOffer.getSale().getSaleNumber();
            if (!offersBySale.containsKey(saleNumber)) {
                offersBySale.put(saleNumber, new ArrayList<>());
            }
            offersBySale.get(saleNumber).add(acceptedOffer);
        }

        Map<Integer, DecisionVenta> decisionBySale = new HashMap<>();
        for (DecisionVenta decision : decisions) {
            decisionBySale.put(decision.getSaleNumber(), decision);
        }

        Map<Integer, TransaccionPago> paymentBySale = new HashMap<>();
        for (Sale sale : mySales) {
            List<TransaccionPago> payments = facade.getTransaccionesBySale(sale.getSaleNumber());
            for (TransaccionPago payment : payments) {
                TransaccionPago current = paymentBySale.get(sale.getSaleNumber());
                if (current == null || payment.getId() > current.getId()) {
                    paymentBySale.put(sale.getSaleNumber(), payment);
                }
            }
        }

        for (Sale sale : mySales) {
            List<AcceptedOffer> offers = offersBySale.get(sale.getSaleNumber());
            DecisionVenta decision = decisionBySale.get(sale.getSaleNumber());
            TransaccionPago payment = paymentBySale.get(sale.getSaleNumber());
            String decisionStatus = decision != null ? labels.getString("SellerViewMyOffersGUI.Decided") : labels.getString("SellerViewMyOffersGUI.NotDecided");
            String paymentStatus = payment != null ? payment.getEstado().name() : "-";

            if (offers == null || offers.isEmpty()) {
                tableModel.addRow(new Object[]{
                    sale.getSaleNumber(),
                    sale.getTitle(),
                    "€" + sale.getPrice(),
                    labels.getString("SellerViewMyOffersGUI.NotAccepted"),
                    "-",
                    "-",
                    "-",
                    "-",
                    decisionStatus,
                    paymentStatus,
                    null
                });
                continue;
            }

            for (AcceptedOffer accepted : offers) {
                tableModel.addRow(new Object[]{
                    sale.getSaleNumber(),
                    sale.getTitle(),
                    "€" + sale.getPrice(),
                    displayOfferState(accepted.getEstado()),
                    accepted.getBuyer().getName(),
                    accepted.getBuyer().getEmail(),
                    "€" + accepted.getFinalPrice(),
                    dateFormat.format(accepted.getAcceptanceDate()),
                    decisionStatus,
                    paymentStatus,
                    accepted.getId()
                });
            }
        }
    }

    private void handleDecideBuyerAndCharge() {
        int selectedRow = tableOffers.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                labels.getString("SellerViewMyOffersGUI.SelectOffer"),
                labels.getString("SellerViewMyOffersGUI.ErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer acceptedOfferId = (Integer) tableModel.getValueAt(selectedRow, 10);
        if (acceptedOfferId == null) {
            JOptionPane.showMessageDialog(this,
                labels.getString("SellerViewMyOffersGUI.NoAcceptedOffer"),
                labels.getString("SellerViewMyOffersGUI.ErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer saleNumber = (Integer) tableModel.getValueAt(selectedRow, 0);
        String offerStatus = String.valueOf(tableModel.getValueAt(selectedRow, 3));
        if (!labels.getString("SellerViewMyOffersGUI.Pending").equals(offerStatus)) {
            JOptionPane.showMessageDialog(this,
                labels.getString("SellerViewMyOffersGUI.AlreadyDecided"),
                labels.getString("SellerViewMyOffersGUI.ErrorTitle"),
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        CriterioDecision[] criterios = CriterioDecision.values();
        JComboBox<CriterioDecision> criterioCombo = new JComboBox<>(criterios);
        int criterioResult = JOptionPane.showConfirmDialog(
            this,
            criterioCombo,
            labels.getString("SellerViewMyOffersGUI.SelectCriteria"),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (criterioResult != JOptionPane.OK_OPTION) {
            return;
        }

        String motivo = JOptionPane.showInputDialog(
            this,
            labels.getString("SellerViewMyOffersGUI.ReasonPrompt"),
            labels.getString("SellerViewMyOffersGUI.ReasonDefault")
        );
        if (motivo == null) {
            return;
        }

        BLFacade facade = MainGUI.getBusinessLogic();
        DecisionVenta decision = facade.decidirComprador(
            saleNumber,
            acceptedOfferId,
            ((CriterioDecision) criterioCombo.getSelectedItem()).name(),
            motivo
        );

        if (decision == null) {
            JOptionPane.showMessageDialog(this,
                labels.getString("SellerViewMyOffersGUI.DecisionError"),
                labels.getString("SellerViewMyOffersGUI.ErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String buyerEmail = String.valueOf(tableModel.getValueAt(selectedRow, 5));
        TransaccionPago selectedPayment = null;
        List<TransaccionPago> payments = facade.getTransaccionesBySale(saleNumber);
        for (TransaccionPago payment : payments) {
            if (payment.getBuyerEmail().equalsIgnoreCase(buyerEmail)) {
                if (selectedPayment == null || payment.getId() > selectedPayment.getId()) {
                    selectedPayment = payment;
                }
            }
        }

        String paymentInfo = selectedPayment != null
            ? selectedPayment.getEstado().name() + " / " + selectedPayment.getReferenciaExterna()
            : labels.getString("SellerViewMyOffersGUI.PaymentNotFound");

        JOptionPane.showMessageDialog(this,
            labels.getString("SellerViewMyOffersGUI.DecisionSuccess") + "\n" +
            labels.getString("SellerViewMyOffersGUI.PaymentResult") + " " + paymentInfo,
            labels.getString("SellerViewMyOffersGUI.SuccessTitle"),
            JOptionPane.INFORMATION_MESSAGE);

        loadMyOffers();
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