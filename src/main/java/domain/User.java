package domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Base class representing a user in the marketplace system.
 * Can be extended by Buyer or Seller with specific attributes.
 */
@Entity
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    private String email;
    private String name;
    private String password;
    
    /**
     * Default constructor for JPA.
     */
    public User() { super(); }
    
    /**
     * Creates a new user.
     * 
     * @param email the user's email address (used as identifier)
     * @param name the user's name
     * @param password the user's password
     */
    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    @Override
    public String toString() {
        return "User{email='" + email + "', name='" + name + "'}";
    }
}