package es.chollotek.DAO;

import es.chollotek.beans.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alba
 */
public class CategoriaDAOImpl implements CategoriaDAO{

    private static final Logger logger = Logger.getLogger(CategoriaDAOImpl.class.getName());

    @Override
    public List<Categoria> listarTodas(Connection con) throws Exception {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY nombre";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        
        logger.log(Level.INFO, "Categor\u00edas listadas: {0}", lista.size());
        return lista;
    }

    @Override
    public Categoria buscarPorId(byte id, Connection con) throws Exception {
        String sql = "SELECT * FROM categorias WHERE idcategoria = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setByte(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.log(Level.INFO, "Categor\u00eda encontrada: {0}", id);
                    return mapear(rs);
                }
            }
        }
        
        logger.log(Level.INFO, "Categor\u00eda no encontrada: {0}", id);
        return null;
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setIdcategoria(rs.getByte("idcategoria"));
        c.setNombre(rs.getString("nombre"));
        c.setImagen(rs.getString("imagen"));
        return c;
    }
}
    
