package businessLogic;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.jws.WebMethod;
import javax.jws.WebService;

import dataAccess.DataAccess;
import domain.AcceptedOffer;
import domain.Buyer;
import domain.Sale;
import domain.Seller;
import domain.User;
import exceptions.FileNotUploadedException;
import exceptions.InvalidEmailException;
import exceptions.InvalidFieldException;
import exceptions.InvalidPriceException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;
import exceptions.UserAlreadyExistsException;


/**
 * Implementation of the Business Logic Facade.
 * Implements all business operations defined in BLFacade interface.
 * Acts as an intermediary between the presentation layer and data access layer.
 */
@WebService(endpointInterface = "businessLogic.BLFacade")
public class BLFacadeImplementation  implements BLFacade {
	 private static final int baseSize = 160;

		private static final String basePath="src/main/resources/images/";
	DataAccess dbManager;

	public BLFacadeImplementation()  {		
		System.out.println("Creating BLFacadeImplementation instance");
		dbManager=new DataAccess();		
	}
	
    public BLFacadeImplementation(DataAccess da)  {
		System.out.println("Creating BLFacadeImplementation instance with DataAccess parameter");
		dbManager=da;		
	}
    

	/**
	 * {@inheritDoc}
	 */
   @WebMethod
	public Sale createSale(String title, String description,int status, float price, Date pubDate, String sellerEmail, File file) throws  FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException, InvalidFieldException, InvalidPriceException {
		dbManager.open();
		Sale product=dbManager.createSale(title, description, status, price, pubDate, sellerEmail, file);		
		dbManager.close();
		return product;
   };
	
   /**
    * {@inheritDoc}
    */
	@WebMethod 
	public List<Sale> getSales(String desc){
		dbManager.open();
		List<Sale>  rides=dbManager.getSales(desc);
		dbManager.close();
		return rides;
	}
	
	/**
	* {@inheritDoc}
	*/
	@WebMethod 
	public List<Sale> getPublishedSales(String desc, Date pubDate) {
		dbManager.open();
		List<Sale>  rides=dbManager.getPublishedSales(desc,pubDate);
		dbManager.close();
		return rides;
	}
	
	/**
	* {@inheritDoc}
	*/
	@WebMethod public BufferedImage getFile(String fileName) {
		return dbManager.getFile(fileName);
	}

	/**
	 * Closes the database connection.
	 */
    public void close() {
		DataAccess dB4oManager=new DataAccess();
		dB4oManager.close();

	}

	/**
	 * {@inheritDoc}
	 */
    @WebMethod	
	 public void initializeBD(){
    	dbManager.open();
		dbManager.initializeDB();
		dbManager.close();
	}
    
	/**
	 * {@inheritDoc}
	 */
    @WebMethod public Image downloadImage(String imageName) {
        File image = new File(basePath+imageName);
        try {
            return ImageIO.read(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

	/**
	 * {@inheritDoc}
	 */
	@WebMethod
	public User login(String email, String password) {
		dbManager.open();
		User user = dbManager.login(email, password);
		dbManager.close();
		return user;
	}

	/**
	 * {@inheritDoc}
	 */
	@WebMethod
	public Buyer registerBuyer(String email, String name, 
							String password, String shippingAddress)
			throws UserAlreadyExistsException, InvalidEmailException, 
			       InvalidFieldException {
		dbManager.open();
		try {
			Buyer buyer = dbManager.createBuyer(email, name, password, shippingAddress);
			return buyer;
		} finally {
			dbManager.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@WebMethod
	public Seller registerSeller(String email, String name, String password, String bankAccount)
			throws UserAlreadyExistsException, InvalidEmailException, 
			       InvalidFieldException {
		dbManager.open();
		try {
			Seller seller = dbManager.createSeller(email, name, password, bankAccount);
			return seller;
		} finally {
			dbManager.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@WebMethod
	public AcceptedOffer acceptOffer(String buyerEmail, Integer saleNumber, Float negotiatedPrice) throws InvalidPriceException {
		dbManager.open();
		try {
			AcceptedOffer accepted = dbManager.acceptOffer(buyerEmail, saleNumber,negotiatedPrice);
			return accepted;
		} finally {
			dbManager.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@WebMethod
	public List<AcceptedOffer> getAcceptedOffersBySeller(String sellerEmail) {
		dbManager.open();
		List<AcceptedOffer> offers = dbManager.getAcceptedOffersBySeller(sellerEmail);
		dbManager.close();
		return offers;
	}

	/**
	 * {@inheritDoc}
	 */
	@WebMethod
	public List<AcceptedOffer> getAcceptedOffersByBuyer(String buyerEmail) {
		dbManager.open();
		List<AcceptedOffer> offers = dbManager.getAcceptedOffersByBuyer(buyerEmail);
		dbManager.close();
		return offers;
	}

    /**
	 * {@inheritDoc}
	 */
    @WebMethod
    public List<Sale> getAvailableSalesForBuyer(Date pubDate) {
        dbManager.open();
        List<Sale> available = dbManager.getAvailableSalesForBuyer(pubDate);
        dbManager.close();
        return available;
    }

    /**
	 * {@inheritDoc}
	 */
    @WebMethod
    public List<Sale> getSalesBySellerEmail(String sellerEmail) {
        dbManager.open();
        List<Sale> sales = dbManager.getSalesBySellerEmail(sellerEmail);
        dbManager.close();
        return sales;
    }
    
}

