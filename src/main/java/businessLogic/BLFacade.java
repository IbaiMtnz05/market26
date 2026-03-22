package businessLogic;

import java.awt.Image;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

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
 * Business Logic Facade interface that defines all business operations
 * available in the marketplace system.
 * This interface is exposed as a web service.
 */
@WebService
public interface BLFacade  {
	  

	/**
	 * This method creates/adds a product to a seller
	 * 
	 * @param title of the product
	 * @param description of the product
	 * @param status 
	 * @param selling price
	 * @param category of a product
	 * @param publicationDate
	 * @return Sale
	 */
   @WebMethod
	public Sale createSale(String title, String description, int status, float price, Date pubDate, String sellerEmail, File file) throws  FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException, InvalidFieldException, InvalidPriceException;
	
	
	/**
	 * This method retrieves the products that contain desc
	 * 
	 * @param desc the text to search
	 * @return collection of sales that contain desc 
	 */
	@WebMethod public List<Sale> getSales(String desc);
	
	/**
	 * 	 * This method retrieves the products that contain a desc text in a title and the publicationDate today or before
	 * 
	 * @param desc the text to search
	 * @param pubDate the date  of the publication date
	 * @return collection of sales that contain desc and published before pubDate
	 */
	@WebMethod public List<Sale> getPublishedSales(String desc, Date pubDate);

	
	/**
	 * This method calls the data access to initialize the database with some sellers and products.
	 * It is only invoked  when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	@WebMethod public void initializeBD();
	
	/**
	 * Downloads a product image from the server file system.
	 * 
	 * @param imageName Name of the image file to download
	 * @return Image object if found, null otherwise
	 */	
	@WebMethod public Image downloadImage(String imageName);
	
	/**
	 * Login de usuario
	 * @param email del usuario
	 * @param password contraseña
	 * @return User si las credenciales son correctas, null otherwise
	 */
	@WebMethod
	public User login(String email, String password);

	/**
	 * Registrar nuevo comprador
	 * @return Buyer creado y persistido
	 * @throws UserAlreadyExistsException si el email ya existe
	 * @throws InvalidEmailException si el email es inválido
	 * @throws InvalidFieldException si algún campo es inválido
	 */
	@WebMethod
	public Buyer registerBuyer(String email, String name, String password, String shippingAddress)
		throws UserAlreadyExistsException, InvalidEmailException, InvalidFieldException;

	/**
	 * Registrar nuevo vendedor
	 * @return Seller creado y persistido
	 * @throws UserAlreadyExistsException si el email ya existe
	 * @throws InvalidEmailException si el email es inválido
	 * @throws InvalidFieldException si algún campo es inválido
	 */
	@WebMethod
	public Seller registerSeller(String email, String name, String password, String bankAccount)
		throws UserAlreadyExistsException, InvalidEmailException, InvalidFieldException;

	/**
	 * Obtener ofertas aceptadas de un vendedor
	 * @param sellerEmail email del vendedor
	 * @return Lista de ofertas aceptadas con información del comprador
	 */
	@WebMethod
	public List<AcceptedOffer> getAcceptedOffersBySeller(String sellerEmail);

	/**
	 * Obtener ofertas aceptadas de un comprador
	 * @param buyerEmail email del comprador
	 * @return Lista de ofertas aceptadas por el comprador
	 */
	@WebMethod
	public List<AcceptedOffer> getAcceptedOffersByBuyer(String buyerEmail);
	
    /**
     * Aceptar una oferta con posible negociación de precio
     * @param buyerEmail email del comprador
     * @param saleNumber número de la oferta
     * @param negotiatedPrice el precio ofrecido (null si acepta el original)
     * @return AcceptedOffer si se aceptó, null si ya fue aceptada
     * @throws InvalidPriceException si el precio es inválido
     */
    @WebMethod
    public AcceptedOffer acceptOffer(String buyerEmail, Integer saleNumber, 
                                     Float negotiatedPrice) throws InvalidPriceException;

    /**
     * Obtiene ofertas disponibles 
     * @param pubDate fecha actual
     * @return Lista de ofertas 
     */
    @WebMethod
    public List<Sale> getAvailableSalesForBuyer(Date pubDate);

    /**
     * Obtiene ofertas creadas por el vendedor
     * @param sellerEmail del vendedor
     * @return Lista de sus ofertas
     */
    @WebMethod
    public List<Sale> getSalesBySellerEmail(String sellerEmail);
}
