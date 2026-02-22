package es.chollotek.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

/**
 *
 * @author Alba
 */
public class Pedido implements Serializable {

    private int idpedido;
    private Date fecha;
    private char estado; // 'c' = carrito, 'f' = finalizado
    private int idusuario;
    private BigDecimal importe;
    private BigDecimal iva;

    public Pedido() {
    }

    // Getters y Setters
    public int getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(int idpedido) {
        this.idpedido = idpedido;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public char getEstado() {
        return estado;
    }

    public void setEstado(char estado) {
        this.estado = estado;
    }

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    @Override
    public String toString() {
        return "Pedido{idpedido=" + idpedido + ", estado=" + estado + ", importe=" + importe + "}";
    }
}
