package gui;

/**
 * @author Software Engineering teachers
 */


import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import businessLogic.BLFacade;

/**
 * Main GUI window for seller users.
 * Provides access to create sales, view sales, and manage seller-specific operations.
 */
public class MainGUI extends JFrame {
	
    private String sellerMail;
	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	private JButton jButtonCreateQuery = null;
	private JButton jButtonQueryQueries = null;
	private JButton jButtonLogout = null;
	private JButton jButtonViewMyOffers = null;

    private static BLFacade appFacadeInterface;
	
	public static BLFacade getBusinessLogic(){
		return appFacadeInterface;
	}
	 
	public static void setBussinessLogic (BLFacade facade){
		appFacadeInterface=facade;
	}
	protected JLabel jLabelSelectOption;
	
	/**
	 * This is the default constructor
	 */
	public MainGUI( String mail) {
		super();

		this.sellerMail=mail;
		
		this.setSize(560, 400);
		jLabelSelectOption = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jLabelSelectOption.setFont(new Font("Segoe UI", Font.BOLD, 14));
		jLabelSelectOption.setForeground(Color.BLACK);
		jLabelSelectOption.setHorizontalAlignment(SwingConstants.CENTER);
		
		jButtonCreateQuery = new JButton();
		jButtonCreateQuery.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateSale"));
		jButtonCreateQuery.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		jButtonCreateQuery.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new CreateSaleGUI(sellerMail);
				a.setVisible(true);
			}
		});
		
		jButtonQueryQueries = new JButton();
		jButtonQueryQueries.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.ViewAvailable"));
		jButtonQueryQueries.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		jButtonQueryQueries.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new QuerySalesGUI();
				a.setVisible(true);
			}
		});

		jButtonViewMyOffers = new JButton();
		jButtonViewMyOffers.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.ViewMyOffers"));
		jButtonViewMyOffers.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		jButtonViewMyOffers.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame sellerViewGUI = new SellerViewMyOffersGUI(sellerMail);
				sellerViewGUI.setVisible(true);
			}
		});
		
		jButtonLogout = new JButton();
		jButtonLogout.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Logout"));
		jButtonLogout.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		jButtonLogout.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame loginGUI = new LoginGUI();
				loginGUI.setVisible(true);
				dispose();
			}
		});

		jContentPane = new JPanel();
		jContentPane.setBackground(new Color(245, 247, 250));
		jContentPane.setLayout(new GridLayout(5, 1, 0, 10));
		jContentPane.add(jLabelSelectOption);
		jContentPane.add(jButtonCreateQuery);
		jContentPane.add(jButtonQueryQueries);
		jContentPane.add(jButtonViewMyOffers); 
		jContentPane.add(jButtonLogout);
		
		
		setContentPane(jContentPane);
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle") +": "+sellerMail);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
	}
	
	private void paintAgain() {
		jLabelSelectOption.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jButtonQueryQueries.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.ViewAvailable"));
		jButtonCreateQuery.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateSale"));
		jButtonLogout.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Logout"));
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle")+ ": "+sellerMail);
	}
	
} // @jve:decl-index=0:visual-constraint="0,0"

