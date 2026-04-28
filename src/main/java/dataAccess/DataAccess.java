package dataAccess;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.*;
import exceptions.FileNotUploadedException;
import exceptions.InvalidEmailException;
import exceptions.InvalidFieldException;
import exceptions.InvalidPriceException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;
import exceptions.UserAlreadyExistsException;

/**
 * Data Access Object for the ObjectDB database.
 * Provides methods for CRUD operations on all domain entities
 * including sales, buyers, sellers, and accepted offers.
 */
public class DataAccess  {
	private  EntityManager  db;
	private  EntityManagerFactory emf;
    private static final int baseSize = 160;

	private static final String basePath="src/main/resources/images/";
	private static final String dbServerDir = "src/main/resources/db/";


	ConfigXML c=ConfigXML.getInstance();

	private String msg(String key) {
		return ResourceBundle.getBundle("Etiquetas").getString(key);
	}

    public DataAccess()  {
		if (c.isDatabaseInitialized()) {
			String fileName=c.getDbFilename();

			if (!c.isDatabaseLocal()) fileName=dbServerDir+fileName;
			
			File fileToDelete= new File(fileName);
			if(fileToDelete.delete()){
				File fileToDeleteTemp= new File(fileName+"$");
				fileToDeleteTemp.delete();
				System.out.println("File deleted");
			 } else {
				 System.out.println("Operation failed");
				}
		}
		open();
		if  (c.isDatabaseInitialized()) 
			initializeDB();
		System.out.println("DataAccess created => isDatabaseLocal: "+c.isDatabaseLocal()+" isDatabaseInitialized: "+c.isDatabaseInitialized());

		close();

	}
     
    public DataAccess(EntityManager db) {
    	this.db=db;
    }

	
	
	/**
	 * This method  initializes the database with some products and sellers.
	 * This method is invoked by the business logic (constructor of BLFacadeImplementation) when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	public void initializeDB() {
		db.getTransaction().begin();
		
		try {
			Seller seller1 = new Seller("seller1@gmail.com", "Aitor Fernandez", "ES91 1234 5678 9012 3456 7890");
			seller1.setPassword("pass1");
			
			Seller seller2 = new Seller("seller22@gmail.com", "Ane Gaztañaga", "ES92 9876 5432 1098 7654 3210");
			seller2.setPassword("pass2");
			
			Seller seller3 = new Seller("seller3@gmail.com", "Test Seller", "ES93 5555 5555 5555 5555 5555");
			seller3.setPassword("pass3");
			
			Date today = UtilDate.trim(new Date());
			
			Sale sale1 = seller1.addSale("futbol baloia", 
				"oso polita, gutxi erabilita", 2, 10, today, null);
			Sale sale2 = seller1.addSale("salomon mendiko botak", 
				"44 zenbakia, 3 ateraldi", 2, 20, today, null);
			Sale sale3 = seller2.addSale("iphone 17", 
				"oso gutxi erabilita", 2, 400, today, null);
			Sale sale4 = seller2.addSale("orbea mendiko bizikleta", 
				"29\" 10 urte", 3, 225, today, null);
			
			db.persist(seller1);
			db.persist(seller2);
			db.persist(seller3);
			
			db.flush();
			
			Buyer buyer1 = new Buyer("buyer1@gmail.com", "Maria Lopez", "buyerpass1", "Calle Mayor 10, Bilbao");
			Buyer buyer2 = new Buyer("buyer2@gmail.com", "Jon Etxebarria", "buyerpass2", "Avenida Libertad 23, Donostia");
			
			AcceptedOffer acc1 = new AcceptedOffer(buyer1, sale1, 2.4f);
			buyer1.addAcceptedOffer(acc1);
			
			AcceptedOffer acc2 = new AcceptedOffer(buyer2, sale3, null);
			buyer2.addAcceptedOffer(acc2);
			
			AcceptedOffer acc3 = new AcceptedOffer(buyer1, sale4, 6.4f);
			buyer1.addAcceptedOffer(acc3);
			
			// Persistir compradores (con sus aceptaciones)
			db.persist(buyer1);
			db.persist(buyer2);
			
			db.flush();
			
			db.getTransaction().commit();
			System.out.println("DB inicializada con vendedores, compradores y aceptaciones");
		} catch (Exception e) {
			e.printStackTrace();
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
		}
	}
	
	
	/**
	 * This method creates/adds a product to a seller
	 * 
	 * @param title of the product
	 * @param description of the product
	 * @param status 
	 * @param selling price
	 * @param category of a product
	 * @param publicationDate
	 * @return Product
 	 * @throws SaleAlreadyExistException if the same product already exists for the seller
	 */
	public Sale createSale(String title, String description, int status, float price,  Date pubDate, String sellerEmail, File file) throws  FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException, InvalidFieldException, InvalidPriceException {
		if (title == null || title.trim().isEmpty()) {
			throw new InvalidFieldException(msg("DataAccess.ErrorTitleRequired"));
		}
		if (description == null || description.trim().isEmpty()) {
			throw new InvalidFieldException(msg("DataAccess.ErrorDescriptionRequired"));
		}
		if (price <= 0) {
			throw new InvalidPriceException(msg("DataAccess.ErrorPriceMustBePositive"));
		}
		if (pubDate.before(UtilDate.trim(new Date()))) {
			throw new MustBeLaterThanTodayException(msg("DataAccess.ErrorSaleMustBeLaterThanToday"));
		}

		saveImageIfPresent(file);
		db.getTransaction().begin();
		try {
			Seller seller = db.find(Seller.class, sellerEmail);
			if (seller == null) {
				db.getTransaction().rollback();
				return null;
			}

			if (seller.doesSaleExist(title)) {
				db.getTransaction().rollback();
				throw new SaleAlreadyExistException(msg("DataAccess.SaleAlreadyExist"));
			}

			Sale sale = seller.addSale(title, description, status, price, pubDate, file);
			sale.setCategory(inferCategory(title, description));
			db.persist(sale);
			db.getTransaction().commit();
			return sale;
		} catch (RuntimeException e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			throw e;
		}
	}
	
	/**
	 * This method retrieves all the products that contain a desc text in a title
	 * 
	 * @param desc the text to search
	 * @return collection of products that contain desc in a title
	 */
	public List<Sale> getSales(String desc) {
		System.out.println(">> DataAccess: getProducts=> from= "+desc);

		List<Sale> res = new ArrayList<Sale>();	
		TypedQuery<Sale> query = db.createQuery("SELECT s FROM Sale s WHERE s.title LIKE ?1",Sale.class);   
		query.setParameter(1, "%"+desc+"%");
		
		List<Sale> sales = query.getResultList();
	 	 for (Sale sale:sales){
		   res.add(sale);
		  }
	 	return res;
	}
	
	/**
	 * This method retrieves the products that contain a desc text in a title and the publicationDate today or before
	 * 
	 * @param desc the text to search
	 * @return collection of products that contain desc in a title
	 */
	public List<Sale> getPublishedSales(String desc, Date pubDate) {
		System.out.println(">> DataAccess: getProducts=> from= "+desc);

		List<Sale> res = new ArrayList<Sale>();	
		TypedQuery<Sale> query = db.createQuery("SELECT s FROM Sale s WHERE s.title LIKE ?1 AND s.pubDate <=?2",Sale.class);   
		query.setParameter(1, "%"+desc+"%");
		query.setParameter(2,pubDate);
		
		List<Sale> sales = query.getResultList();
	 	 for (Sale sale:sales){
		   res.add(sale);
		  }
	 	return res;
	}

	/**
	 * Opens the database connection.
	 */
	public void open(){
		
		String fileName=c.getDbFilename();
		if (c.isDatabaseLocal()) {
			emf = Persistence.createEntityManagerFactory("objectdb:"+fileName);
			db = emf.createEntityManager();
		} else {
			Map<String, String> properties = new HashMap<String, String>();
			  properties.put("javax.persistence.jdbc.user", c.getUser());
			  properties.put("javax.persistence.jdbc.password", c.getPassword());

			  emf = Persistence.createEntityManagerFactory("objectdb://"+c.getDatabaseNode()+":"+c.getDatabasePort()+"/"+fileName, properties);
			  db = emf.createEntityManager();
    	   }
		System.out.println("DataAccess opened => isDatabaseLocal: "+c.isDatabaseLocal());

		
	}

	/**
	 * Retrieves an image file from the specified path and resizes it to a standard size.
	 * @param fileName the name of the image file to retrieve
	 * @return a BufferedImage object containing the resized image, or null if the file cannot be read
	 * @throws IOException if an error occurs while reading the image file
	 */
	public BufferedImage getFile(String fileName) {
		File file=new File(basePath+fileName);
		BufferedImage targetImg=null;
		try {
             targetImg = rescale(ImageIO.read(file));
        } catch (IOException ex) {
            //Logger.getLogger(MainAppFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
		return targetImg;

	}
	
	/**
	 * Resizes the given image to a standard size defined by baseSize.
	 * @param originalImage the original BufferedImage to be resized
	 * @return a new BufferedImage object containing the resized image
	 */
	public BufferedImage rescale(BufferedImage originalImage)
    {
		System.out.println("rescale "+originalImage);
        BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
        g.dispose();
        return resizedImage;
    }
	
	/**
	 * Persiste una imagen en disco si el archivo está presente.
	 * @param file archivo de imagen a guardar
	 */
	private void saveImageIfPresent(File file) {
		if (file == null) {
			return;
		}
		try {
			BufferedImage img = ImageIO.read(file);
			if (img == null) {
				return;
			}
			File outputDir = new File(basePath);
			if (!outputDir.exists() && !outputDir.mkdirs()) {
				throw new IOException("Cannot create image directory: " + outputDir.getAbsolutePath());
			}

			String format = "png";
			String name = file.getName();
			int dotIndex = name.lastIndexOf('.');
			if (dotIndex > 0 && dotIndex < name.length() - 1) {
				String ext = name.substring(dotIndex + 1).toLowerCase();
				if ("png".equals(ext) || "jpg".equals(ext) || "jpeg".equals(ext) || "gif".equals(ext) || "bmp".equals(ext)) {
					format = ext;
				}
			}

			File output = new File(outputDir, name);
			ImageIO.write(img, format, output);
		} catch (IOException e) {
			throw new RuntimeException("Error saving image file: " + file.getName(), e);
		}
	}
	
	public void close(){
		try {
			if (db != null && db.isOpen()) {
				db.close();
			}
		} finally {
			if (emf != null && emf.isOpen()) {
				emf.close();
			}
		}
		System.out.println("DataAccess closed");
	}

	/**
	 * Valida credenciales de usuario
	 * @return User si existe y la contraseña coincide, null otherwise
	 */
	public User login(String email, String password) {
		try {
			// Buscar usuario por email (clave primaria)
			User user = db.find(User.class, email);
			
			// Verificar si existe y contraseña correcta
			if (user != null && user.getPassword().equals(password)) {
				return user;
			}
			return null; // Añadir excepcion para usuario no encontrado o contraseña incorrecta 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Crea y persiste un nuevo comprador con validaciones completas
	 * @throws UserAlreadyExistsException si el email ya está registrado
	 * @throws InvalidEmailException si el formato del email es inválido
	 * @throws InvalidFieldException si algún campo es inválido
	 */
	public Buyer createBuyer(String email, String name, String password, String shippingAddress) throws UserAlreadyExistsException, InvalidEmailException, InvalidFieldException {
		try {
			// Validar campos no vacíos
			if (email == null || email.trim().isEmpty()) {
				throw new InvalidFieldException(msg("DataAccess.ErrorEmailRequired"));
			}
			if (name == null || name.trim().isEmpty()) {
				throw new InvalidFieldException(msg("DataAccess.ErrorNameRequired"));
			}
			if (password == null || password.trim().isEmpty()) {
				throw new InvalidFieldException(msg("DataAccess.ErrorPasswordRequired"));
			}
			if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
				throw new InvalidFieldException(msg("DataAccess.ErrorShippingAddressRequired"));
			}
			
			// Validar formato de email
			if (!isValidEmail(email)) {
				throw new InvalidEmailException(msg("DataAccess.ErrorInvalidEmail"));
			}
			
			db.getTransaction().begin();

			// Validar email no duplicado
			User existing = db.find(User.class, email);
			if (existing != null) {
				db.getTransaction().rollback();
				throw new UserAlreadyExistsException(msg("DataAccess.ErrorEmailAlreadyRegistered"));
			}
			
			// Crear objeto Buyer
			Buyer buyer = new Buyer(email, name, password, shippingAddress);
			
			// Persistir en la BD
			db.persist(buyer);
			
			db.getTransaction().commit();
			return buyer;
		} catch (UserAlreadyExistsException | InvalidEmailException | 
		         InvalidFieldException e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			throw e;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			e.printStackTrace();
			throw new RuntimeException(msg("DataAccess.ErrorRegisterBuyer") + ": " + e.getMessage(), e);
		}
	}

	/**
	 * Crea y persiste un nuevo vendedor con validaciones completas
	 * @throws UserAlreadyExistsException si el email ya está registrado
	 * @throws InvalidEmailException si el formato del email es inválido
	 * @throws InvalidFieldException si algún campo es inválido
	 */
	public Seller createSeller(String email, String name, String password, String bankAccount) throws UserAlreadyExistsException, InvalidEmailException, InvalidFieldException {
		try {
			// Validar campos no vacíos
			if (email == null || email.trim().isEmpty()) {
				throw new InvalidFieldException(msg("DataAccess.ErrorEmailRequired"));
			}
			if (name == null || name.trim().isEmpty()) {
				throw new InvalidFieldException(msg("DataAccess.ErrorNameRequired"));
			}
			if (password == null || password.trim().isEmpty()) {
				throw new InvalidFieldException(msg("DataAccess.ErrorPasswordRequired"));
			}
			if (bankAccount == null || bankAccount.trim().isEmpty()) {
				throw new InvalidFieldException(msg("DataAccess.ErrorBankAccountRequired"));
			}
			
			// Validar formato de email
			if (!isValidEmail(email)) {
				throw new InvalidEmailException(msg("DataAccess.ErrorInvalidEmail"));
			}
			
			db.getTransaction().begin();

			// Validar email no duplicado
			User existing = db.find(User.class, email);
			if (existing != null) {
				db.getTransaction().rollback();
				throw new UserAlreadyExistsException(msg("DataAccess.ErrorEmailAlreadyRegistered"));
			}
			
			Seller seller = new Seller(email, name, bankAccount);
			seller.setPassword(password);
			
			db.persist(seller);
			
			db.getTransaction().commit();
			return seller;
		} catch (UserAlreadyExistsException | InvalidEmailException | InvalidFieldException e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			throw e;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			e.printStackTrace();
			throw new RuntimeException(msg("DataAccess.ErrorRegisterSeller") + ": " + e.getMessage(), e);
		}
	}

	/**
	 * Registra la aceptación de una oferta
	 * @param buyerEmail email del comprador
	 * @param saleNumber número de la oferta
	 * @param negotiatedPrice el precio que ofrece el comprador (null si acepta el original)
	 * @return AcceptedOffer si se aceptó exitosamente
	 * @throws InvalidPriceException si el precio negociado es inválido
	 */
	public AcceptedOffer acceptOffer(String buyerEmail, Integer saleNumber, Float negotiatedPrice) 
			throws InvalidPriceException {
		try {
			db.getTransaction().begin();
			
			Buyer buyer = db.find(Buyer.class, buyerEmail);
			Sale sale = db.find(Sale.class, saleNumber);
			
			if (buyer == null || sale == null) {
				db.getTransaction().rollback();
				return null;
			}
			
			// Validar precio negociado si existe
			if (negotiatedPrice != null) {
				// El precio debe ser positivo
				if (negotiatedPrice <= 0) {
					db.getTransaction().rollback();
					throw new InvalidPriceException(msg("DataAccess.ErrorPriceMustBePositive"));
				}
				// El precio negociado no puede ser mayor que el precio original
				if (negotiatedPrice > sale.getPrice()) {
					db.getTransaction().rollback();
					throw new InvalidPriceException(MessageFormat.format(
						msg("DataAccess.ErrorNegotiatedPriceTooHigh"),
						negotiatedPrice,
						sale.getPrice()));
				}
			}

			TypedQuery<Long> existingOfferQuery = db.createQuery(
				"SELECT COUNT(a) FROM AcceptedOffer a WHERE a.sale.saleNumber = :saleNumber AND a.buyer.email = :buyerEmail",
				Long.class);
			existingOfferQuery.setParameter("saleNumber", saleNumber);
			existingOfferQuery.setParameter("buyerEmail", buyerEmail);
			if (existingOfferQuery.getSingleResult() > 0) {
				db.getTransaction().rollback();
				return null;
			}
			
			// Crear registro de aceptación con precio negociado
			AcceptedOffer accepted = new AcceptedOffer(buyer, sale, negotiatedPrice);
			accepted.setEstado(AcceptedOffer.EstadoOferta.PENDIENTE);
			
			// Añadir a la lista del comprador
			buyer.addAcceptedOffer(accepted);
			
			// Persistir
			db.persist(accepted);
			
			db.getTransaction().commit();
			System.out.println("Oferta aceptada. Precio: " + accepted.getFinalPrice());
			return accepted;
		} catch (InvalidPriceException e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			throw e;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Obtiene ofertas aceptadas de un vendedor
	 */
	public List<AcceptedOffer> getAcceptedOffersBySeller(String sellerEmail) {
		try {
			// Consulta JPQL
			TypedQuery<AcceptedOffer> query = db.createQuery(
				"SELECT a FROM AcceptedOffer a " +
				"WHERE a.sale.seller.email = :email", 
				AcceptedOffer.class);
			
			query.setParameter("email", sellerEmail);
			
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	/**
	 * Obtiene ofertas aceptadas de un comprador
	 */
	public List<AcceptedOffer> getAcceptedOffersByBuyer(String buyerEmail) {
		try {
			TypedQuery<AcceptedOffer> query = db.createQuery(
				"SELECT a FROM AcceptedOffer a " +
				"WHERE a.buyer.email = :email",
				AcceptedOffer.class);

				query.setParameter("email", buyerEmail);

			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	/**
	 * Obtiene ofertas disponibles (incluye aceptadas)
	 */
	public List<Sale> getAvailableSalesForBuyer(Date pubDate) {
		try {
			TypedQuery<Sale> query = db.createQuery(
				"SELECT s FROM Sale s WHERE s.pubDate <= :pubDate",
				Sale.class);
			query.setParameter("pubDate", pubDate);
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	/**
	 * Obtiene las ofertas creadas por un vendedor
	 */
	public List<Sale> getSalesBySellerEmail(String sellerEmail) {
		try {
			TypedQuery<Sale> query = db.createQuery(
				"SELECT s FROM Sale s WHERE s.seller.email = :email",
				Sale.class);
			query.setParameter("email", sellerEmail);
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	/**
	 * Registra la decisión de venta de un comprador
	 */
    public DecisionVenta decidirComprador(Integer saleNumber, Integer acceptedOfferId,
            String criterio, String motivo) {
    	try {
    		db.getTransaction().begin();
    		
    		Sale sale = db.find(Sale.class, saleNumber);
    		AcceptedOffer acceptedOffer = db.find(AcceptedOffer.class, acceptedOfferId);
    		
	    		if (sale == null || acceptedOffer == null) {
    			db.getTransaction().rollback();
    			return null;	
    		}

	    		if (!acceptedOffer.getSale().getSaleNumber().equals(saleNumber)) {
	    			db.getTransaction().rollback();
	    			return null;
	    		}

	    		if (existsDecisionForSale(saleNumber)) {
	    			db.getTransaction().rollback();
	    			return null;
	    		}

	    		String criterioFinal = (criterio == null || criterio.trim().isEmpty())
	    				? CriterioDecision.OTRO.name()
	    				: criterio.trim();
	    		String motivoFinal = (motivo == null)
	    				? ""
	    				: motivo.trim();
    		
			DecisionVenta decision = new DecisionVenta(
				sale,
				acceptedOffer,
				sale.getSeller().getEmail(),
				criterioFinal,
				motivoFinal);
    		
    		db.persist(decision);
			acceptedOffer.setEstado(AcceptedOffer.EstadoOferta.ACEPTADA);
			db.merge(acceptedOffer);

			TypedQuery<AcceptedOffer> otherOffersQuery = db.createQuery(
				"SELECT a FROM AcceptedOffer a WHERE a.sale.saleNumber = :saleNumber AND a.id <> :acceptedOfferId",
				AcceptedOffer.class);
			otherOffersQuery.setParameter("saleNumber", saleNumber);
			otherOffersQuery.setParameter("acceptedOfferId", acceptedOfferId);
			List<AcceptedOffer> otherOffers = otherOffersQuery.getResultList();
			for (AcceptedOffer otherOffer : otherOffers) {
				if (otherOffer.getEstado() == null || otherOffer.getEstado() == AcceptedOffer.EstadoOferta.PENDIENTE) {
					otherOffer.setEstado(AcceptedOffer.EstadoOferta.RECHAZADA);
					db.merge(otherOffer);
				}
			}

	    		float importe = acceptedOffer.getFinalPrice();
	    		if (importe <= 0) {
	    			db.getTransaction().rollback();
	    			return null;
	    		}

			TransaccionPago transaccion = new TransaccionPago(
				saleNumber,
				acceptedOffer.getBuyer().getEmail(),
				importe);
			transaccion.setEstado(TransaccionPago.EstadoPago.PENDIENTE);
			transaccion.setReferenciaExterna("REF-" + System.currentTimeMillis());
			transaccion.setEstado(TransaccionPago.EstadoPago.CONFIRMADO);
			db.persist(transaccion);

    		db.getTransaction().commit();
    		return decision;
    	} catch (Exception e) {
    		if (db.getTransaction().isActive()) {
    			db.getTransaction().rollback();	
    		}
    		e.printStackTrace();
    		return null;	
    	}	
    }
    
    /**
     * Registra el cobro de una venta aceptada
     */
	public TransaccionPago procesarCobro(Integer saleNumber, String buyerEmail, float importe) {
        try {
            if (importe <= 0) {
				return null;
            }

            db.getTransaction().begin();

            Sale sale = db.find(Sale.class, saleNumber);
            Buyer buyer = db.find(Buyer.class, buyerEmail);

            if (sale == null || buyer == null) {
                db.getTransaction().rollback();
                return null;
            }

			DecisionVenta decision = getDecisionBySale(saleNumber);
			if (decision == null || decision.getAcceptedOffer() == null ||
				!decision.getAcceptedOffer().getBuyer().getEmail().equalsIgnoreCase(buyerEmail)) {
				db.getTransaction().rollback();
				return null;
			}

			TypedQuery<TransaccionPago> existingQuery = db.createQuery(
				"SELECT t FROM TransaccionPago t WHERE t.saleNumber = :saleNumber " +
				"AND t.buyerEmail = :buyerEmail AND t.estado = :estado ORDER BY t.id DESC",
				TransaccionPago.class);
			existingQuery.setParameter("saleNumber", saleNumber);
			existingQuery.setParameter("buyerEmail", buyerEmail);
			existingQuery.setParameter("estado", TransaccionPago.EstadoPago.CONFIRMADO);
			existingQuery.setMaxResults(1);
			List<TransaccionPago> existing = existingQuery.getResultList();
			if (!existing.isEmpty()) {
				db.getTransaction().rollback();
				return existing.get(0);
			}

			TransaccionPago transaccion = new TransaccionPago(saleNumber, buyerEmail, importe);
			transaccion.setEstado(TransaccionPago.EstadoPago.PENDIENTE);
			transaccion.setEstado(TransaccionPago.EstadoPago.CONFIRMADO);
			transaccion.setReferenciaExterna("REF-" + System.currentTimeMillis());

            db.persist(transaccion);
            db.getTransaction().commit();
            return transaccion;
        } catch (Exception e) {
            if (db.getTransaction().isActive()) {
                db.getTransaction().rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

	public List<TransaccionPago> getTransaccionesBySale(Integer saleNumber) {
		try {
			TypedQuery<TransaccionPago> query = db.createQuery(
				"SELECT t FROM TransaccionPago t WHERE t.saleNumber = :saleNumber",
				TransaccionPago.class);
			query.setParameter("saleNumber", saleNumber);
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

    /**
	 * Calcula la comisión del marketplace para una transacción de pago dada.
	 */
    public ComisionMarketplace calcularComision(Integer transaccionPagoId, float porcentaje) {
        try {
            db.getTransaction().begin();

            TransaccionPago transaccion = db.find(TransaccionPago.class, transaccionPagoId);
			if (transaccion == null ||
				!transaccion.getEstado().equals(TransaccionPago.EstadoPago.CONFIRMADO)) {
				db.getTransaction().rollback();
                return null;
            }

			ComisionMarketplace existing = getComisionByTransaccionId(transaccionPagoId);
			if (existing != null) {
				if (db.getTransaction().isActive()) {
					db.getTransaction().rollback();
				}
				return existing;
			}

			Sale sale = db.find(Sale.class, transaccion.getSaleNumber());
			if (sale == null) {
				db.getTransaction().rollback();
				return null;
			}

			ComisionMarketplace comision = new ComisionMarketplace(
				transaccionPagoId,
				sale.getSeller().getEmail(),
				transaccion.getImporte(),
				porcentaje);
            db.persist(comision);
			db.getTransaction().commit();
            return comision;
        } catch (Exception e) {
            e.printStackTrace();
            if (db.getTransaction().isActive()) {
                db.getTransaction().rollback();
            }
            return null;
        }
    }

	/**
	 * Liquida una comisión ya calculada.
	 */
	public ComisionMarketplace liquidarComision(Integer comisionId) {
		try {
			db.getTransaction().begin();
			ComisionMarketplace comision = db.find(ComisionMarketplace.class, comisionId);
			if (comision == null) {
				db.getTransaction().rollback();
				return null;
			}

			String currentState = comision.getEstado();
			if (!isCommissionState(currentState, "ComisionMarketplace.Estado.CALCULADA") &&
				!isCommissionState(currentState, "ComisionMarketplace.Estado.LIQUIDADA")) {
				db.getTransaction().rollback();
				return null;
			}

			if (isCommissionState(currentState, "ComisionMarketplace.Estado.LIQUIDADA")) {
				db.getTransaction().rollback();
				return comision;
			}

			comision.setEstado("ComisionMarketplace.Estado.LIQUIDADA");
			db.merge(comision);
			db.getTransaction().commit();
			return comision;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			e.printStackTrace();
			return null;
		}
	}
    
    /**
     * Registra una solicitud de reembolso para una transacción de pago dada, con validaciones para el importe y tipo de reembolso.
     */
	public Reembolso solicitarReembolso(Integer transaccionPagoId, float importe,
            String motivo, String buyerEmail, 
            String vendedorEmail, String observaciones) {
			try {
				db.getTransaction().begin();
				TransaccionPago transaccion = db.find(TransaccionPago.class, transaccionPagoId);
				if (transaccion == null) return null;

				Reembolso.TipoReembolso tipo = (importe >= transaccion.getImporte())
						? Reembolso.TipoReembolso.TOTAL
								: Reembolso.TipoReembolso.PARCIAL;

				// Necesitas un constructor con 7 parámetros
				Reembolso reembolso = new Reembolso(transaccionPagoId, buyerEmail,
                     vendedorEmail, tipo, importe, 
                     motivo, observaciones);
				reembolso.setEstado("EstadoReembolso.COMPLETADO");
				reembolso.setEstadoResolucion("EstadoReembolso.COMPLETADO");
				reembolso.setFechaResolucion(new Date());

				transaccion.setEstado(TransaccionPago.EstadoPago.REEMBOLSADO);
				db.merge(transaccion);
				db.persist(reembolso);
				db.flush();
				db.getTransaction().commit();
				return reembolso;
			} catch (Exception e) {
				if (db.getTransaction().isActive()) {
					db.getTransaction().rollback();
				}
				e.printStackTrace();
				return null;
			}
	}
    
	/**
	 * Obtiene las decisiones de venta registradas para un vendedor dado
	 * @param sellerEmail el email del vendedor
	 * @return una lista de objetos DecisionVenta asociados al vendedor, o una lista vacía si no se encuentran registros
	 */
    public List<DecisionVenta> getDecisionVentasBySeller(String sellerEmail) {
        TypedQuery<DecisionVenta> query = db.createQuery(
			"SELECT d FROM DecisionVenta d WHERE d.sellerEmail = :email",
            DecisionVenta.class);
        query.setParameter("email", sellerEmail);
        return query.getResultList();
    }

	/**
	 * Obtiene las comisiones del marketplace registradas para un vendedor dado
	 * @param sellerEmail el email del vendedor
	 * @return una lista de objetos ComisionMarketplace asociados al vendedor, o una lista vacía si no se encuentran registros
	 */
    public List<ComisionMarketplace> getComisionesBySeller(String sellerEmail) {
		try {
			TypedQuery<ComisionMarketplace> query = db.createQuery(
				"SELECT c FROM ComisionMarketplace c WHERE c.sellerEmail = :email",
				ComisionMarketplace.class);
			query.setParameter("email", sellerEmail);
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
    }

	/**
	 * Obtiene los reembolsos registrados para un comprador dado
	 * @param buyerEmail el email del comprador
	 * @return una lista de objetos Reembolso asociados al comprador, o una lista vacía si no se encuentran registros
	 */
    public List<Reembolso> getReembolsosByBuyer(String buyerEmail) {
		try {
			TypedQuery<Reembolso> query = db.createQuery(
				"SELECT r FROM Reembolso r WHERE r.buyerEmail = :email",
				Reembolso.class);
			query.setParameter("email", buyerEmail);
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
    }

	/**
	 * Valida el formato de un email
	 * @param email el email a validar
	 * @return true si el formato es válido
	 */
	private boolean isValidEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}
		// Expresión regular básica para validar email
		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
		return email.matches(emailRegex);
	}

	private boolean existsDecisionForSale(Integer saleNumber) {
		TypedQuery<Long> query = db.createQuery(
			"SELECT COUNT(d) FROM DecisionVenta d WHERE d.saleNumber = :saleNumber",
			Long.class);
		query.setParameter("saleNumber", saleNumber);
		return query.getSingleResult() > 0;
	}

	public DecisionVenta getDecisionBySale(Integer saleNumber) {
		TypedQuery<DecisionVenta> query = db.createQuery(
			"SELECT d FROM DecisionVenta d WHERE d.saleNumber = :saleNumber ORDER BY d.id DESC",
			DecisionVenta.class);
		query.setParameter("saleNumber", saleNumber);
		query.setMaxResults(1);
		List<DecisionVenta> decisions = query.getResultList();
		return decisions.isEmpty() ? null : decisions.get(0);
	}

	public ComisionMarketplace getComisionByTransaccionId(Integer transaccionPagoId) {
		TypedQuery<ComisionMarketplace> query = db.createQuery(
			"SELECT c FROM ComisionMarketplace c WHERE c.transaccionPagoId = :transaccionPagoId",
			ComisionMarketplace.class);
		query.setParameter("transaccionPagoId", transaccionPagoId);
		query.setMaxResults(1);
		List<ComisionMarketplace> commissions = query.getResultList();
		return commissions.isEmpty() ? null : commissions.get(0);
	}

	public boolean isCommissionState(String value, String expectedKey) {
		if (value == null) {
			return false;
		}
		if (expectedKey.equals(value)) {
			return true;
		}
		try {
			return msg(expectedKey).equals(value);
		} catch (Exception e) {
			return false;
		}
	}
	
	public Sale findSale(Integer saleNumber) {
	    return db.find(Sale.class, saleNumber);
	}

	/**
	 * Obtiene la transacción de pago confirmada para una venta
	 */
	public TransaccionPago getTransaccionConfirmadaBySale(Integer saleNumber) {
	    TypedQuery<TransaccionPago> query = db.createQuery(
	        "SELECT t FROM TransaccionPago t WHERE t.saleNumber = :saleNumber " +
	        "AND t.estado = :estado ORDER BY t.id DESC", 
	        TransaccionPago.class);
	    query.setParameter("saleNumber", saleNumber);
	    query.setParameter("estado", TransaccionPago.EstadoPago.CONFIRMADO);
	    query.setMaxResults(1);
	    List<TransaccionPago> results = query.getResultList();
	    return results.isEmpty() ? null : results.get(0);
	}

	/**
	 * Verifica si ya existe un reembolso para una transacción
	 */
	public boolean existeReembolsoPorTransaccion(Integer transaccionPagoId) {
	    try {
	        TypedQuery<Long> query = db.createQuery(
	            "SELECT COUNT(r) FROM Reembolso r WHERE r.transaccionPagoId = :transaccionId",
	            Long.class);
	        query.setParameter("transaccionId", transaccionPagoId);
	        return query.getSingleResult() > 0;
	    } catch (PersistenceException e) {
	        return false;
	    }
	}

	public List<String> getApprovedCategories() {
		TypedQuery<CategoryDefinition> query = db.createQuery(
			"SELECT c FROM CategoryDefinition c WHERE c.approved = true ORDER BY c.name",
			CategoryDefinition.class);
		List<CategoryDefinition> categories = query.getResultList();
		List<String> result = new ArrayList<String>();
		for (CategoryDefinition category : categories) {
			result.add(category.getName());
		}
		return result;
	}

	public List<String> getPendingCategoryProposals() {
		TypedQuery<CategoryDefinition> query = db.createQuery(
			"SELECT c FROM CategoryDefinition c WHERE c.approved = false ORDER BY c.createdAt",
			CategoryDefinition.class);
		List<CategoryDefinition> categories = query.getResultList();
		List<String> result = new ArrayList<String>();
		for (CategoryDefinition category : categories) {
			result.add(category.getName());
		}
		return result;
	}

	public void proposeCategory(String category, String proposerEmail) {
		String normalized = normalizeCategory(category);
		if (normalized.isEmpty()) {
			return;
		}
		db.getTransaction().begin();
		try {
			CategoryDefinition existing = db.find(CategoryDefinition.class, normalized);
			if (existing == null) {
				db.persist(new CategoryDefinition(normalized, false, proposerEmail));
			}
			db.getTransaction().commit();
		} catch (RuntimeException e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			throw e;
		}
	}

	public void approveCategory(String category) {
		String normalized = normalizeCategory(category);
		if (normalized.isEmpty()) {
			return;
		}
		db.getTransaction().begin();
		try {
			CategoryDefinition existing = db.find(CategoryDefinition.class, normalized);
			if (existing == null) {
				db.persist(new CategoryDefinition(normalized, true, "system"));
			} else {
				existing.setApproved(true);
				db.merge(existing);
			}
			db.getTransaction().commit();
		} catch (RuntimeException e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			throw e;
		}
	}

	public void assignCategoryToSale(Integer saleNumber, String category) {
		if (saleNumber == null) {
			return;
		}
		String normalized = normalizeCategory(category);
		if (normalized.isEmpty()) {
			return;
		}
		db.getTransaction().begin();
		try {
			Sale sale = db.find(Sale.class, saleNumber);
			if (sale != null) {
				sale.setCategory(normalized);
				db.merge(sale);
			}
			CategoryDefinition existing = db.find(CategoryDefinition.class, normalized);
			if (existing == null) {
				db.persist(new CategoryDefinition(normalized, true, "system"));
			}
			db.getTransaction().commit();
		} catch (RuntimeException e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			throw e;
		}
	}

	public int seedDemoSalesIfNeeded() {
		long current;
		try {
			TypedQuery<Long> countQuery = db.createQuery("SELECT COUNT(s) FROM Sale s", Long.class);
			current = countQuery.getSingleResult();
		} catch (PersistenceException e) {
			// In a fresh ObjectDB file the entity metadata may not exist yet.
			// Treat this as an empty dataset so demo seeding can bootstrap it.
			current = 0L;
		}
		int target = 20;
		if (current >= target) {
			db.getTransaction().begin();
			try {
				ensureDefaultCategoriesPersisted();
				db.getTransaction().commit();
			} catch (RuntimeException e) {
				if (db.getTransaction().isActive()) {
					db.getTransaction().rollback();
				}
				throw e;
			}
			return 0;
		}

		Seller seller = db.find(Seller.class, "seller3@gmail.com");
		if (seller == null) {
			db.getTransaction().begin();
			seller = new Seller("seller3@gmail.com", "Demo Seller", "ES93 1111 2222 3333 4444 5555");
			seller.setPassword("pass3");
			db.persist(seller);
			db.getTransaction().commit();
		}

		List<String> titles = Arrays.asList(
			"iPhone 15 128GB", "Portatil Lenovo ThinkPad", "Monitor LG 27 pulgadas", "Teclado mecanico RGB",
			"Bicicleta MTB 29", "Balon futbol Adidas", "Raqueta padel Bullpadel", "Botas trekking Salomon",
			"Sofa 3 plazas gris", "Lampara escritorio LED", "Mesa comedor roble", "Silla ergonomica oficina",
			"Chaqueta vaquera", "Zapatillas running Nike", "Vestido verano azul", "Camisa lino blanca",
			"Casco moto integral", "Llantas aleacion 17", "Figura coleccion anime", "Vinilo clasicos rock"
		);
		List<String> descs = Arrays.asList(
			"Muy cuidado, funciona perfecto", "Estado excelente, bateria con buena salud", "Poco uso y sin golpes",
			"Ideal para uso diario y trabajo", "Con accesorios originales"
		);
		Random random = new Random(26);
		int created = 0;

		db.getTransaction().begin();
		try {
			for (int i = (int) current; i < target; i++) {
				String title = titles.get(i % titles.size()) + " #" + (i + 1);
				if (seller.doesSaleExist(title)) {
					continue;
				}
				float price = 20f + random.nextInt(780);
				String desc = descs.get(random.nextInt(descs.size()));
				Sale sale = seller.addSale(title, desc, 2, price, UtilDate.trim(new Date()), null);
				sale.setCategory(inferCategory(title, desc));
				db.persist(sale);
				created++;
			}
			db.merge(seller);
			ensureDefaultCategoriesPersisted();
			db.getTransaction().commit();
		} catch (RuntimeException e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			throw e;
		}
		return created;
	}

	private void ensureDefaultCategoriesPersisted() {
		for (String category : Arrays.asList("Tecnologia", "Hogar", "Deporte", "Moda", "Motor", "Coleccionismo", "General")) {
			CategoryDefinition existing = db.find(CategoryDefinition.class, category);
			if (existing == null) {
				db.persist(new CategoryDefinition(category, true, "system"));
			}
		}
	}

	private String normalizeCategory(String category) {
		if (category == null) {
			return "";
		}
		String value = category.trim();
		if (value.isEmpty()) {
			return "";
		}
		return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
	}

	private String inferCategory(String title, String description) {
		String text = ((title == null ? "" : title) + " " + (description == null ? "" : description)).toLowerCase();
		if (containsAny(text, "iphone", "android", "pc", "portatil", "tablet", "teclado", "monitor")) {
			return "Tecnologia";
		}
		if (containsAny(text, "bici", "bicicleta", "botas", "futbol", "deporte", "raqueta", "trekking", "padel")) {
			return "Deporte";
		}
		if (containsAny(text, "camisa", "zapatillas", "chaqueta", "moda", "vestido")) {
			return "Moda";
		}
		if (containsAny(text, "coche", "moto", "motor", "casco", "llanta")) {
			return "Motor";
		}
		if (containsAny(text, "silla", "mesa", "sofa", "lampara", "hogar", "comedor")) {
			return "Hogar";
		}
		if (containsAny(text, "coleccion", "cromo", "figura", "vinilo")) {
			return "Coleccionismo";
		}
		return "General";
	}

	private boolean containsAny(String text, String... words) {
		for (String word : words) {
			if (text.contains(word)) {
				return true;
			}
		}
		return false;
	}


}
