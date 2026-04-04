package domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class TransaccionPago implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Sale sale;

    @ManyToOne(fetch = FetchType.EAGER)
    private Buyer buyer;

    private float importe;
    private EstadoPago estado;
    private Date fecha;
    private String referenciaPasarela;

    public TransaccionPago() {
    }

    public TransaccionPago(Sale sale, Buyer buyer, float importe) {
        this.sale = sale;
        this.buyer = buyer;
        this.importe = importe;
        this.estado = EstadoPago.PENDIENTE;
        this.fecha = new Date();
    }

    public Integer getId() { return id; }
    public Sale getSale() { return sale; }
    public Buyer getBuyer() { return buyer; }
    public float getImporte() { return importe; }
    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }
    public Date getFecha() { return fecha; }
    public String getReferenciaPasarela() { return referenciaPasarela; }
    public void setReferenciaPasarela(String referenciaPasarela) { this.referenciaPasarela = referenciaPasarela; }
}