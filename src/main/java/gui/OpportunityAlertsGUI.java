package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import domain.Sale;

public class OpportunityAlertsGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private final String buyerEmail;
    private final JComboBox<String> cmbCategory;
    private final JTextField txtKeyword;
    private final JTextField txtMaxPrice;
    private final DefaultTableModel model;
    private final JTable table;

    public OpportunityAlertsGUI(String buyerEmail) {
        this.buyerEmail = buyerEmail;

        setTitle("Alertas de oportunidades");
        setSize(980, 540);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(new Color(21, 24, 30));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topPanel = new JPanel(new BorderLayout(6, 6));
        topPanel.setOpaque(false);

        JLabel title = new JLabel("Oportunidades para ti", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 19));
        title.setForeground(new Color(232, 236, 242));
        topPanel.add(title, BorderLayout.NORTH);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.setOpaque(false);

        filters.add(new JLabel("Categoria"));
        cmbCategory = new JComboBox<String>();
        filters.add(cmbCategory);

        filters.add(new JLabel("Keyword"));
        txtKeyword = new JTextField(12);
        filters.add(txtKeyword);

        filters.add(new JLabel("Precio max"));
        txtMaxPrice = new JTextField(7);
        filters.add(txtMaxPrice);

        JButton btnSearch = new JButton("Buscar alertas");
        btnSearch.addActionListener(e -> searchAlerts());
        filters.add(btnSearch);

        topPanel.add(filters, BorderLayout.SOUTH);
        root.add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[] {
            "Titulo", "Precio", "Categoria", "Motivo", "SaleObj"
        }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(4));
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton btnOffer = new JButton("Hacer oferta");
        btnOffer.addActionListener(e -> openOfferDialog());
        actions.add(btnOffer);

        root.add(actions, BorderLayout.SOUTH);
        add(root);

        loadCategories();
        searchAlerts();
    }

    private void loadCategories() {
        BLFacade facade = MainGUI.getBusinessLogic();
        DefaultComboBoxModel<String> modelCat = new DefaultComboBoxModel<String>();
        modelCat.addElement("Todas");
        for (String category : facade.getAllCategories()) {
            modelCat.addElement(category);
        }
        cmbCategory.setModel(modelCat);
    }

    private void searchAlerts() {
        BLFacade facade = MainGUI.getBusinessLogic();
        String category = (String) cmbCategory.getSelectedItem();
        if ("Todas".equalsIgnoreCase(category)) {
            category = null;
        }
        String keyword = txtKeyword.getText();

        Float maxPrice = null;
        String priceText = txtMaxPrice.getText().trim();
        if (!priceText.isEmpty()) {
            try {
                maxPrice = Float.parseFloat(priceText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Precio maximo invalido");
                return;
            }
        }

        List<Sale> alerts = facade.getOpportunityAlerts(buyerEmail, category, keyword, maxPrice, 30);
        model.setRowCount(0);
        for (Sale sale : alerts) {
            model.addRow(new Object[] {
                sale.getTitle(),
                sale.getPrice(),
                facade.getCategoryForSale(sale.getSaleNumber()),
                facade.getOpportunityReason(buyerEmail, sale.getSaleNumber()),
                sale
            });
        }
    }

    private void openOfferDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto primero");
            return;
        }

        Sale sale = (Sale) model.getValueAt(row, 4);
        new AcceptOfferGUI(buyerEmail, sale.getSaleNumber()).setVisible(true);
    }
}
