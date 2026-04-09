package domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Reembolso implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TipoReembolso { TOTAL, PARCIAL }

    @Id
    @GeneratedValue
    private Integer id;

    private Integer transaccionPagoId;
    private String buyerEmail;
    private TipoReembolso tipo;
    private float importe;
    private String motivo;
    private String estado;
    private String detalleEstado;
    private Date fechaSolicitud;

    public Reembolso() {
    }

    public Reembolso(Integer transaccionPagoId, String buyerEmail, TipoReembolso tipo, float importe, String motivo) {
        this.transaccionPagoId = transaccionPagoId;
        this.buyerEmail = buyerEmail;
        this.tipo = tipo;
        this.importe = importe;
        this.motivo = motivo;
        this.fechaSolicitud = new Date();
        this.estado = "EstadoReembolso.PENDIENTE";
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTransaccionPagoId() { return transaccionPagoId; }
    public void setTransaccionPagoId(Integer transaccionPagoId) { this.transaccionPagoId = transaccionPagoId; }

    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }

    public TipoReembolso getTipo() { return tipo; }
    public void setTipo(TipoReembolso tipo) { this.tipo = tipo; }

    public float getImporte() { return importe; }
    public void setImporte(float importe) { this.importe = importe; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Date getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Date fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getDetalleEstado() { return detalleEstado; }
    public void setDetalleEstado(String detalleEstado) { this.detalleEstado = detalleEstado; }
}