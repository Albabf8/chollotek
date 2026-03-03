package es.chollotek.DAO;

import es.chollotek.beans.Producto;
import java.math.BigDecimal;
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
public class ProductoDAOImpl implements ProductoDAO {

    private static final Logger logger = Logger.getLogger(ProductoDAOImpl.class.getName());

    /**
     * Busca un producto específico por su identificador.
     *
     * * @param id ID único del producto.
     * @param con Conexión activa a la base de datos.
     * @return El objeto {@link Producto} o {@code null} si no existe.
     * @throws Exception Si ocurre un error en la consulta.
     */
    @Override
    public Producto buscarPorId(int id, Connection con) throws Exception {
        String sql = "SELECT * FROM productos WHERE idproducto = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.log(Level.INFO, "Producto encontrado: {0}", id);
                    return mapear(rs);
                }
            }
        }

        logger.log(Level.INFO, "Producto no encontrado: {0}", id);
        return null;
    }

    /**
     * Recupera todos los productos de la base de datos ordenados por nombre.
     *
     * * @param con Conexión activa a la base de datos.
     * @return Lista con todos los productos.
     * @throws Exception Si falla la lectura.
     */
    @Override
    public List<Producto> listarTodos(Connection con) throws Exception {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos ORDER BY nombre";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

            logger.log(Level.INFO, "Productos listados: {0}", lista.size());
        }

        return lista;
    }

    /**
     * Obtiene los productos pertenecientes a una categoría concreta.
     *
     * * @param idCategoria ID de la categoría a filtrar.
     * @param con Conexión activa a la base de datos.
     * @return Lista de productos de dicha categoría.
     * @throws Exception Si ocurre un error SQL.
     */
    @Override
    public List<Producto> listarPorCategoria(int idCategoria, Connection con) throws Exception {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE idcategoria = ? ORDER BY nombre";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }

        logger.log(Level.INFO, "Productos de categor\u00eda {0}: {1}", new Object[]{idCategoria, lista.size()});
        return lista;
    }

    /**
     * Realiza una búsqueda avanzada utilizando múltiples criterios opcionales.
     * Construye la consulta SQL dinámicamente según los parámetros
     * proporcionados.
     *
     * * @param nombre Filtro por nombre (búsqueda parcial con LIKE).
     * @param marca Filtro por marca exacta o parcial.
     * @param precioMin Límite inferior de precio.
     * @param precioMax Límite superior de precio.
     * @param con Conexión activa a la base de datos.
     * @return Lista de productos que cumplen todos los criterios aplicados.
     * @throws Exception Si falla la construcción o ejecución del
     * PreparedStatement.
     */
    @Override
    public List<Producto> buscarPorFiltros(String nombre, String marca,
            BigDecimal precioMin, BigDecimal precioMax,
            Connection con) throws Exception {
        List<Producto> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM productos WHERE 1=1");

        // Construcción dinámica del SQL según filtros presentes
        if (nombre != null && !nombre.trim().isEmpty()) {
            sql.append(" AND nombre LIKE ?");
        }
        if (marca != null && !marca.trim().isEmpty()) {
            sql.append(" AND marca LIKE ?");
        }
        if (precioMin != null) {
            sql.append(" AND precio >= ?");
        }
        if (precioMax != null) {
            sql.append(" AND precio <= ?");
        }

        sql.append(" ORDER BY nombre");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            // Asignación dinámica de parámetros en el mismo orden que los WHERE
            if (nombre != null && !nombre.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + nombre.trim() + "%");
            }
            if (marca != null && !marca.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + marca.trim() + "%");
            }
            if (precioMin != null) {
                ps.setBigDecimal(paramIndex++, precioMin);
            }
            if (precioMax != null) {
                ps.setBigDecimal(paramIndex, precioMax);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }

        logger.log(Level.INFO, "B\u00fasqueda con filtros: {0} resultados", lista.size());
        return lista;
    }

    /**
     * Obtiene una selección aleatoria de productos para mostrar, por ejemplo,
     * en la portada.
     *
     * * @param cantidad Número de productos a recuperar.
     * @param con Conexión activa a la base de datos.
     * @return Lista de productos aleatorios.
     * @throws Exception Si falla la función RAND() de SQL.
     */
    @Override
    public List<Producto> obtenerProductosAleatorios(int cantidad, Connection con) throws Exception {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos ORDER BY RAND() LIMIT ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cantidad);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }

        logger.log(Level.INFO, "Productos aleatorios obtenidos: {0}", lista.size());
        return lista;
    }

    /**
     * Recupera una lista única de todas las marcas presentes en el catálogo.
     *
     * * @param con Conexión activa a la base de datos.
     * @return Lista de Strings con los nombres de las marcas sin duplicados.
     * @throws Exception Si ocurre un error SQL.
     */
    @Override
    public List<String> obtenerMarcas(Connection con) throws Exception {
        List<String> marcas = new ArrayList<>();
        String sql = "SELECT DISTINCT marca FROM productos WHERE marca IS NOT NULL ORDER BY marca";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                marcas.add(rs.getString("marca"));
            }
        }

        logger.log(Level.INFO, "Marcas \u00fanicas obtenidas: {0}", marcas.size());
        return marcas;
    }

    /**
     * Calcula el precio más alto de entre todos los productos. Útil para
     * configurar rangos en filtros de búsqueda.
     *
     * * @param con Conexión activa a la base de datos.
     * @return El valor máximo encontrado o {@link BigDecimal#ZERO} si no hay
     * productos.
     * @throws Exception Si falla la función agregada MAX.
     */
    @Override
    public BigDecimal obtenerPrecioMaximo(Connection con) throws Exception {
        String sql = "SELECT MAX(precio) AS precio_max FROM productos";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                BigDecimal max = rs.getBigDecimal("precio_max");
                return (max != null) ? max : BigDecimal.ZERO;
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * Método privado para transformar un registro de la base de datos en un
     * objeto Producto. Incluye lógica para asignar una imagen por defecto si el
     * campo está vacío en la BD.
     *
     * * @param rs El ResultSet en la posición actual.
     * @return Objeto {@link Producto} mapeado.
     * @throws SQLException Si ocurre un error al leer las columnas.
     */
    private Producto mapear(ResultSet rs) throws SQLException {
        Producto producto = new Producto();

        producto.setIdproducto(rs.getInt("idproducto"));
        producto.setIdcategoria(rs.getInt("idcategoria"));
        producto.setNombre(rs.getString("nombre"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setPrecio(rs.getBigDecimal("precio"));
        producto.setMarca(rs.getString("marca"));

        // Gestión de imagen por defecto
        String imagen = rs.getString("imagen");
        if (imagen == null || imagen.trim().isEmpty()) {
            producto.setImagen("default.jpg");
        } else {
            producto.setImagen(imagen);
        }

        return producto;
    }

    /**
     * Calcula el precio más bajo de entre todos los productos.
     *
     * * @param con Conexión activa a la base de datos.
     * @return El valor mínimo encontrado o {@link BigDecimal#ZERO}.
     * @throws Exception Si falla la función agregada MIN.
     */
    @Override
    public BigDecimal obtenerPrecioMinimo(Connection con) throws Exception {
        String sql = "SELECT MIN(precio) AS precio_min FROM productos";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                BigDecimal min = rs.getBigDecimal("precio_min");
                return (min != null) ? min : BigDecimal.ZERO;
            }
        }

        return BigDecimal.ZERO;
    }
}
