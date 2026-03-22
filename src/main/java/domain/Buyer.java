package domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

/**
 * Represents a buyer user in the marketplace system.
 * Extends User with buyer-specific attributes and accepted offers.
 */
@Entity
public class Buyer extends User {
    private static final long serialVersionUID = 1L;
    
    private String shippingAddress;
    
    @OneToMany(mappedBy="buyer", fetch=FetchType.EAGER, 
               cascade=CascadeType.ALL)
    private List<AcceptedOffer> acceptedOffers = new ArrayList<>();
    
    /**
     * Default constructor for JPA.
     */
    public Buyer() { super(); }
    
    /**
     * Creates a new buyer.
     * 
     * @param email the buyer's email address
     * @param name the buyer's name
     * @param password the buyer's password
     * @param shippingAddress the buyer's shipping address
     */
    public Buyer(String email, String name, String password, 
                 String shippingAddress) {
        super(email, name, password);
        this.shippingAddress = shippingAddress;
    }
    
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { 
        this.shippingAddress = shippingAddress; 
    }
    
    public List<AcceptedOffer> getAcceptedOffers() { return acceptedOffers; }
    public void setAcceptedOffers(List<AcceptedOffer> acceptedOffers) { 
        this.acceptedOffers = acceptedOffers; 
    }
    
    /**
     * Adds an accepted offer to this buyer's list.
     * 
     * @param offer the accepted offer to add
     */
    public void addAcceptedOffer(AcceptedOffer offer) {
        acceptedOffers.add(offer);
        offer.setBuyer(this);
    }
}