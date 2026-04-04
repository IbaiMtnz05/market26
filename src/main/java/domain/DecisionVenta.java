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
    private Buyer buyerElegido;

    @ManyToOne(fetch = FetchType.EAGER)
    private Seller seller;

    private CriterioDecision criterio;
    private String motivo;
    private Date fechaDecision;

    public DecisionVenta() {
    }

    public DecisionVenta(Sale sale, Buyer buyerElegido, Seller seller, CriterioDecision criterio, String motivo) {
        this.sale = sale;
        this.buyerElegido = buyerElegido;
        this.seller = seller;
        this.criterio = criterio;
        this.motivo = motivo;
        this.fechaDecision = new Date();
    }

    public Integer getId() { return id; }
    public Sale getSale() { return sale; }
    public Buyer getBuyerElegido() { return buyerElegido; }
    public Seller getSeller() { return seller; }
    public CriterioDecision getCriterio() { return criterio; }
    public String getMotivo() { return motivo; }
    public Date getFechaDecision() { return fechaDecision; }
}