package domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Represents an accepted offer in the marketplace system.
 * Links a buyer to a specific sale with optional price negotiation.
 */
@Entity
public class AcceptedOffer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    private Integer id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "buyer_email", nullable = false)
    private Buyer buyer;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sale_number", nullable = false)
    private Sale sale;
    
    private Date acceptanceDate;

    private Float negotiatedPrice;
    
    /**
     * Default constructor for JPA.
     */
    public AcceptedOffer() { }
    
    /**
     * Creates a new accepted offer.
     * 
     * @param buyer the buyer who accepted the offer
     * @param sale the sale being accepted
     * @param negotiatedPrice the negotiated price, or null to use the original sale price
     */
    public AcceptedOffer(Buyer buyer, Sale sale, Float negotiatedPrice) {
        this.buyer = buyer;
        this.sale = sale;
        this.negotiatedPrice = negotiatedPrice;
        this.acceptanceDate = new Date();
    }
    
    // Getters y setters
    public Integer getId() { return id; }
    
    public Buyer getBuyer() { return buyer; }
    public void setBuyer(Buyer buyer) { this.buyer = buyer; }
    
    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }
    
    public Date getAcceptanceDate() { return acceptanceDate; }
    public void setAcceptanceDate(Date date) { 
        this.acceptanceDate = date; 
    }

    public Float getNegotiatedPrice() { return negotiatedPrice; }
    public void setNegotiatedPrice(Float price) { this.negotiatedPrice = price; }
    
    /**
     * Gets the final price for this accepted offer.
     * Returns the negotiated price if set, otherwise the original sale price.
     * 
     * @return the final price
     */
    public Float getFinalPrice() {
        return negotiatedPrice != null ? negotiatedPrice : sale.getPrice();
    }
}