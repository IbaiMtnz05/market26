package gui;

import java.awt.BorderLayout;
import java.awt.Font;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import businessLogic.BLFacade;
import domain.ComisionMarketplace;
import domain.Sale;
import domain.TransaccionPago;

/**
 * Seller screen to review confirmed payments and calculate marketplace commissions.
 */
public class SellerCommissionsGUI extends JFrame {
    private final String sellerEmail;
    private final ResourceBundle labels = ResourceBundle.getBundle("Etiquetas");

    private final JTable tableCommissions;
    private final DefaultTableModel tableModel;
    private final JTextField txtPercentage = new JTextField(8);
    private final JButton btnRefresh;
    private final JButton btnCalculate;
    private final JButton btnLiquidate;

    public SellerCommissionsGUI(String sellerEmail) {
        this.sellerEmail = sellerEmail;

        setTitle(labels.getString("SellerCommissionsGUI.Title"));
        setSize(1100, 460);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel(labels.getString("SellerCommissionsGUI.Header") + " " + sellerEmail);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {
            labels.getString("SellerCommissionsGUI.ColumnSale"),
            labels.getString("SellerCommissionsGUI.ColumnTitle"),
            labels.getString("SellerCommissionsGUI.ColumnPaymentId"),
            labels.getString("SellerCommissionsGUI.ColumnBuyer"),
            labels.getString("SellerCommissionsGUI.ColumnAmount"),
            labels.getString("SellerCommissionsGUI.ColumnPaymentStatus"),
            labels.getString("SellerCommissionsGUI.ColumnCommissionId"),
            labels.getString("SellerCommissionsGUI.ColumnPercentage"),
            labels.getString("SellerCommissionsGUI.ColumnNetAmount"),
            labels.getString("SellerCommissionsGUI.ColumnCommissionStatus")
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableCommissions = new JTable(tableModel);
        mainPanel.add(new JScrollPane(tableCommissions), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel(labels.getString("SellerCommissionsGUI.PercentageLabel")));
        txtPercentage.setText("10");
        bottomPanel.add(txtPercentage);

        btnCalculate = new JButton(labels.getString("SellerCommissionsGUI.CalculateButton"));
        btnLiquidate = new JButton(labels.getString("SellerCommissionsGUI.LiquidateButton"));
        btnRefresh = new JButton(labels.getString("SellerCommissionsGUI.RefreshButton"));
        JButton btnClose = new JButton(labels.getString("Close"));

        btnCalculate.addActionListener(e -> handleCalculateCommission());
        btnLiquidate.addActionListener(e -> handleLiquidateCommission());
        btnRefresh.addActionListener(e -> loadData());
        btnClose.addActionListener(e -> dispose());

        bottomPanel.add(btnCalculate);
        bottomPanel.add(btnLiquidate);
        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnClose);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        loadData();
    }

    private void handleLiquidateCommission() {
        int selectedRow = tableCommissions.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                labels.getString("SellerCommissionsGUI.SelectCommission"),
                labels.getString("SellerCommissionsGUI.ErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer commissionId = (Integer) tableModel.getValueAt(selectedRow, 6);
        Integer paymentId = (Integer) tableModel.getValueAt(selectedRow, 2);
        if (paymentId == null) {
            JOptionPane.showMessageDialog(this,
                labels.getString("SellerCommissionsGUI.NoPayment"),
                labels.getString("SellerCommissionsGUI.ErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        float percentage = parsePercentage();
        if (percentage <= 0) {
            return;
        }

        BLFacade facade = MainGUI.getBusinessLogic();
        ComisionMarketplace commission;

        if (commissionId == null) {
            commission = facade.calcularComision(paymentId, percentage);
            if (commission == null) {
                JOptionPane.showMessageDialog(this,
                    labels.getString("SellerCommissionsGUI.CommissionError"),
                    labels.getString("SellerCommissionsGUI.ErrorTitle"),
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            commission = facade.calcularComision(paymentId, percentage);
            if (commission == null) {
                JOptionPane.showMessageDialog(this,
                    labels.getString("SellerCommissionsGUI.CommissionError"),
                    labels.getString("SellerCommissionsGUI.ErrorTitle"),
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (commission.getEstado() != null && isCommissionState(commission.getEstado(), "ComisionMarketplace.Estado.LIQUIDADA")) {
            JOptionPane.showMessageDialog(this,
                labels.getString("SellerCommissionsGUI.AlreadyLiquidated"),
                labels.getString("SellerCommissionsGUI.SuccessTitle"),
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        commission = facade.liquidarComision(commission.getId());
        if (commission == null) {
            JOptionPane.showMessageDialog(this,
                labels.getString("SellerCommissionsGUI.LiquidationError"),
                labels.getString("SellerCommissionsGUI.ErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
            labels.getString("SellerCommissionsGUI.LiquidationOk") + "\n" +
            labels.getString("SellerCommissionsGUI.CommissionAmount") + " " + String.format("%.2f€", commission.getImporteComision()) + "\n" +
            labels.getString("SellerCommissionsGUI.NetAmount") + " " + String.format("%.2f€", commission.getImporteNeto()),
            labels.getString("SellerCommissionsGUI.SuccessTitle"),
            JOptionPane.INFORMATION_MESSAGE);

        loadData();
        if (selectedRow >= 0 && selectedRow < tableCommissions.getRowCount()) {
            tableCommissions.setRowSelectionInterval(selectedRow, selectedRow);
        }
        tableCommissions.revalidate();
        tableCommissions.repaint();
    }

    private float parsePercentage() {
        try {
            float percentage = Float.parseFloat(txtPercentage.getText().trim().replace(',', '.'));
            if (percentage <= 0) {
                JOptionPane.showMessageDialog(this,
                    labels.getString("SellerCommissionsGUI.InvalidPercentage"),
                    labels.getString("SellerCommissionsGUI.ErrorTitle"),
                    JOptionPane.ERROR_MESSAGE);
                return -1;
            }
            return percentage;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                labels.getString("SellerCommissionsGUI.InvalidPercentage"),
                labels.getString("SellerCommissionsGUI.ErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);

        BLFacade facade = MainGUI.getBusinessLogic();
        List<Sale> sales = facade.getSalesBySellerEmail(sellerEmail);
        List<ComisionMarketplace> commissions = facade.getComisionesBySeller(sellerEmail);

        Map<Integer, ComisionMarketplace> commissionByPaymentId = new HashMap<>();
        for (ComisionMarketplace commission : commissions) {
            commissionByPaymentId.put(commission.getTransaccionPagoId(), commission);
        }

        for (Sale sale : sales) {
            List<TransaccionPago> payments = facade.getTransaccionesBySale(sale.getSaleNumber());
            TransaccionPago latestPayment = null;
            for (TransaccionPago payment : payments) {
                if (latestPayment == null || payment.getId() > latestPayment.getId()) {
                    latestPayment = payment;
                }
            }

            if (latestPayment == null) {
                tableModel.addRow(new Object[]{
                    sale.getSaleNumber(),
                    sale.getTitle(),
                    null,
                    "-",
                    "-",
                    "-",
                    null,
                    "-",
                    "-",
                    labels.getString("SellerCommissionsGUI.NoCommission")
                });
                continue;
            }

            ComisionMarketplace commission = commissionByPaymentId.get(latestPayment.getId());
            tableModel.addRow(new Object[]{
                sale.getSaleNumber(),
                sale.getTitle(),
                latestPayment.getId(),
                latestPayment.getBuyerEmail(),
                String.format("%.2f€", latestPayment.getImporte()),
                latestPayment.getEstado().name(),
                commission != null ? commission.getId() : null,
                commission != null ? String.format("%.2f%%", commission.getPorcentajeComision()) : "-",
                commission != null ? String.format("%.2f€", commission.getImporteNeto()) : "-",
                commission != null ? displayCommissionState(commission.getEstado()) : labels.getString("SellerCommissionsGUI.NoCommission")
            });
        }
    }

    private String displayCommissionState(String stateKeyOrLabel) {
        if (stateKeyOrLabel == null) {
            return labels.getString("SellerCommissionsGUI.NoCommission");
        }
        if (labels.containsKey(stateKeyOrLabel)) {
            return labels.getString(stateKeyOrLabel);
        }
        return stateKeyOrLabel;
    }

    private boolean isCommissionState(String value, String expectedKey) {
        if (value == null) {
            return false;
        }
        if (expectedKey.equals(value)) {
            return true;
        }
        return labels.containsKey(expectedKey) && labels.getString(expectedKey).equals(value);
    }

    private void handleCalculateCommission() {
        int selectedRow = tableCommissions.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                labels.getString("SellerCommissionsGUI.SelectPayment"),
                labels.getString("SellerCommissionsGUI.ErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer paymentId = (Integer) tableModel.getValueAt(selectedRow, 2);
        if (paymentId == null) {
            JOptionPane.showMessageDialog(this,
                labels.getString("SellerCommissionsGUI.NoPayment"),
                labels.getString("SellerCommissionsGUI.ErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        float percentage;
        try {
            percentage = Float.parseFloat(txtPercentage.getText().trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                labels.getString("SellerCommissionsGUI.InvalidPercentage"),
                labels.getString("SellerCommissionsGUI.ErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (percentage <= 0) {
            JOptionPane.showMessageDialog(this,
                labels.getString("SellerCommissionsGUI.InvalidPercentage"),
                labels.getString("SellerCommissionsGUI.ErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        BLFacade facade = MainGUI.getBusinessLogic();
        ComisionMarketplace commission = facade.calcularComision(paymentId, percentage);
        if (commission == null) {
            JOptionPane.showMessageDialog(this,
                labels.getString("SellerCommissionsGUI.CommissionError"),
                labels.getString("SellerCommissionsGUI.ErrorTitle"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
            labels.getString("SellerCommissionsGUI.CommissionOk") + "\n" +
            labels.getString("SellerCommissionsGUI.CommissionAmount") + " " + String.format("%.2f€", commission.getImporteComision()) + "\n" +
            labels.getString("SellerCommissionsGUI.NetAmount") + " " + String.format("%.2f€", commission.getImporteNeto()),
            labels.getString("SellerCommissionsGUI.SuccessTitle"),
            JOptionPane.INFORMATION_MESSAGE);

        loadData();
        tableCommissions.revalidate();
        tableCommissions.repaint();
    }
}