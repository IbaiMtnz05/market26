package domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ComisionMarketplace implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    private TransaccionPago transaccionPago;

    @ManyToOne(fetch = FetchType.EAGER)
    private Seller seller;

    private float porcentaje;
    private float importeComision;
    private float importeNeto;
    private Date fecha;

    public ComisionMarketplace() {
    }

    public ComisionMarketplace(TransaccionPago transaccionPago, Seller seller, float porcentaje) {
        this.transaccionPago = transaccionPago;
        this.seller = seller;
        this.porcentaje = porcentaje;
        this.importeComision = transaccionPago.getImporte() * porcentaje;
        this.importeNeto = transaccionPago.getImporte() - this.importeComision;
        this.fecha = new Date();
    }

    public Integer getId() { return id; }
    public TransaccionPago getTransaccionPago() { return transaccionPago; }
    public Seller getSeller() { return seller; }
    public float getPorcentaje() { return porcentaje; }
    public float getImporteComision() { return importeComision; }
    public float getImporteNeto() { return importeNeto; }
    public Date getFecha() { return fecha; }
}