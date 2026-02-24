package es.chollotek.beans;

import java.io.Serializable;

/**
 *
 * @author Alba
 */
public class LineaPedido implements Serializable {

    private int idlinea;
    private int idpedido;
    private int idproducto;
    private int cantidad;

    public LineaPedido() {
    }

//    public LineaPedido(Producto producto, int cantidad) {
//        this.producto = producto;
//        this.cantidad = cantidad;
//    }

    //Getters y Setters
    public int getIdlinea() {
        return idlinea;
    }

    public void setIdlinea(int idlinea) {
        this.idlinea = idlinea;
    }

    public int getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(int idpedido) {
        this.idpedido = idpedido;
    }

    public int getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(int idproducto) {
        this.idproducto = idproducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
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
