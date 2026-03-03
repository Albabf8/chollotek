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
public class CategoriaDAOImpl implements CategoriaDAO {

    private static final Logger logger = Logger.getLogger(CategoriaDAOImpl.class.getName());

    /**
     * Recupera todas las categorías de la base de datos ordenadas
     * alfabéticamente por nombre.
     *
     * * @param con Conexión activa a la base de datos.
     * @return Una lista de objetos {@link Categoria}. Si no hay resultados,
     * devuelve una lista vacía.
     * @throws Exception Si ocurre un error durante la ejecución de la consulta
     * SQL o la conexión.
     */
    @Override
    public List<Categoria> listarTodas(Connection con) throws Exception {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY nombre";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }

        logger.log(Level.INFO, "Categor\u00edas listadas: {0}", lista.size());
        return lista;
    }

    /**
     * Busca una categoría específica en la base de datos mediante su
     * identificador único.
     *
     * * @param id El ID de la categoría a buscar.
     * @param con Conexión activa a la base de datos.
     * @return El objeto {@link Categoria} encontrado, o {@code null} si no
     * existe ninguna categoría con ese ID.
     * @throws Exception Si ocurre un error en la consulta preparada o en la
     * comunicación con la BD.
     */
    @Override
    public Categoria buscarPorId(int id, Connection con) throws Exception {
        String sql = "SELECT * FROM categorias WHERE idcategoria = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
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

    /**
     * Método auxiliar privado que transforma una fila del ResultSet en un
     * objeto de tipo Categoria.
     *
     * * @param rs El ResultSet posicionado en la fila actual a leer.
     * @return Un objeto {@link Categoria} con los datos cargados desde la base
     * de datos.
     * @throws SQLException Si ocurre un error al acceder a las columnas del
     * ResultSet.
     */
    private Categoria mapear(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setIdcategoria(rs.getInt("idcategoria"));
        c.setNombre(rs.getString("nombre"));
        c.setImagen(rs.getString("imagen"));
        return c;
    }
}
