package es.chollotek.DAO;

import es.chollotek.beans.Categoria;
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
public class CategoriaDAOImpl implements CategoriaDAO{

    @Override
    public List<Categoria> getCategorias() {
        List<Categoria> listaCategorias = new ArrayList<>();

        String sql = "SELECT idcategoria, nombre, imagen FROM categorias ORDER BY nombre";

        Connection conexion = null;
        PreparedStatement preparada = null;
        ResultSet resultado = null;

        try {
            conexion = ConnectionFactory.getConnection();
            preparada = conexion.prepareStatement(sql);
            resultado = preparada.executeQuery();

            while (resultado.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdcategoria(resultado.getInt("idcategoria"));
                categoria.setNombre(resultado.getString("nombre"));

                String img = resultado.getString("imagen");
                // Validamos imagen por si viene nula o vacía
                if (img == null || img.trim().isEmpty()) {
                    categoria.setImagen("default.jpg");
                } else {
                    categoria.setImagen(img);
                }

                listaCategorias.add(categoria);
            }

        } catch (SQLException e) {
            Logger.getLogger(CategoriaDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            this.closeConnection();
        }

        return listaCategorias;

    }

    @Override
    public void closeConnection() {
        ConnectionFactory.closeConexion();
    }
}
    
