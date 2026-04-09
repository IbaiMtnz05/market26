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
    private String vendedorEmail;      
    private String observaciones;      
    private Date fechaResolucion;      
    private String estadoResolucion;   

    public Reembolso() {
    }

    public Reembolso(Integer transaccionPagoId, String buyerEmail, String vendedorEmail,
            TipoReembolso tipo, float importe, String motivo, String observaciones) {
    		this.transaccionPagoId = transaccionPagoId;
    		this.buyerEmail = buyerEmail;
    		this.vendedorEmail = vendedorEmail;
    		this.tipo = tipo;
    		this.importe = importe;
    		this.motivo = motivo;
    		this.observaciones = observaciones;
    		this.fechaSolicitud = new Date();
    		this.estado = "PENDIENTE";
    		this.estadoResolucion = "PENDIENTE";
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
    
    public String getVendedorEmail() { return vendedorEmail; }
    public void setVendedorEmail(String vendedorEmail) { this.vendedorEmail = vendedorEmail; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public Date getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(Date fechaResolucion) { this.fechaResolucion = fechaResolucion; }
    
    public String getEstadoResolucion() { return estadoResolucion; }
    public void setEstadoResolucion(String estadoResolucion) { this.estadoResolucion = estadoResolucion;} 
}