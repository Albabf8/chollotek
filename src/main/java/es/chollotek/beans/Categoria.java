package es.chollotek.beans;

import java.io.Serializable;

/**
 *
 * @author Alba
 */
public class Categoria implements Serializable {

    private int idcategoria;
    private String nombre;
    private String imagen;

    public Categoria() {
    }

    //Getters y Setters
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "Categoria{idcategoria=" + idcategoria + ", nombre='" + nombre + "'}";
    }
}
