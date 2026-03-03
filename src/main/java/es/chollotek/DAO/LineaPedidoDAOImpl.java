package es.chollotek.DAO;

import es.chollotek.beans.LineaPedido;
import es.chollotek.beans.Producto;
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
public class LineaPedidoDAOImpl implements LineaPedidoDAO {

    private static final Logger logger = Logger.getLogger(LineaPedidoDAOImpl.class.getName());

    /**
     * Obtiene todas las líneas de un pedido específico, incluyendo los detalles
     * del producto asociado. Realiza un JOIN entre 'lineaspedidos' y
     * 'productos'.
     *
     * * @param idPedido Identificador del pedido del cual se quieren recuperar
     * las líneas.
     * @param con Conexión activa a la base de datos.
     * @return Lista de objetos {@link LineaPedido} con su objeto
     * {@link Producto} interno cargado.
     * @throws Exception Si ocurre un error en la consulta SQL.
     */
    @Override
    public List<LineaPedido> listarPorPedido(int idPedido, Connection con) throws Exception {
        List<LineaPedido> lista = new ArrayList<>();
        String sql = "SELECT lp.*, p.nombre, p.marca, p.precio, p.imagen "
                + "FROM lineaspedidos lp "
                + "JOIN productos p ON lp.idproducto = p.idproducto "
                + "WHERE lp.idpedido = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearConProducto(rs));
                }
            }
        }

        logger.log(Level.INFO, "L\u00edneas del pedido {0}: {1}", new Object[]{idPedido, lista.size()});
        return lista;
    }

    /**
     * Busca una línea de pedido específica filtrando por pedido y producto.
     *
     * * @param idPedido ID del pedido.
     * @param idProducto ID del producto.
     * @param con Conexión activa a la base de datos.
     * @return Objeto {@link LineaPedido} encontrado o {@code null} si no
     * existe.
     * @throws Exception Si hay errores de comunicación con la BD.
     */
    @Override
    public LineaPedido buscarLinea(int idPedido, int idProducto, Connection con) throws Exception {
        String sql = "SELECT * FROM lineaspedidos WHERE idpedido = ? AND idproducto = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.log(Level.INFO, "L\u00ednea encontrada: pedido {0}, producto {1}", new Object[]{idPedido, idProducto});
                    return mapear(rs);
                }
            }
        }

        logger.log(Level.INFO, "L\u00ednea no encontrada: pedido {0}, producto {1}", new Object[]{idPedido, idProducto});
        return null;
    }

    /**
     * Inserta una nueva línea de pedido en la base de datos.
     *
     * * @param linea Objeto {@link LineaPedido} con los datos a insertar.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si la inserción falla o no afecta a ninguna fila.
     * @throws Exception Para otros errores generales.
     */
    @Override
    public void insertar(LineaPedido linea, Connection con) throws Exception {
        String sql = "INSERT INTO lineaspedidos (idpedido, idproducto, cantidad) "
                + "VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, linea.getIdpedido());
            ps.setInt(2, linea.getIdproducto());
            ps.setInt(3, linea.getCantidad());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                throw new SQLException("Error al insertar línea de pedido.");
            }

            logger.log(Level.INFO, "L\u00ednea insertada: pedido {0}, producto {1}, cantidad {2}", new Object[]{linea.getIdpedido(), linea.getIdproducto(), linea.getCantidad()});
        }
    }

    /**
     * Actualiza la cantidad de unidades de una línea de pedido existente.
     *
     * * @param idLinea Identificador único de la línea de pedido.
     * @param cantidad Nueva cantidad a asignar.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si el ID no existe o no se pudo actualizar.
     */
    @Override
    public void actualizarCantidad(int idLinea, int cantidad, Connection con) throws Exception {
        String sql = "UPDATE lineaspedidos SET cantidad = ? WHERE idlinea = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idLinea);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                logger.log(Level.WARNING, "No se encontr\u00f3 l\u00ednea con ID: {0}", idLinea);
                throw new SQLException("No se pudo actualizar: línea no encontrada.");
            }

            logger.log(Level.INFO, "Cantidad actualizada: l\u00ednea {0} \u2192 {1} uds", new Object[]{idLinea, cantidad});
        }
    }

    /**
     * Elimina una línea de pedido mediante su identificador.
     *
     * * @param idLinea ID de la línea a borrar.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si el ID no existe o la eliminación falla.
     */
    @Override
    public void eliminar(int idLinea, Connection con) throws Exception {
        String sql = "DELETE FROM lineaspedidos WHERE idlinea = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLinea);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                logger.log(Level.WARNING, "No se encontr\u00f3 l\u00ednea con ID: {0}", idLinea);
                throw new SQLException("No se pudo eliminar: línea no encontrada.");
            }

            logger.log(Level.INFO, "L\u00ednea eliminada: {0}", idLinea);
        }
    }

    /**
     * Elimina todas las líneas asociadas a un pedido específico.
     *
     * * @param idPedido ID del pedido padre.
     * @param con Conexión activa a la base de datos.
     * @throws Exception Si ocurre un error en la ejecución.
     */
    @Override
    public void eliminarPorPedido(int idPedido, Connection con) throws Exception {
        String sql = "DELETE FROM lineaspedidos WHERE idpedido = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);

            int filasAfectadas = ps.executeUpdate();

            logger.log(Level.INFO, "L\u00edneas eliminadas del pedido {0}: {1}", new Object[]{idPedido, filasAfectadas});
        }
    }

    /**
     * Mapea una fila del ResultSet a un objeto LineaPedido simple.
     */
    private LineaPedido mapear(ResultSet rs) throws SQLException {
        LineaPedido l = new LineaPedido();
        l.setIdlinea(rs.getInt("idlinea"));
        l.setIdpedido(rs.getInt("idpedido"));
        l.setIdproducto(rs.getInt("idproducto"));
        l.setCantidad(rs.getInt("cantidad"));
        return l;
    }

    /**
     * Mapea una fila del ResultSet a un objeto LineaPedido incluyendo la carga
     * de un objeto Producto con sus detalles.
     */
    private LineaPedido mapearConProducto(ResultSet rs) throws SQLException {
        LineaPedido l = new LineaPedido();
        l.setIdlinea(rs.getInt("idlinea"));
        l.setIdpedido(rs.getInt("idpedido"));
        l.setIdproducto(rs.getInt("idproducto"));
        l.setCantidad(rs.getInt("cantidad"));

        Producto p = new Producto();
        p.setIdproducto(rs.getInt("idproducto"));
        p.setNombre(rs.getString("nombre"));
        p.setMarca(rs.getString("marca"));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setImagen(rs.getString("imagen"));
        l.setProducto(p);

        return l;
    }

}
