package dataAccess;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.Seller;
import domain.User;
import domain.AcceptedOffer;
import domain.Buyer;
import domain.Sale;
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
		

		System.out.println(">> DataAccess: createProduct=> title= "+title+" seller="+sellerEmail);
		try {
			// Validar campos no vacíos
			if (title == null || title.trim().isEmpty()) {
				throw new InvalidFieldException("El título es obligatorio");
			}
			if (description == null || description.trim().isEmpty()) {
				throw new InvalidFieldException("La descripción es obligatoria");
			}
			
			// Validar precio positivo
			if (price <= 0) {
				throw new InvalidPriceException("El precio debe ser mayor a 0");
			}

			if(pubDate.before(UtilDate.trim(new Date()))) {
				throw new MustBeLaterThanTodayException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.ErrorSaleMustBeLaterThanToday"));
			}
			db.getTransaction().begin();
			
			Seller seller = db.find(Seller.class, sellerEmail);
			if (seller == null) {
				db.getTransaction().rollback();
				return null;
			}
			if (seller.doesSaleExist(title)) {
				db.getTransaction().commit();
				throw new SaleAlreadyExistException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.SaleAlreadyExist"));
			}

			Sale sale = seller.addSale(title, description, status, price, pubDate, file);
			db.persist(sale);
			db.getTransaction().commit();
			 System.out.println("sale stored "+sale+ " "+seller);

			return sale;
		} catch (NullPointerException e) {
			e.printStackTrace();
			db.getTransaction().commit();
			return null;
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
	
	public BufferedImage rescale(BufferedImage originalImage)
    {
		System.out.println("rescale "+originalImage);
        BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
        g.dispose();
        return resizedImage;
    }
	
	public void close(){
		db.close();
		System.out.println("DataAcess closed");
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
				throw new InvalidFieldException("El email es obligatorio");
			}
			if (name == null || name.trim().isEmpty()) {
				throw new InvalidFieldException("El nombre es obligatorio");
			}
			if (password == null || password.trim().isEmpty()) {
				throw new InvalidFieldException("La contraseña es obligatoria");
			}
			if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
				throw new InvalidFieldException("La dirección de envío es obligatoria");
			}
			
			// Validar formato de email
			if (!isValidEmail(email)) {
				throw new InvalidEmailException("El formato del email es inválido");
			}
			
			db.getTransaction().begin();

			// Validar email no duplicado
			User existing = db.find(User.class, email);
			if (existing != null) {
				db.getTransaction().rollback();
				throw new UserAlreadyExistsException("El email ya está registrado");
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
			throw new RuntimeException("Error al registrar comprador: " + e.getMessage(), e);
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
				throw new InvalidFieldException("El email es obligatorio");
			}
			if (name == null || name.trim().isEmpty()) {
				throw new InvalidFieldException("El nombre es obligatorio");
			}
			if (password == null || password.trim().isEmpty()) {
				throw new InvalidFieldException("La contraseña es obligatoria");
			}
			if (bankAccount == null || bankAccount.trim().isEmpty()) {
				throw new InvalidFieldException("La cuenta bancaria es obligatoria");
			}
			
			// Validar formato de email
			if (!isValidEmail(email)) {
				throw new InvalidEmailException("El formato del email es inválido");
			}
			
			db.getTransaction().begin();

			// Validar email no duplicado
			User existing = db.find(User.class, email);
			if (existing != null) {
				db.getTransaction().rollback();
				throw new UserAlreadyExistsException("El email ya está registrado");
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
			throw new RuntimeException("Error al registrar vendedor: " + e.getMessage(), e);
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
					throw new InvalidPriceException("El precio debe ser mayor a 0");
				}
				// El precio negociado no puede ser mayor que el precio original
				if (negotiatedPrice > sale.getPrice()) {
					db.getTransaction().rollback();
					throw new InvalidPriceException("El precio negociado (" + negotiatedPrice + 
						") no puede ser mayor que el precio de la oferta (" + sale.getPrice() + ")");
				}
			}
			
			// Crear registro de aceptación con precio negociado
			AcceptedOffer accepted = new AcceptedOffer(buyer, sale, negotiatedPrice);
			
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
			TypedQuery<Sale> queryPublished = db.createQuery(
				"SELECT s FROM Sale s",
				Sale.class);
			return queryPublished.getResultList();
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
	
	// ============== MÉTODOS DE VALIDACIÓN ==============
	
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
}
