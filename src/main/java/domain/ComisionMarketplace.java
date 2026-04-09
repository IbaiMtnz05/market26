package domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ComisionMarketplace implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Integer id;

    private Integer transaccionPagoId;
    private String sellerEmail;
    private float importeVenta;
    private float porcentajeComision;
    private float importeComision;
    private float importeNeto;
    private Date fechaCalculo;
    private String estado;

    public ComisionMarketplace() {
    }

    public ComisionMarketplace(Integer transaccionPagoId, String sellerEmail,
            float importeVenta, float porcentajeComision) {
        this.transaccionPagoId = transaccionPagoId;
        this.sellerEmail = sellerEmail;
        this.importeVenta = importeVenta;
        this.porcentajeComision = porcentajeComision;
        this.importeComision = importeVenta * (porcentajeComision / 100.0f);
        this.importeNeto = importeVenta - this.importeComision;
        this.fechaCalculo = new Date();
        this.estado = "ComisionMarketplace.Estado.CALCULADA";
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTransaccionPagoId() { return transaccionPagoId; }
    public void setTransaccionPagoId(Integer transaccionPagoId) { this.transaccionPagoId = transaccionPagoId; }

    public String getSellerEmail() { return sellerEmail; }
    public void setSellerEmail(String sellerEmail) { this.sellerEmail = sellerEmail; }

    public float getImporteVenta() { return importeVenta; }
    public void setImporteVenta(float importeVenta) { this.importeVenta = importeVenta; }

    public float getPorcentajeComision() { return porcentajeComision; }
    public void setPorcentajeComision(float porcentajeComision) { this.porcentajeComision = porcentajeComision; }

    public float getImporteComision() { return importeComision; }
    public void setImporteComision(float importeComision) { this.importeComision = importeComision; }

    public float getImporteNeto() { return importeNeto; }
    public void setImporteNeto(float importeNeto) { this.importeNeto = importeNeto; }

    public Date getFechaCalculo() { return fechaCalculo; }
    public void setFechaCalculo(Date fechaCalculo) { this.fechaCalculo = fechaCalculo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}