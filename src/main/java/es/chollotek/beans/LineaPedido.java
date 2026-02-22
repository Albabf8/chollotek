package es.chollotek.beans;

import java.io.Serializable;

/**
 *
 * @author Alba
 */
public class LineaPedido implements Serializable {

    private short idlinea;
    private short idpedido;
    private short idproducto;
    private short cantidad;

    public LineaPedido() {
    }

//    public LineaPedido(Producto producto, int cantidad) {
//        this.producto = producto;
//        this.cantidad = cantidad;
//    }

    //Getters y Setters
    public short getIdlinea() {
        return idlinea;
    }

    public void setIdlinea(short idlinea) {
        this.idlinea = idlinea;
    }

    public short getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(short idpedido) {
        this.idpedido = idpedido;
    }

    public short getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(short idproducto) {
        this.idproducto = idproducto;
    }

    public short getCantidad() {
        return cantidad;
    }

    public void setCantidad(short cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "LineaPedido{" + "idlinea=" + idlinea + ", idpedido=" + idpedido + ", producto=" + idproducto + ", cantidad=" + cantidad + '}';
    }

//    // Calcula el precio total (Precio por Cantidad)
//    public BigDecimal getSubtotal() {
//        if (producto != null && producto.getPrecio() != null) {
//            // Multiplicamos el BigDecimal por la cantidad
//            return producto.getPrecio().multiply(new BigDecimal(this.cantidad));
//        }
//        return BigDecimal.ZERO;
//    }
}
