package es.chollotek.DAO;

import es.chollotek.beans.Producto;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Alba
 */
public interface ProductoDAO {
    
    public List<Producto> getProductosLanding();

    public Producto getProductoById(int id);
    
    public List<Producto> buscarProductos(String texto);

    public List<String> getMarcas();
 
    public BigDecimal getPrecioMaximo();

    //public List<Producto> getProductosFiltrados(String idCategoria, String marca, double precioTope);

    public void closeConnection();

}
