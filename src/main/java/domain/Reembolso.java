package domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Reembolso implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    private TransaccionPago transaccionPago;

    private float importe;
    private TipoReembolso tipo;
    private String motivo;
    private EstadoReembolso estado;
    private Date fechaSolicitud;
    private Date fechaResolucion;

    public Reembolso() {
    }

    public Reembolso(TransaccionPago transaccionPago, float importe, TipoReembolso tipo, String motivo) {
        this.transaccionPago = transaccionPago;
        this.importe = importe;
        this.tipo = tipo;
        this.motivo = motivo;
        this.estado = EstadoReembolso.PENDIENTE;
        this.fechaSolicitud = new Date();
    }

    public Integer getId() { return id; }
    public TransaccionPago getTransaccionPago() { return transaccionPago; }
    public float getImporte() { return importe; }
    public TipoReembolso getTipo() { return tipo; }
    public String getMotivo() { return motivo; }
    public EstadoReembolso getEstado() { return estado; }
    public void setEstado(EstadoReembolso estado) { this.estado = estado; }
    public Date getFechaSolicitud() { return fechaSolicitud; }
    public Date getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(Date fechaResolucion) { this.fechaResolucion = fechaResolucion; }
}