package gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import businessLogic.BLFacade;
import businessLogic.BLFacadeImplementation;
import domain.AcceptedOffer;
import domain.Reembolso;
import domain.Sale;
import domain.TransaccionPago;

public class GestionarReembolsoGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtSaleNumber;
    private JTextField txtImporte;
    private JTextField txtMotivo;
    private JTextArea txtObservaciones;
    private JTable tableSales;
    private DefaultTableModel tableModel;
    private final Map<Integer, Float> importePorVenta = new HashMap<>();
    
    private BLFacade facade;
    private String vendedorEmail;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GestionarReembolsoGUI frame = new GestionarReembolsoGUI("vendedor@ejemplo.com");
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public GestionarReembolsoGUI(String emailVendedor) {
        this.vendedorEmail = emailVendedor;
        facade = MainGUI.getBusinessLogic();
        if (facade == null) {
            facade = new BLFacadeImplementation();
        }
        
        setTitle("Gestionar Reembolso - Incidencias Postventa");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        // Título
        JLabel lblTitulo = new JLabel("Gestión de Reembolsos por Incidencia");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTitulo.setBounds(250, 10, 400, 30);
        contentPane.add(lblTitulo);
        
        // Sección de ventas del vendedor
        JLabel lblVentas = new JLabel("Mis Ventas con Transacciones Confirmadas:");
        lblVentas.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblVentas.setBounds(20, 50, 300, 20);
        contentPane.add(lblVentas);
        
        // Tabla de ventas
        tableModel = new DefaultTableModel(
            new Object[][]{},
            new String[]{"Nº Venta", "Título", "Precio", "Comprador", "Estado"}
        );
        tableSales = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableSales);
        scrollPane.setBounds(20, 80, 550, 200);
        contentPane.add(scrollPane);
        
        // Botón cargar ventas
        JButton btnCargarVentas = new JButton("Cargar Mis Ventas");
        btnCargarVentas.setBounds(20, 290, 150, 30);
        btnCargarVentas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarVentas();
            }
        });
        contentPane.add(btnCargarVentas);
        
        // Panel de datos del reembolso
        JLabel lblDatosReembolso = new JLabel("Datos del Reembolso:");
        lblDatosReembolso.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblDatosReembolso.setBounds(620, 50, 200, 20);
        contentPane.add(lblDatosReembolso);
        
        JLabel lblSaleNumber = new JLabel("Nº Venta:");
        lblSaleNumber.setBounds(620, 80, 80, 20);
        contentPane.add(lblSaleNumber);
        
        txtSaleNumber = new JTextField();
        txtSaleNumber.setBounds(710, 80, 150, 25);
        txtSaleNumber.setEditable(false);
        contentPane.add(txtSaleNumber);
        
        JLabel lblImporte = new JLabel("Importe Reembolso (€):");
        lblImporte.setBounds(620, 120, 130, 20);
        contentPane.add(lblImporte);
        
        txtImporte = new JTextField();
        txtImporte.setBounds(760, 120, 100, 25);
        txtImporte.setEditable(false);
        contentPane.add(txtImporte);
        
        JLabel lblMotivo = new JLabel("Motivo:");
        lblMotivo.setBounds(620, 160, 80, 20);
        contentPane.add(lblMotivo);
        
        txtMotivo = new JTextField();
        txtMotivo.setBounds(710, 160, 150, 25);
        contentPane.add(txtMotivo);
        
        JLabel lblObservaciones = new JLabel("Observaciones:");
        lblObservaciones.setBounds(620, 200, 100, 20);
        contentPane.add(lblObservaciones);
        
        txtObservaciones = new JTextArea();
        txtObservaciones.setBounds(620, 230, 240, 80);
        contentPane.add(txtObservaciones);
        
        // Botón gestionar reembolso
        JButton btnGestionarReembolso = new JButton("Solicitar Reembolso");
        btnGestionarReembolso.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnGestionarReembolso.setBounds(660, 330, 180, 40);
        btnGestionarReembolso.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gestionarReembolso();
            }
        });
        contentPane.add(btnGestionarReembolso);
        
        // Botón limpiar
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(660, 380, 180, 30);
        btnLimpiar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });
        contentPane.add(btnLimpiar);
        
        // Cargar ventas al iniciar
        cargarVentas();
        
        // Seleccionar fila de la tabla
        tableSales.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = tableSales.getSelectedRow();
            if (selectedRow >= 0) {
                Integer saleNumber = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                txtSaleNumber.setText(String.valueOf(saleNumber));
                Float importeAceptado = importePorVenta.get(saleNumber);
                txtImporte.setText(importeAceptado != null ? String.valueOf(importeAceptado) : "");
            }
        });
    }
    
    private void cargarVentas() {
        try {
            // Limpiar tabla
            tableModel.setRowCount(0);
            importePorVenta.clear();
            
            // Obtener ventas del vendedor
            List<Sale> ventas = facade.getSalesBySellerEmail(vendedorEmail);
            List<AcceptedOffer> ofertasAceptadas = facade.getAcceptedOffersBySeller(vendedorEmail);
            Map<Integer, AcceptedOffer> ofertaAceptadaPorVenta = new HashMap<>();

            for (AcceptedOffer oferta : ofertasAceptadas) {
                if (oferta.getSale() != null && oferta.getEstado() == AcceptedOffer.EstadoOferta.ACEPTADA) {
                    ofertaAceptadaPorVenta.put(oferta.getSale().getSaleNumber(), oferta);
                }
            }
            
            for (Sale venta : ventas) {
                // Verificar si tiene transacción confirmada
                List<TransaccionPago> transacciones = facade.getTransaccionesBySale(venta.getSaleNumber());
                boolean tieneTransaccionConfirmada = transacciones.stream()
                    .anyMatch(t -> t.getEstado() == TransaccionPago.EstadoPago.CONFIRMADO);
                
                if (tieneTransaccionConfirmada) {
                    AcceptedOffer ofertaAceptada = ofertaAceptadaPorVenta.get(venta.getSaleNumber());
                    float importeAceptado = ofertaAceptada != null ? ofertaAceptada.getFinalPrice() : venta.getPrice();
                    importePorVenta.put(venta.getSaleNumber(), importeAceptado);
                    String estado = "Pendiente";
                    // Verificar si ya tiene reembolso
                    // Nota: Necesitarías un método para obtener reembolsos por venta
                    tableModel.addRow(new Object[]{
                        venta.getSaleNumber(),
                        venta.getTitle(),
                        importeAceptado,
                        "Comprador", // Idealmente obtener el comprador
                        estado
                    });
                }
            }
            
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No hay ventas con transacciones confirmadas para reembolso.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar ventas: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void gestionarReembolso() {
        try {
            // Validar campos
            if (txtSaleNumber.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Seleccione una venta de la tabla.", 
                    "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Integer saleNumber = Integer.parseInt(txtSaleNumber.getText());
            float importeReembolso;
            
            try {
                importeReembolso = Float.parseFloat(txtImporte.getText());
                if (importeReembolso <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "El importe debe ser un número positivo.", 
                    "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String motivo = txtMotivo.getText().trim();
            if (motivo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe especificar un motivo.", 
                    "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String observaciones = txtObservaciones.getText().trim();
            
            // Confirmar antes de procesar
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de solicitar un reembolso de " + importeReembolso + "€ para la venta Nº " + saleNumber + "?\n" +
                "Motivo: " + motivo,
                "Confirmar Reembolso",
                JOptionPane.YES_NO_OPTION);
            
            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }
            
            // Llamar al método de negocio
            Reembolso reembolso = facade.gestionarReembolsoPorVendedor(
                saleNumber,
                vendedorEmail,
                importeReembolso,
                motivo,
                observaciones
            );
            
            if (reembolso != null) {
                JOptionPane.showMessageDialog(this,
                    "✅ Reembolso solicitado correctamente.\n" +
                    "ID: " + reembolso.getId() + "\n" +
                    "Tipo: " + reembolso.getTipo() + "\n" +
                    "Importe: " + reembolso.getImporte() + "€\n" +
                    "Estado: " + reembolso.getEstado(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                
                // Recargar ventas y limpiar
                cargarVentas();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo procesar el reembolso. Verifique que la venta cumple las condiciones.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(this,
                "Error de seguridad: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error inesperado: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void limpiarCampos() {
        txtSaleNumber.setText("");
        txtImporte.setText("");
        txtMotivo.setText("");
        txtObservaciones.setText("");
        tableSales.clearSelection();
    }
}
