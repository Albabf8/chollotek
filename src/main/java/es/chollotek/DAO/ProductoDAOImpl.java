package es.chollotek.DAO;

import es.chollotek.beans.Producto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alba
 */
public class ProductoDAOImpl implements ProductoDAO {

    @Override
    public List<Producto> getProductosLanding() {
        List<Producto> listaProductos = new ArrayList<>();
        String sql = "SELECT * FROM productos ORDER BY RAND() LIMIT 8";

        Connection conexion = null;
        PreparedStatement preparada = null;
        ResultSet resultado = null;

        try {
            conexion = ConnectionFactory.getConnection();
            preparada = conexion.prepareStatement(sql);
            resultado = preparada.executeQuery();

            while (resultado.next()) {
                Producto producto = new Producto();

                producto.setIdproducto(resultado.getShort("idproducto"));
                producto.setIdcategoria(resultado.getByte("idcategoria"));
                producto.setNombre(resultado.getString("nombre"));
                producto.setDescripcion(resultado.getString("descripcion"));
                producto.setPrecio(resultado.getBigDecimal("precio"));
                producto.setMarca(resultado.getString("marca"));

                String img = resultado.getString("imagen");
                if (img == null || img.trim().isEmpty()) {
                    producto.setImagen("default.jpg");
                } else {
                    producto.setImagen(img);

                }

                listaProductos.add(producto);
            }
        } catch (SQLException e) {
            Logger.getLogger(ProductoDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            this.closeConnection();
        }

        return listaProductos;

    }
    
     @Override
    public Producto getProductoById(int id){
        Producto producto = null;
        
        String sql = "SELECT * FROM productos WHERE idproducto = ?";
        
        Connection conexion = null;
        PreparedStatement preparada = null;
        ResultSet resultado = null;
     
        try{
            conexion = ConnectionFactory.getConnection();
            preparada = conexion.prepareStatement(sql);
            preparada.setInt(1, id);
            resultado = preparada.executeQuery();
            
            if (resultado.next()) {
                producto = new Producto();
                producto.setIdproducto(resultado.getShort("idproducto"));
                producto.setIdcategoria(resultado.getByte("idcategoria"));
                producto.setNombre(resultado.getString("nombre"));
                producto.setDescripcion(resultado.getString("descripcion"));
                producto.setPrecio(resultado.getBigDecimal("precio"));
                producto.setMarca(resultado.getString("marca"));
                producto.setImagen(resultado.getString("imagen"));
                
                String img = resultado.getString("imagen");
                if (img == null || img.trim().isEmpty()) {
                    producto.setImagen("default.jpg");
                } else {
                    producto.setImagen(img);
                }
            }
        }catch (SQLException e){
           Logger.getLogger(ProductoDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }finally {
             this.closeConnection();
        }
        
        return producto;
    }
    
     @Override
    public List<Producto> buscarProductos(String texto){
        texto = texto.trim();
        List<Producto> listaBuscarProductos = new ArrayList<>();
        // Buscamos por nombre
        String sql = "SELECT * FROM productos WHERE nombre LIKE ?";
        
        Connection conexion = null;
        PreparedStatement preparada = null;
        ResultSet resultado = null;
        
        try {
            conexion = ConnectionFactory.getConnection();
            preparada = conexion.prepareStatement(sql);
            // Con los porcentajes % busca cualquier nombre que contenga esta palabra en cualquier posición
            preparada.setString(1, "%" + texto + "%");

            resultado = preparada.executeQuery();
            
            while (resultado.next()){
                Producto producto = new Producto();
                producto.setIdproducto(resultado.getShort("idproducto"));
                producto.setIdcategoria(resultado.getByte("idcategoria"));
                producto.setNombre(resultado.getString("nombre"));
                producto.setDescripcion(resultado.getString("descripcion"));
                producto.setPrecio(resultado.getBigDecimal("precio"));
                producto.setMarca(resultado.getString("marca"));
                
                String img = resultado.getString("imagen");
                if (img == null || img.trim().isEmpty()) {
                    producto.setImagen("default.jpg");
                } else {
                    producto.setImagen(img);
                }
                
                listaBuscarProductos.add(producto);
            }
            
            
        }catch (SQLException e){
            Logger.getLogger(ProductoDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }finally {
             this.closeConnection();
        }
        
        return listaBuscarProductos;
    }
    
    @Override
    public List<String> getMarcas(){
        List<String> listaMarcas = new ArrayList<>();
        
        String sql = "SELECT marca FROM productos ORDER BY marca";
        
        Connection conexion = null;
        PreparedStatement preparada = null;
        ResultSet resultado = null;
        
        try {
            
            conexion = ConnectionFactory.getConnection();
            preparada = conexion.prepareStatement(sql);
            resultado = preparada.executeQuery();
            
            while (resultado.next()) {
                listaMarcas.add(resultado.getString("marca"));
            }
            
            
        }catch (SQLException e){
            Logger.getLogger(ProductoDAOImpl.class.getName()).log(Level.SEVERE, null, e);
            
        }finally {
            this.closeConnection();
        }
        
        return listaMarcas;
    }
    
    @Override
public BigDecimal getPrecioMaximo() {
    BigDecimal max = new BigDecimal("1000.00"); // Valor por defecto
    String sql = "SELECT MAX(precio) FROM productos";

    Connection conexion = null;
    PreparedStatement preparada = null;
    ResultSet resultado = null;

    try {
        conexion = ConnectionFactory.getConnection();
        preparada = conexion.prepareStatement(sql);
        resultado = preparada.executeQuery();

        if (resultado.next()) {
            // Usamos getBigDecimal para máxima precisión
            BigDecimal res = resultado.getBigDecimal(1);
            if (res != null) max = res;
        }
    } catch (SQLException e) {
        Logger.getLogger(ProductoDAOImpl.class.getName()).log(Level.SEVERE, null, e);
    } finally {
        this.closeConnection();
    }
    return max;
}
    
    

    @Override
    public void closeConnection() {
        ConnectionFactory.closeConexion();
    }

}
