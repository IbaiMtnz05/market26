package domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class DecisionVenta implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Sale sale;

    @ManyToOne(fetch = FetchType.EAGER)
    private AcceptedOffer acceptedOffer;

    private String sellerEmail;
    private String criterio;
    private String motivo;
    private Date fechaDecision;
    private Integer saleNumber;

    public DecisionVenta() {
    }

    public DecisionVenta(Sale sale, AcceptedOffer acceptedOffer, String sellerEmail, String criterio, String motivo) {
        this.sale = sale;
        this.acceptedOffer = acceptedOffer;
        this.sellerEmail = sellerEmail;
        this.criterio = criterio;
        this.motivo = motivo;
        this.fechaDecision = new Date();
        this.saleNumber = sale != null ? sale.getSaleNumber() : null;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }

    public AcceptedOffer getAcceptedOffer() { return acceptedOffer; }
    public void setAcceptedOffer(AcceptedOffer acceptedOffer) { this.acceptedOffer = acceptedOffer; }

    public String getSellerEmail() { return sellerEmail; }
    public void setSellerEmail(String sellerEmail) { this.sellerEmail = sellerEmail; }

    public String getCriterio() { return criterio; }
    public void setCriterio(String criterio) { this.criterio = criterio; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Date getFechaDecision() { return fechaDecision; }
    public void setFechaDecision(Date fechaDecision) { this.fechaDecision = fechaDecision; }

    public Integer getSaleNumber() { return saleNumber; }
    public void setSaleNumber(Integer saleNumber) { this.saleNumber = saleNumber; }
}