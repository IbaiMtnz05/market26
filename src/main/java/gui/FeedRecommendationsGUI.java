package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.List;

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
import domain.Sale;

public class FeedRecommendationsGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private final String buyerEmail;
    private final DefaultTableModel model;
    private final JTable table;
    private final DecimalFormat scoreFormat = new DecimalFormat("0.000");

    public FeedRecommendationsGUI(String buyerEmail) {
        this.buyerEmail = buyerEmail;

        setTitle("Feed personalizado con IA");
        setSize(980, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(new Color(21, 24, 30));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Tu feed recomendado", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 19));
        title.setForeground(new Color(232, 236, 242));
        root.add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[] {
            "Titulo", "Precio", "Categoria", "Score IA", "Motivo", "SaleObj"
        }, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.removeColumn(table.getColumnModel().getColumn(5));
        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton btnRefresh = new JButton("Actualizar feed");
        btnRefresh.addActionListener(e -> refreshFeed());
        actions.add(btnRefresh);

        JButton btnOffer = new JButton("Hacer oferta");
        btnOffer.addActionListener(e -> openOfferDialog());
        actions.add(btnOffer);

        root.add(actions, BorderLayout.SOUTH);
        add(root);

        refreshFeed();
    }

    private void refreshFeed() {
        BLFacade facade = MainGUI.getBusinessLogic();
        List<Sale> feed = facade.getPersonalizedFeed(buyerEmail, 30);

        model.setRowCount(0);
        for (Sale sale : feed) {
            float score = facade.getRecommendationScore(buyerEmail, sale.getSaleNumber());
            String reason = facade.getRecommendationReason(buyerEmail, sale.getSaleNumber());
            model.addRow(new Object[] {
                sale.getTitle(),
                sale.getPrice(),
                facade.getCategoryForSale(sale.getSaleNumber()),
                scoreFormat.format(score),
                reason,
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

        Sale sale = (Sale) model.getValueAt(row, 5);
        new AcceptOfferGUI(buyerEmail, sale.getSaleNumber()).setVisible(true);
    }
}
