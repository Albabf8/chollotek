package es.chollotek.beans;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Alba
 */
public class Producto implements Serializable {

    private int idproducto;
    private int idcategoria;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String marca;
    private String imagen;

    public Producto() {
    }

    // Getters y Setters
    public int getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(int idproducto) {
        this.idproducto = idproducto;
    }

    public int getIdcategoria() {
        return idcategoria;
    }

    public void setIdcategoria(int idcategoria) {
        this.idcategoria = idcategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
   
    @Override
    public String toString() {
        return "Producto{idproducto=" + idproducto + ", nombre='" + nombre + "', precio=" + precio + "}";
    }
}
