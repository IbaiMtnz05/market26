package domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class TransaccionPago implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum EstadoPago { PENDIENTE, CONFIRMADO, FALLIDO, REEMBOLSADO }

    @Id
    @GeneratedValue
    private Integer id;

    private Integer saleNumber;
    private String buyerEmail;
    private float importe;
    private EstadoPago estado;
    private Date fechaPago;
    private String referenciaExterna;
    private String detalleError;

    public TransaccionPago() {
    }

    public TransaccionPago(Integer saleNumber, String buyerEmail, float importe) {
        this.saleNumber = saleNumber;
        this.buyerEmail = buyerEmail;
        this.importe = importe;
        this.estado = EstadoPago.PENDIENTE;
        this.fechaPago = new Date();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getSaleNumber() { return saleNumber; }
    public void setSaleNumber(Integer saleNumber) { this.saleNumber = saleNumber; }

    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }

    public float getImporte() { return importe; }
    public void setImporte(float importe) { this.importe = importe; }

    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }

    public Date getFechaPago() { return fechaPago; }
    public void setFechaPago(Date fechaPago) { this.fechaPago = fechaPago; }

    public String getReferenciaExterna() { return referenciaExterna; }
    public void setReferenciaExterna(String referenciaExterna) { this.referenciaExterna = referenciaExterna; }

    public String getDetalleError() { return detalleError; }
    public void setDetalleError(String detalleError) { this.detalleError = detalleError; }
}