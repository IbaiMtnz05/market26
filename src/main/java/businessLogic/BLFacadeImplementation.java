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
 * Implementation of the Business Logic Facade.
 * Implements all business operations defined in BLFacade interface.
 * Acts as an intermediary between the presentation layer and data access layer.
 */
@WebService(endpointInterface = "businessLogic.BLFacade")
public class BLFacadeImplementation  implements BLFacade {
	 private static final int baseSize = 160;

		private static final String basePath="src/main/resources/images/";
	DataAccess dbManager;

	private interface DbAction<T> {
		T run();
	}

	private <T> T execute(DbAction<T> action) {
		dbManager.open();
		try {
			return action.run();
		} finally {
			dbManager.close();
		}
	}

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
		return execute(() -> dbManager.getSales(desc));
	}
	
	/**
	* {@inheritDoc}
	*/
	@WebMethod 
	public List<Sale> getPublishedSales(String desc, Date pubDate) {
		return execute(() -> dbManager.getPublishedSales(desc, pubDate));
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
		dbManager.close();
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
		return execute(() -> dbManager.login(email, password));
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
    
    /**
	 * {@inheritDoc}
	 */
    @WebMethod
    public DecisionVenta decidirComprador(Integer saleNumber, Integer acceptedOfferId,
										  String criterio, String motivo) {
		return execute(() -> dbManager.decidirComprador(saleNumber, acceptedOfferId,
													 criterio, motivo));
    }
   
    /**
	 * {@inheritDoc}
	 */
    @WebMethod
	public TransaccionPago procesarCobro(Integer saleNumber, String buyerEmail, float importe) {
		return execute(() -> dbManager.procesarCobro(saleNumber, buyerEmail, importe));
	}

	@WebMethod
	public List<TransaccionPago> getTransaccionesBySale(Integer saleNumber) {
		return execute(() -> dbManager.getTransaccionesBySale(saleNumber));
    }
    
    /**
     * {@inheritDoc}
     */
    @WebMethod
    public ComisionMarketplace calcularComision(Integer transaccionPagoId, float porcentaje) {
		return execute(() -> dbManager.calcularComision(transaccionPagoId, porcentaje));
    }

	/**
	 * {@inheritDoc}
	 */
    @WebMethod
    public ComisionMarketplace liquidarComision(Integer comisionId) {
		return execute(() -> dbManager.liquidarComision(comisionId));
    }
    
    /**
	 * {@inheritDoc}
	 */
    @WebMethod
    public Reembolso solicitarReembolso(Integer transaccionPagoId, float importe,
            String motivo, String buyerEmail,
            String vendedorEmail, String observaciones) {
    	dbManager.open();
    	try {
    		return dbManager.solicitarReembolso(transaccionPagoId, importe, motivo, 
                    buyerEmail, vendedorEmail, observaciones);
    	} finally {
    		dbManager.close();
    	}
    }
    
    /**
	 * {@inheritDoc}
	 */
    @WebMethod
    public List<DecisionVenta> getDecisionVentasBySeller(String sellerEmail) {
		return execute(() -> dbManager.getDecisionVentasBySeller(sellerEmail));
    }
    
    /**
     * {@inheritDoc}
     */
    @WebMethod
    public List<ComisionMarketplace> getComisionesBySeller(String sellerEmail) {
		return execute(() -> dbManager.getComisionesBySeller(sellerEmail));
    }
    
    /**
     * {@inheritDoc}
     */
    @WebMethod
    public List<Reembolso> getReembolsosByBuyer(String buyerEmail) {
		return execute(() -> dbManager.getReembolsosByBuyer(buyerEmail));
    }
    
    @WebMethod
    public Reembolso gestionarReembolsoPorVendedor(Integer saleNumber, String vendedorEmail,
            float importeReembolso, String motivo,
            String observaciones) 
            throws IllegalStateException {
    dbManager.open();
    try {
        // Validar que la venta existe y pertenece al vendedor
        Sale sale = dbManager.findSale(saleNumber);
        if (sale == null) {
            throw new IllegalStateException("La venta no existe");
        }
        if (!sale.getSeller().getEmail().equals(vendedorEmail)) {
            throw new SecurityException("Solo el vendedor puede gestionar reembolsos de esta venta");
        }

        // Validar que la venta tiene una decisión tomada
        DecisionVenta decision = dbManager.getDecisionBySale(saleNumber);
        if (decision == null) {
            throw new IllegalStateException("La venta no tiene un comprador seleccionado aún");
        }

        // Validar que existe transacción de pago
        TransaccionPago transaccion = dbManager.getTransaccionConfirmadaBySale(saleNumber);
        if (transaccion == null) {
            throw new IllegalStateException("No hay transacción de pago confirmada para esta venta");
        }

        // Validar que no haya reembolso previo para esta transacción
        if (dbManager.existeReembolsoPorTransaccion(transaccion.getId())) {
            throw new IllegalStateException("Ya se ha solicitado un reembolso para esta transacción");
        }

        // Validar importe
        if (importeReembolso <= 0) {
            throw new IllegalArgumentException("El importe del reembolso debe ser positivo");
        }
        if (importeReembolso > transaccion.getImporte()) {
            throw new IllegalArgumentException("El importe del reembolso no puede superar el importe pagado");
        }

        // Registrar el reembolso - AHORA CON 7 ARGUMENTOS
        String buyerEmail = decision.getAcceptedOffer().getBuyer().getEmail();
        return dbManager.solicitarReembolso(transaccion.getId(), importeReembolso, motivo, 
                                            buyerEmail, vendedorEmail, observaciones);

    } finally {
        dbManager.close();
    }
}

	
}

