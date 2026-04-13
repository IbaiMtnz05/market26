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
	private JButton jButtonViewCommissions = null;
	private JButton jButtonLogout = null;
	private JButton jButtonViewMyOffers = null;
	private JButton jButtonManageRefunds = null;
	private JButton jButtonManageCategories = null;
	private final ResourceBundle labels = ResourceBundle.getBundle("Etiquetas");

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
		
		this.setSize(560, 460);
		jLabelSelectOption = new JLabel(labels.getString("MainGUI.SelectOption"));
		jLabelSelectOption.setFont(new Font("Dialog", Font.BOLD, 16));
		jLabelSelectOption.setForeground(new Color(232, 236, 242));
		jLabelSelectOption.setHorizontalAlignment(SwingConstants.CENTER);
		
		jButtonCreateQuery = new JButton();
		jButtonCreateQuery.setText(labels.getString("MainGUI.CreateSale"));
		jButtonCreateQuery.setFont(new Font("Dialog", Font.BOLD, 14));
		jButtonCreateQuery.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new CreateSaleGUI(sellerMail);
				a.setVisible(true);
			}
		});
		
		jButtonQueryQueries = new JButton();
		jButtonQueryQueries.setText(labels.getString("MainGUI.ViewAvailable"));
		jButtonQueryQueries.setFont(new Font("Dialog", Font.BOLD, 14));
		jButtonQueryQueries.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new QuerySalesGUI();
				a.setVisible(true);
			}
		});

		jButtonViewMyOffers = new JButton();
		jButtonViewMyOffers.setText(labels.getString("MainGUI.ViewMyOffers"));
		jButtonViewMyOffers.setFont(new Font("Dialog", Font.BOLD, 14));
		jButtonViewMyOffers.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame sellerViewGUI = new SellerViewMyOffersGUI(sellerMail);
				sellerViewGUI.setVisible(true);
			}
		});

		jButtonViewCommissions = new JButton();
		jButtonViewCommissions.setText(labels.getString("MainGUI.ViewCommissions"));
		jButtonViewCommissions.setFont(new Font("Dialog", Font.BOLD, 14));
		jButtonViewCommissions.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame commissionsGUI = new SellerCommissionsGUI(sellerMail);
				commissionsGUI.setVisible(true);
			}
		});

		jButtonManageRefunds = new JButton();
		jButtonManageRefunds.setText(labels.getString("MainGUI.ManageRefunds"));
		jButtonManageRefunds.setFont(new Font("Dialog", Font.BOLD, 14));
		jButtonManageRefunds.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame refundsGUI = new GestionarReembolsoGUI(sellerMail);
				refundsGUI.setVisible(true);
			}
		});

		jButtonManageCategories = new JButton();
		jButtonManageCategories.setText("Categorias dinamicas");
		jButtonManageCategories.setFont(new Font("Dialog", Font.BOLD, 14));
		jButtonManageCategories.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame categoryGUI = new CategoryManagerGUI(sellerMail);
				categoryGUI.setVisible(true);
			}
		});
		
		jButtonLogout = new JButton();
		jButtonLogout.setText(labels.getString("MainGUI.Logout"));
		jButtonLogout.setFont(new Font("Dialog", Font.BOLD, 14));
		jButtonLogout.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame loginGUI = new LoginGUI();
				loginGUI.setVisible(true);
				dispose();
			}
		});

		jContentPane = new JPanel();
		jContentPane.setBackground(new Color(21, 24, 30));
		jContentPane.setLayout(new GridLayout(8, 1, 0, 10));
		jContentPane.add(jLabelSelectOption);
		jContentPane.add(jButtonCreateQuery);
		jContentPane.add(jButtonQueryQueries);
		jContentPane.add(jButtonViewMyOffers); 
		jContentPane.add(jButtonViewCommissions);
		jContentPane.add(jButtonManageRefunds);
		jContentPane.add(jButtonManageCategories);
		jContentPane.add(jButtonLogout);
		
		
		setContentPane(jContentPane);
		setTitle(labels.getString("MainGUI.MainTitle") +": "+sellerMail);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
	}
	
	private void paintAgain() {
		ResourceBundle l = ResourceBundle.getBundle("Etiquetas");
		jLabelSelectOption.setText(l.getString("MainGUI.SelectOption"));
		jButtonQueryQueries.setText(l.getString("MainGUI.ViewAvailable"));
		jButtonCreateQuery.setText(l.getString("MainGUI.CreateSale"));
		jButtonViewCommissions.setText(l.getString("MainGUI.ViewCommissions"));
		jButtonManageRefunds.setText(l.getString("MainGUI.ManageRefunds"));
		jButtonLogout.setText(l.getString("MainGUI.Logout"));
		this.setTitle(l.getString("MainGUI.MainTitle")+ ": "+sellerMail);
	}
	
} // @jve:decl-index=0:visual-constraint="0,0"

