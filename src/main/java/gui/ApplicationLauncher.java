package gui;

import java.net.URL;
import java.util.Locale;

import javax.swing.UIManager;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import businessLogic.BLFacade;
import businessLogic.BLFacadeImplementation;
import configuration.ConfigXML;
import dataAccess.DataAccess;

/**
 * Application entry point.
 * Configures locale, initializes business logic facade (local or remote), and launches login GUI.
 */
public class ApplicationLauncher { 
	
	
	
	public static void main(String[] args) {

		ConfigXML c=ConfigXML.getInstance();		
		Locale.setDefault(new Locale(c.getLocale()));
		
		try {
			
			BLFacade appFacadeInterface;
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			
			if (c.isBusinessLogicLocal()) {
				DataAccess da= new DataAccess();
				appFacadeInterface=new BLFacadeImplementation(da);
			}
			else { //If remote
				
				 String serviceName= "http://"+c.getBusinessLogicNode() +":"+ c.getBusinessLogicPort()+"/ws/"+c.getBusinessLogicName()+"?wsdl";	 
				 URL url = new URL(serviceName);

		 
		        //1st argument refers to wsdl document above
				//2nd argument is service name, refer to wsdl document above
		        QName qname = new QName("http://businessLogic/", "BLFacadeImplementationService");
		 
		        Service service = Service.create(url, qname);

		        appFacadeInterface = service.getPort(BLFacade.class);
			} 
			
			MainGUI.setBussinessLogic(appFacadeInterface);
			
			// Abrir LoginGUI como pantalla inicial
			LoginGUI loginGUI = new LoginGUI();
			loginGUI.setVisible(true);
			
		}catch (Exception e) {
			System.out.println("Error in ApplicationLauncher: "+e.toString());
			e.printStackTrace();
		}


	}

}
