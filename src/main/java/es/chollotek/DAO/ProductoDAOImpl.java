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

    @Override
    public Producto buscarPorId(short id, Connection con) throws Exception {
        String sql = "SELECT * FROM productos WHERE idproducto = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setShort(1, id);
            
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

    @Override
    public List<Producto> listarTodos(Connection con) throws Exception {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos ORDER BY nombre";
        
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            
            logger.log(Level.INFO, "Productos listados: {0}", lista.size());
        }
        
        return lista;
    }

    @Override
    public List<Producto> listarPorCategoria(byte idCategoria, Connection con) throws Exception {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE idcategoria = ? ORDER BY nombre";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setByte(1, idCategoria);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        
        logger.log(Level.INFO, "Productos de categor\u00eda {0}: {1}", new Object[]{idCategoria, lista.size()});
        return lista;
    }

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

    @Override
    public List<String> obtenerMarcas(Connection con) throws Exception {
        List<String> marcas = new ArrayList<>();
        String sql = "SELECT DISTINCT marca FROM productos WHERE marca IS NOT NULL ORDER BY marca";
        
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                marcas.add(rs.getString("marca"));
            }
        }
        
        logger.log(Level.INFO, "Marcas \u00fanicas obtenidas: {0}", marcas.size());
        return marcas;
    }

    @Override
    public BigDecimal obtenerPrecioMaximo(Connection con) throws Exception {
        String sql = "SELECT MAX(precio) AS precio_max FROM productos";
        
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            if (rs.next()) {
                BigDecimal max = rs.getBigDecimal("precio_max");
                return (max != null) ? max : BigDecimal.ZERO;
            }
        }
        
        return BigDecimal.ZERO;
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        
        producto.setIdproducto(rs.getShort("idproducto"));
        producto.setIdcategoria(rs.getByte("idcategoria"));
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

}
