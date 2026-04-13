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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import businessLogic.BLFacade;
import domain.Sale;

public class CategoryManagerGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private final String sellerEmail;
    private final DefaultTableModel tableModel;
    private final JTable tableSales;
    private final JComboBox<String> cmbCategories;
    private final DefaultComboBoxModel<String> categoryModel;

    public CategoryManagerGUI(String sellerEmail) {
        this.sellerEmail = sellerEmail;

        setTitle("Categorias dinamicas");
        setSize(860, 500);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(new Color(21, 24, 30));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Gestion de categorias", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 19));
        title.setForeground(new Color(232, 236, 242));
        root.add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[] {"Venta", "Titulo", "Precio", "Categoria"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableSales = new JTable(tableModel);
        root.add(new JScrollPane(tableSales), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setOpaque(false);

        JPanel assignPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        assignPanel.setOpaque(false);
        categoryModel = new DefaultComboBoxModel<String>();
        cmbCategories = new JComboBox<String>(categoryModel);
        cmbCategories.setPrototypeDisplayValue("Categoria muy larga");
        assignPanel.add(new JLabel("Categoria:"));
        assignPanel.add(cmbCategories);

        JButton btnAssign = new JButton("Asignar a venta seleccionada");
        btnAssign.addActionListener(e -> assignCategoryToSelectedSale());
        assignPanel.add(btnAssign);

        JButton btnSuggest = new JButton("Sugerir por titulo");
        btnSuggest.addActionListener(e -> suggestForSelectedSale());
        assignPanel.add(btnSuggest);

        bottom.add(assignPanel, BorderLayout.NORTH);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionsPanel.setOpaque(false);

        JButton btnPropose = new JButton("Proponer categoria");
        btnPropose.addActionListener(e -> proposeCategory());
        actionsPanel.add(btnPropose);

        JButton btnApprove = new JButton("Aprobar pendientes");
        btnApprove.addActionListener(e -> approvePendingCategories());
        actionsPanel.add(btnApprove);

        JButton btnRefresh = new JButton("Actualizar");
        btnRefresh.addActionListener(e -> refreshData());
        actionsPanel.add(btnRefresh);

        bottom.add(actionsPanel, BorderLayout.SOUTH);

        root.add(bottom, BorderLayout.SOUTH);
        add(root);

        refreshData();
    }

    private void refreshData() {
        BLFacade facade = MainGUI.getBusinessLogic();

        categoryModel.removeAllElements();
        for (String category : facade.getAllCategories()) {
            categoryModel.addElement(category);
        }

        tableModel.setRowCount(0);
        List<Sale> mySales = facade.getSalesBySellerEmail(sellerEmail);
        for (Sale sale : mySales) {
            tableModel.addRow(new Object[] {
                sale.getSaleNumber(),
                sale.getTitle(),
                sale.getPrice(),
                facade.getCategoryForSale(sale.getSaleNumber())
            });
        }
    }

    private void assignCategoryToSelectedSale() {
        int row = tableSales.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una venta primero");
            return;
        }

        Integer saleNumber = (Integer) tableModel.getValueAt(row, 0);
        String category = (String) cmbCategories.getSelectedItem();
        if (category == null || category.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoria valida");
            return;
        }

        BLFacade facade = MainGUI.getBusinessLogic();
        facade.assignCategoryToSale(saleNumber, category);
        refreshData();
    }

    private void suggestForSelectedSale() {
        int row = tableSales.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una venta primero");
            return;
        }

        String title = String.valueOf(tableModel.getValueAt(row, 1));
        BLFacade facade = MainGUI.getBusinessLogic();
        List<String> suggestions = facade.suggestCategories(title, title);
        if (!suggestions.isEmpty()) {
            cmbCategories.setSelectedItem(suggestions.get(0));
            JOptionPane.showMessageDialog(this, "Sugerencia IA: " + suggestions.get(0));
        }
    }

    private void proposeCategory() {
        String proposed = JOptionPane.showInputDialog(this, "Nombre de la nueva categoria:");
        if (proposed == null || proposed.trim().isEmpty()) {
            return;
        }

        BLFacade facade = MainGUI.getBusinessLogic();
        facade.proposeCategory(proposed, sellerEmail);
        refreshData();
        JOptionPane.showMessageDialog(this, "Categoria propuesta correctamente");
    }

    private void approvePendingCategories() {
        BLFacade facade = MainGUI.getBusinessLogic();
        List<String> pending = facade.getPendingCategoryProposals();
        if (pending.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay categorias pendientes");
            return;
        }

        int approved = 0;
        for (String category : pending) {
            facade.approveCategory(category);
            approved++;
        }

        refreshData();
        JOptionPane.showMessageDialog(this, "Categorias aprobadas: " + approved);
    }
}
