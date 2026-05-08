package businessLogic;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private final Map<String, Map<Integer, Float>> lastFeedScores = new HashMap<String, Map<Integer, Float>>();
	private final Map<String, Map<Integer, String>> lastFeedReasons = new HashMap<String, Map<Integer, String>>();
	private final Map<String, Map<Integer, String>> lastAlertReasons = new HashMap<String, Map<Integer, String>>();
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

	@WebMethod
	public synchronized List<String> getAllCategories() {
		return execute(() -> dbManager.getApprovedCategories());
	}

	@WebMethod
	public synchronized List<String> getPendingCategoryProposals() {
		return execute(() -> dbManager.getPendingCategoryProposals());
	}

	@WebMethod
	public synchronized List<String> suggestCategories(String title, String description) {
		List<String> suggestions = new ArrayList<String>();
		String inferred = inferCategory(title, description);
		String aiSuggestion = callMistralCategorySuggestion(title, description, getAllCategories());
		if (aiSuggestion != null && !aiSuggestion.trim().isEmpty()) {
			inferred = aiSuggestion;
		}
		suggestions.add(inferred);
		for (String category : getAllCategories()) {
			if (category.equalsIgnoreCase(inferred)) {
				continue;
			}
			if (matchesCategoryKeywords(category, title, description)) {
				if (!suggestions.contains(category)) {
					suggestions.add(category);
				}
			}
			if (suggestions.size() >= 3) {
				break;
			}
		}
		return suggestions;
	}

	private String callMistralCategorySuggestion(String title, String description, List<String> categories) {
		// Para probarlo de manera menos segura, sin complicaciones de path, te dejo una API KEY
		String apiKey = System.getenv("MISTRAL_API"); // Comentar
		//String apiKey = "PASTE HERE"; // Desconmentar
		if (apiKey == null || apiKey.trim().isEmpty()) {
			return null;
		}
		if (categories == null || categories.isEmpty()) {
			return null;
		}

		String prompt = "Devuelve solo el nombre de la categoria mas adecuada, sin comillas. " +
			"Si ninguna encaja, puedes sugerir una categoria nueva. " +
			"Categorias existentes: " + String.join(", ", categories) + ". " +
			"Titulo: " + (title == null ? "" : title) + ". " +
			"Descripcion: " + (description == null ? "" : description) + ".";
		String payload = "{\"model\":\"mistral-small-2603\",\"messages\":[{\"role\":\"user\",\"content\":\"" +
			escapeJson(prompt) + "\"}],\"temperature\":0.1,\"max_tokens\":20}";

		try {
			URL url = new URL("https://api.mistral.ai/v1/chat/completions");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(4000);
			connection.setReadTimeout(5000);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Authorization", "Bearer " + apiKey.trim());

			try (OutputStream os = connection.getOutputStream()) {
				os.write(payload.getBytes("UTF-8"));
			}

			InputStream responseStream = connection.getResponseCode() >= 200 && connection.getResponseCode() < 300
				? connection.getInputStream()
				: connection.getErrorStream();
			if (responseStream == null) {
				return null;
			}
			String response = readAll(responseStream);
			String content = extractContent(response);
			if (content == null) {
				return null;
			}
			String normalized = normalizeCategorySuggestion(content);
			if (normalized == null) {
				return null;
			}
			for (String category : categories) {
				if (category.equalsIgnoreCase(normalized)) {
					return category;
				}
			}
			return normalized;
		} catch (IOException ex) {
			return null;
		}
	}

	private String normalizeCategorySuggestion(String content) {
		if (content == null) {
			return null;
		}
		String value = content.trim();
		if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
			value = value.substring(1, value.length() - 1).trim();
		}
		if (value.isEmpty() || value.length() > 40) {
			return null;
		}
		if (value.contains("\n") || value.contains("\r")) {
			return null;
		}
		return value;
	}

	private String extractContent(String response) {
		if (response == null) {
			return null;
		}
		Pattern pattern = Pattern.compile("\\\"content\\\"\\s*:\\s*\\\"(.*?)\\\"", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(response);
		if (!matcher.find()) {
			return null;
		}
		return unescapeJson(matcher.group(1));
	}

	private String readAll(InputStream input) throws IOException {
		StringBuilder builder = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
		}
		return builder.toString();
	}

	private String escapeJson(String value) {
		if (value == null) {
			return "";
		}
		return value.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t");
	}

	private String unescapeJson(String value) {
		if (value == null) {
			return null;
		}
		return value.replace("\\n", "\n")
			.replace("\\r", "\r")
			.replace("\\t", "\t")
			.replace("\\\"", "\"")
			.replace("\\\\", "\\");
	}

	@WebMethod
	public synchronized void proposeCategory(String category, String proposerEmail) {
		execute(() -> {
			dbManager.proposeCategory(category, proposerEmail);
			return null;
		});
	}

	@WebMethod
	public synchronized void approveCategory(String category) {
		execute(() -> {
			dbManager.approveCategory(category);
			return null;
		});
	}

	@WebMethod
	public synchronized void assignCategoryToSale(Integer saleNumber, String category) {
		execute(() -> {
			dbManager.assignCategoryToSale(saleNumber, category);
			return null;
		});
	}

	@WebMethod
	public synchronized String getCategoryForSale(Integer saleNumber) {
		if (saleNumber == null) {
			return "General";
		}
		Sale sale = execute(() -> dbManager.findSale(saleNumber));
		if (sale == null) {
			return "General";
		}
		if (sale.getCategory() != null && !sale.getCategory().trim().isEmpty()) {
			return sale.getCategory();
		}
		String inferred = inferCategory(sale.getTitle(), sale.getDescription());
		assignCategoryToSale(saleNumber, inferred);
		return inferred;
	}

	@WebMethod
	public int seedDemoSalesIfNeeded() {
		return execute(() -> dbManager.seedDemoSalesIfNeeded());
	}

	@WebMethod
	public synchronized List<Sale> getPersonalizedFeed(String buyerEmail, int limit) {
		List<Sale> sales = execute(() -> dbManager.getAvailableSalesForBuyer(new Date()));
		if (sales == null || sales.isEmpty()) {
			lastFeedScores.put(buyerEmail, new HashMap<Integer, Float>());
			lastFeedReasons.put(buyerEmail, new HashMap<Integer, String>());
			return new ArrayList<Sale>();
		}

		Map<Integer, Float> scores = new HashMap<Integer, Float>();
		Map<Integer, String> reasons = new HashMap<Integer, String>();
		Map<String, Integer> preferredCategories = getBuyerPreferredCategories(buyerEmail);
		float targetPrice = getBuyerTargetPrice(buyerEmail, sales);
		Map<Integer, Integer> popularity = getSalesPopularity();
		int maxPopularity = 1;
		for (Integer value : popularity.values()) {
			if (value != null && value > maxPopularity) {
				maxPopularity = value;
			}
		}

		for (Sale sale : sales) {
			String category = resolveCategoryForSale(sale);
			float categoryScore = computeCategoryScore(category, preferredCategories);
			float priceScore = computePriceScore(sale.getPrice(), targetPrice);
			float freshnessScore = computeFreshnessScore(sale.getPublicationDate());
			float popularityScore = (float) popularity.getOrDefault(sale.getSaleNumber(), 0) / (float) maxPopularity;
			float finalScore = 0.45f * categoryScore + 0.25f * priceScore +
				0.20f * freshnessScore + 0.10f * popularityScore;
			scores.put(sale.getSaleNumber(), finalScore);
			reasons.put(sale.getSaleNumber(), buildReason(categoryScore, priceScore, freshnessScore, category));
		}

		Collections.sort(sales, new Comparator<Sale>() {
			public int compare(Sale a, Sale b) {
				Float sa = scores.getOrDefault(a.getSaleNumber(), 0f);
				Float sb = scores.getOrDefault(b.getSaleNumber(), 0f);
				return sb.compareTo(sa);
			}
		});

		if (limit > 0 && sales.size() > limit) {
			sales = new ArrayList<Sale>(sales.subList(0, limit));
		}
		lastFeedScores.put(buyerEmail, scores);
		lastFeedReasons.put(buyerEmail, reasons);
		return sales;
	}

	@WebMethod
	public synchronized float getRecommendationScore(String buyerEmail, Integer saleNumber) {
		Map<Integer, Float> scoreMap = lastFeedScores.getOrDefault(buyerEmail, Collections.<Integer, Float>emptyMap());
		if (!scoreMap.containsKey(saleNumber)) {
			getPersonalizedFeed(buyerEmail, 20);
			scoreMap = lastFeedScores.getOrDefault(buyerEmail, Collections.<Integer, Float>emptyMap());
		}
		return scoreMap.getOrDefault(saleNumber, 0f);
	}

	@WebMethod
	public synchronized String getRecommendationReason(String buyerEmail, Integer saleNumber) {
		Map<Integer, String> reasonMap = lastFeedReasons.getOrDefault(buyerEmail, Collections.<Integer, String>emptyMap());
		if (!reasonMap.containsKey(saleNumber)) {
			getPersonalizedFeed(buyerEmail, 20);
			reasonMap = lastFeedReasons.getOrDefault(buyerEmail, Collections.<Integer, String>emptyMap());
		}
		return reasonMap.getOrDefault(saleNumber, "Recomendacion general por actividad reciente");
	}

	@WebMethod
	public synchronized List<Sale> getOpportunityAlerts(String buyerEmail, String category,
			String keyword, Float maxPrice, int limit) {
		List<Sale> ranked = getPersonalizedFeed(buyerEmail, 0);
		Map<Integer, String> reasons = new HashMap<Integer, String>();
		List<Sale> filtered = new ArrayList<Sale>();
		String requestedCategory = normalizeCategory(category);
		String requestedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
		float buyerTargetPrice = getBuyerTargetPrice(buyerEmail, ranked);

		for (Sale sale : ranked) {
			String saleCategoryName = resolveCategoryForSale(sale);
			if (!requestedCategory.isEmpty() && !saleCategoryName.equalsIgnoreCase(requestedCategory)) {
				continue;
			}
			if (!requestedKeyword.isEmpty()) {
				String text = (sale.getTitle() + " " + sale.getDescription()).toLowerCase();
				if (!text.contains(requestedKeyword)) {
					continue;
				}
			}
			if (maxPrice != null && maxPrice > 0 && sale.getPrice() > maxPrice) {
				continue;
			}

			float score = getRecommendationScore(buyerEmail, sale.getSaleNumber());
			boolean greatPrice = buyerTargetPrice > 0 && sale.getPrice() <= buyerTargetPrice * 0.85f;
			if (score >= 0.55f || greatPrice) {
				filtered.add(sale);
				if (greatPrice) {
					reasons.put(sale.getSaleNumber(), "Precio muy competitivo respecto a tu historico");
				} else {
					reasons.put(sale.getSaleNumber(), "Alta afinidad segun ranking IA del feed");
				}
			}
		}

		if (limit > 0 && filtered.size() > limit) {
			filtered = new ArrayList<Sale>(filtered.subList(0, limit));
		}
		lastAlertReasons.put(buyerEmail, reasons);
		return filtered;
	}

	@WebMethod
	public synchronized String getOpportunityReason(String buyerEmail, Integer saleNumber) {
		Map<Integer, String> reasonMap = lastAlertReasons.getOrDefault(buyerEmail, Collections.<Integer, String>emptyMap());
		return reasonMap.getOrDefault(saleNumber, "Coincidencia con tus reglas y preferencias");
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
		if (containsAny(text, "bici", "bicicleta", "botas", "futbol", "deporte", "raqueta", "montana")) {
			return "Deporte";
		}
		if (containsAny(text, "camisa", "zapatillas", "chaqueta", "moda", "vestido")) {
			return "Moda";
		}
		if (containsAny(text, "coche", "moto", "motor", "casco", "llanta")) {
			return "Motor";
		}
		if (containsAny(text, "silla", "mesa", "sofa", "lampara", "hogar", "cocina")) {
			return "Hogar";
		}
		if (containsAny(text, "coleccion", "cromo", "figura", "vinilo")) {
			return "Coleccionismo";
		}
		return "General";
	}

	private boolean matchesCategoryKeywords(String category, String title, String description) {
		String inferred = inferCategory(title, description);
		return category != null && category.equalsIgnoreCase(inferred);
	}

	private boolean containsAny(String text, String... words) {
		for (String word : words) {
			if (text.contains(word)) {
				return true;
			}
		}
		return false;
	}

	private Map<String, Integer> getBuyerPreferredCategories(String buyerEmail) {
		Map<String, Integer> preferred = new HashMap<String, Integer>();
		if (buyerEmail == null || buyerEmail.trim().isEmpty()) {
			return preferred;
		}
		List<AcceptedOffer> offers = execute(() -> dbManager.getAcceptedOffersByBuyer(buyerEmail));
		for (AcceptedOffer offer : offers) {
			if (offer == null || offer.getSale() == null) {
				continue;
			}
			String category = resolveCategoryForSale(offer.getSale());
			preferred.put(category, preferred.getOrDefault(category, 0) + 1);
		}
		return preferred;
	}

	private float getBuyerTargetPrice(String buyerEmail, List<Sale> fallbackSales) {
		if (buyerEmail != null && !buyerEmail.trim().isEmpty()) {
			List<AcceptedOffer> offers = execute(() -> dbManager.getAcceptedOffersByBuyer(buyerEmail));
			if (offers != null && !offers.isEmpty()) {
				float sum = 0f;
				int count = 0;
				for (AcceptedOffer offer : offers) {
					if (offer == null || offer.getFinalPrice() == null) {
						continue;
					}
					sum += offer.getFinalPrice();
					count++;
				}
				if (count > 0) {
					return sum / count;
				}
			}
		}
		if (fallbackSales == null || fallbackSales.isEmpty()) {
			return 100f;
		}
		float sum = 0f;
		for (Sale sale : fallbackSales) {
			sum += sale.getPrice();
		}
		return sum / fallbackSales.size();
	}

	private Map<Integer, Integer> getSalesPopularity() {
		Map<Integer, Integer> popularity = new HashMap<Integer, Integer>();
		List<Sale> allSales = execute(() -> dbManager.getSales(""));
		Map<String, List<AcceptedOffer>> offersBySeller = new HashMap<String, List<AcceptedOffer>>();
		for (Sale sale : allSales) {
			if (sale == null || sale.getSeller() == null) {
				continue;
			}
			String sellerEmail = sale.getSeller().getEmail();
			if (!offersBySeller.containsKey(sellerEmail)) {
				offersBySeller.put(sellerEmail, execute(() -> dbManager.getAcceptedOffersBySeller(sellerEmail)));
			}
			popularity.put(sale.getSaleNumber(), 0);
		}
		for (Sale sale : allSales) {
			if (sale == null || sale.getSeller() == null) {
				continue;
			}
			Integer saleNumber = sale.getSaleNumber();
			List<AcceptedOffer> offers = offersBySeller.getOrDefault(sale.getSeller().getEmail(), Collections.<AcceptedOffer>emptyList());
			int count = 0;
			for (AcceptedOffer offer : offers) {
				if (offer.getSale() != null && saleNumber.equals(offer.getSale().getSaleNumber())) {
					count++;
				}
			}
			popularity.put(saleNumber, count);
		}
		return popularity;
	}

	private String resolveCategoryForSale(Sale sale) {
		if (sale == null) {
			return "General";
		}
		String category = sale.getCategory();
		if (category != null && !category.trim().isEmpty()) {
			return category;
		}
		return inferCategory(sale.getTitle(), sale.getDescription());
	}

	private float computeCategoryScore(String category, Map<String, Integer> preferredCategories) {
		if (preferredCategories.isEmpty()) {
			return "General".equalsIgnoreCase(category) ? 0.45f : 0.7f;
		}
		if (preferredCategories.containsKey(category)) {
			int max = Collections.max(preferredCategories.values());
			return 0.5f + 0.5f * (preferredCategories.get(category) / (float) Math.max(1, max));
		}
		return 0.2f;
	}

	private float computePriceScore(float price, float targetPrice) {
		float distance = Math.abs(price - targetPrice);
		return Math.max(0f, 1f - (distance / (targetPrice + 1f)));
	}

	private float computeFreshnessScore(Date publicationDate) {
		if (publicationDate == null) {
			return 0.3f;
		}
		long millisDiff = Math.max(0L, new Date().getTime() - publicationDate.getTime());
		long days = millisDiff / (1000L * 60L * 60L * 24L);
		return Math.max(0.2f, 1f - Math.min(days, 30L) / 30f);
	}

	private String buildReason(float categoryScore, float priceScore, float freshnessScore, String category) {
		if (categoryScore >= 0.8f) {
			return "Muy alineado con tus categorias preferidas: " + category;
		}
		if (priceScore >= 0.75f) {
			return "Precio ajustado a tu rango habitual";
		}
		if (freshnessScore >= 0.8f) {
			return "Publicacion reciente con buena visibilidad";
		}
		return "Buena combinacion general para tu perfil";
	}

	
}

