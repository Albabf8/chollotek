package es.chollotek.DAO;

import es.chollotek.beans.Pedido;
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
public class PedidoDAOImpl implements PedidoDAO {

    private static final Logger logger = Logger.getLogger(PedidoDAOImpl.class.getName());

    /**
     * Busca un pedido por su identificador único.
     *
     * * @param idPedido El ID del pedido a recuperar.
     * @param con Conexión activa a la base de datos.
     * @return El objeto {@link Pedido} encontrado o {@code null} si no existe.
     * @throws Exception Si ocurre un error en la consulta SQL.
     */
    @Override
    public Pedido buscarPorId(int idPedido, Connection con) throws Exception {
        String sql = "SELECT * FROM pedidos WHERE idpedido = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.log(Level.INFO, "Pedido encontrado con ID: {0}", idPedido);
                    return mapear(rs);   // reutiliza el mapear() privado ya existente
                }
            }
        }

        logger.log(Level.INFO, "No se encontró pedido con ID: {0}", idPedido);
        return null;
    }

    /**
     * Recupera el pedido que actúa como carrito activo para un usuario (estado
     * = 'c').
     *
     * * @param idUsuario Identificador del usuario.
     * @param con Conexión activa a la base de datos.
     * @return El objeto {@link Pedido} en estado de carrito, o {@code null} si
     * el usuario no tiene carrito activo.
     * @throws Exception Si ocurre un error en la base de datos.
     */
    @Override
    public Pedido buscarCarrito(int idUsuario, Connection con) throws Exception {
        String sql = "SELECT * FROM pedidos WHERE idusuario = ? AND estado = 'c'";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    logger.log(Level.INFO, "Carrito encontrado para usuario: {0}", idUsuario);
                    return mapear(rs);
                }
            }
        }

        logger.log(Level.INFO, "No hay carrito activo para usuario: {0}", idUsuario);
        return null;
    }

    /**
     * Lista todos los pedidos de un usuario que ya han sido confirmados (estado
     * = 'f'). Los resultados se devuelven ordenados por fecha descendente.
     *
     * * @param idUsuario Identificador del usuario.
     * @param con Conexión activa a la base de datos.
     * @return Una lista de {@link Pedido} finalizados.
     * @throws Exception Si ocurre un error durante la consulta.
     */
    @Override
    public List<Pedido> listarPedidosFinalizados(int idUsuario, Connection con) throws Exception {
        List<Pedido> lista = new ArrayList<>();
        String sql = "SELECT * FROM pedidos WHERE idusuario = ? AND estado = 'f' "
                + "ORDER BY fecha DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }

        logger.log(Level.INFO, "Pedidos finalizados encontrados para usuario {0}: {1}", new Object[]{idUsuario, lista.size()});
        return lista;
    }

    /**
     * Inserta un nuevo pedido y recupera el ID generado automáticamente por la
     * base de datos.
     *
     * * @param pedido El objeto {@link Pedido} a persistir.
     * @param con Conexión activa a la base de datos.
     * @return El ID (clave primaria) asignado al nuevo pedido.
     * @throws SQLException Si falla la inserción o no se puede recuperar la
     * clave autogenerada.
     * @throws Exception Para errores generales.
     */
    @Override
    public int insertar(Pedido pedido, Connection con) throws Exception {
        String sql = "INSERT INTO pedidos (fecha, estado, idusuario, importe, iva) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, new java.sql.Date(pedido.getFecha().getTime()));
            ps.setString(2, String.valueOf(pedido.getEstado()));
            ps.setInt(3, pedido.getIdusuario());
            ps.setBigDecimal(4, pedido.getImporte());
            ps.setBigDecimal(5, pedido.getIva());
            ps.executeUpdate();

            // Recuperar el ID autogenerado
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int idGenerado = keys.getInt(1);
                    logger.log(Level.INFO, "Pedido insertado con ID: {0}", idGenerado);
                    return idGenerado;
                }
            }
        }

        throw new SQLException("No se pudo obtener el id del nuevo pedido.");
    }

    /**
     * Actualiza los valores económicos (importe e IVA) de un pedido existente.
     *
     * * @param pedido Objeto pedido con los nuevos datos y el ID
     * correspondiente.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si el pedido no existe.
     */
    @Override
    public void actualizar(Pedido pedido, Connection con) throws Exception {
        String sql = "UPDATE pedidos SET importe = ?, iva = ? WHERE idpedido = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, pedido.getImporte());
            ps.setBigDecimal(2, pedido.getIva());
            ps.setInt(3, pedido.getIdpedido());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                logger.log(Level.WARNING, "No se encontr\u00f3 pedido con ID: {0}", pedido.getIdpedido());
                throw new SQLException("No se pudo actualizar: pedido no encontrado.");
            }

            logger.log(Level.INFO, "Pedido actualizado: {0} - Importe: {1}\u20ac", new Object[]{pedido.getIdpedido(), pedido.getImporte()});
        }
    }

    /**
     * Cambia el estado de un pedido a 'f' (finalizado) y actualiza la fecha a
     * la fecha actual del sistema.
     *
     * * @param idPedido ID del pedido a cerrar.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si el ID no existe.
     */
    @Override
    public void finalizarPedido(int idPedido, Connection con) throws Exception {
        String sql = "UPDATE pedidos SET estado = 'f', fecha = CURDATE() WHERE idpedido = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                logger.log(Level.WARNING, "No se encontr\u00f3 pedido con ID: {0}", idPedido);
                throw new SQLException("No se pudo finalizar: pedido no encontrado.");
            }

            logger.log(Level.INFO, "Pedido finalizado: {0}", idPedido);
        }
    }

    /**
     * Elimina físicamente un pedido de la base de datos.
     *
     * * @param idPedido ID del pedido a borrar.
     * @param con Conexión activa a la base de datos.
     * @throws SQLException Si el pedido no existe.
     */
    @Override
    public void eliminar(int idPedido, Connection con) throws Exception {
        String sql = "DELETE FROM pedidos WHERE idpedido = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                logger.log(Level.WARNING, "No se encontr\u00f3 pedido con ID: {0}", idPedido);
                throw new SQLException("No se pudo eliminar: pedido no encontrado.");
            }

            logger.log(Level.INFO, "Pedido eliminado: {0}", idPedido);
        }
    }

    /**
     * Convierte el registro actual de un ResultSet en un objeto Pedido. Maneja
     * la conversión de tipos SQL Date a Java Date y de String a char para el
     * estado.
     *
     * * @param rs El ResultSet posicionado en la fila a mapear.
     * @return Un objeto {@link Pedido} poblado con los datos de la fila.
     * @throws SQLException Si ocurre un error al acceder a los datos.
     */
    private Pedido mapear(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setIdpedido(rs.getInt("idpedido"));
        p.setFecha(new java.util.Date(rs.getDate("fecha").getTime()));

        // El estado se guarda como CHAR(1) en BD → recuperar como char
        String estado = rs.getString("estado");
        if (estado != null && !estado.isEmpty()) {
            p.setEstado(estado.charAt(0));
        }

        p.setIdusuario(rs.getInt("idusuario"));
        p.setImporte(rs.getBigDecimal("importe"));
        p.setIva(rs.getBigDecimal("iva"));
        return p;
    }

}
