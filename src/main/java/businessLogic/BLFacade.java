package businessLogic;

import java.awt.Image;
import java.io.File;
import java.util.Date;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import domain.AcceptedOffer;
import domain.Buyer;
import domain.ComisionMarketplace;
import domain.DecisionVenta;
import domain.Reembolso;
import domain.Sale;
import domain.Seller;
import domain.TransaccionPago;
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

	/**
	 * Registra la decision del vendedor sobre que comprador sera seleccionado.
	 * @param saleNumber numero de venta
	 * @param acceptedOfferId id de la propuesta aceptada
	 * @param criterio criterio de seleccion (mejor precio, mejor valoracion, etc.)
	 * @param motivo justificacion de la decision
	 * @return DecisionVenta registrada
	 */
    @WebMethod
    public DecisionVenta decidirComprador(Integer saleNumber, Integer acceptedOfferId,
                                          String criterio, String motivo);
    
	/**
	 * Procesa el cobro transaccional del precio del producto al comprador.
	 * @param saleNumber numero de venta
	 * @param buyerEmail email del comprador
	 * @param importe importe a cobrar
	 * @return TransaccionPago registrada
	 */
    @WebMethod
	public TransaccionPago procesarCobro(Integer saleNumber, String buyerEmail, float importe);

	@WebMethod
	public List<TransaccionPago> getTransaccionesBySale(Integer saleNumber);

	/**
	 * Calcula la comision del marketplace sobre una transaccion de pago confirmada.
	 * @param transaccionPagoId id de la transaccion
	 * @param porcentaje porcentaje de comision a aplicar
	 * @return ComisionMarketplace calculada
	 */
    @WebMethod
    public ComisionMarketplace calcularComision(Integer transaccionPagoId, float porcentaje);

	/**
	 * Liquida una comisión ya calculada del marketplace.
	 * @param comisionId id de la comisión
	 * @return ComisionMarketplace actualizada
	 */
    @WebMethod
    public ComisionMarketplace liquidarComision(Integer comisionId);

	/**
	 * Solicita un reembolso total o parcial al comprador por incidencia post-venta.
	 * @param transaccionPagoId id de la transaccion original
	 * @param importe importe a devolver
	 * @param motivo motivo del reembolso (producto defectuoso, cancelacion acordada, etc.)
	 * @param buyerEmail email del comprador
	 * @return Reembolso registrado
	 */
    @WebMethod
    public Reembolso solicitarReembolso(Integer transaccionPagoId, float importe,
										String motivo, String buyerEmail);
    /**
	 * Obtiene las decisiones de venta tomadas por un vendedor, incluyendo el criterio de decisión y el motivo.
	 * @param sellerEmail email del vendedor
	 * @return Lista de DecisionVenta con las decisiones tomadas por el vendedor
	 */
    @WebMethod
    public List<DecisionVenta> getDecisionVentasBySeller(String sellerEmail);

    /**
	 * Obtiene las comisiones del marketplace de un vendedor.
	 * @param sellerEmail email del vendedor
	 * @return Lista de comisiones asociadas al vendedor
	 */
    @WebMethod
    public List<ComisionMarketplace> getComisionesBySeller(String sellerEmail);

    /**
	 * Obtiene los reembolsos solicitados por un comprador, incluyendo el tipo de
	 * reembolso y el motivo.
	 * @param buyerEmail email del comprador
	 * @return Lista de Reembolso con los reembolsos solicitados por el comprador
	 */
    @WebMethod
    public List<Reembolso> getReembolsosByBuyer(String buyerEmail);
}
