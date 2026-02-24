package es.chollotek.DAO;

import es.chollotek.beans.Pedido;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author Alba
 */
public interface PedidoDAO {
    /**
     * Actualiza el importe total e IVA de un pedido existente.
     *
     * Se llama cada vez que se modifica el contenido del carrito:
     * - Al añadir un producto
     * - Al aumentar/disminuir cantidad de un producto
     * - Al eliminar un producto del carrito
     *
     * El importe e IVA se recalculan en la capa de negocio sumando
     * todas las líneas del pedido, y luego se persisten con este método.
     *
     * IMPORTANTE: Debe ejecutarse en la misma transacción que las
     * modificaciones de las líneas del pedido.
     *
     * @param pedido objeto Pedido con el idpedido, importe e iva actualizados
     * @param con conexión activa (DEBE estar en transacción)
     * @throws Exception si hay error al actualizar o el pedido no existe
     */
    void actualizar(Pedido pedido, Connection con) throws Exception;

    /**
     * Busca el pedido en estado carrito ('c') de un usuario específico.
     *
     * Cada usuario registrado solo puede tener UN carrito activo a la vez.
     * Si el usuario no tiene carrito activo, devuelve null (se creará uno
     * la primera vez que añada un producto).
     *
     * Para usuarios anónimos, el carrito se gestiona en sesión y luego
     * se asocia al usuario cuando se registre o haga login.
     *
     * @param idUsuario identificador del usuario cuyo carrito se busca
     * @param con conexión activa a la base de datos (puede estar en transacción)
     * @return Pedido en estado 'c' si existe, null si el usuario no tiene carrito activo
     * @throws Exception si hay error en la consulta SQL o problemas de conexión
     */
    Pedido buscarCarrito(int idUsuario, Connection con) throws Exception;

    /**
     * Elimina un pedido completo de la base de datos.
     *
     * Se usa cuando el usuario vacía completamente su carrito
     * (elimina todos los productos). En vez de dejar un pedido vacío,
     * se elimina el pedido entero.
     *
     * IMPORTANTE: Las líneas del pedido (lineaspedidos) deben eliminarse
     * ANTES de eliminar el pedido padre, para respetar la FK.
     *
     * Flujo correcto para vaciar carrito:
     *   1. LineaPedidoDAO.eliminarPorPedido(idPedido)  ← primero las líneas
     *   2. PedidoDAO.eliminar(idPedido)                 ← luego el pedido
     *
     * Ambas operaciones deben estar en la misma transacción.
     *
     * @param idPedido identificador del pedido a eliminar
     * @param con conexión activa (DEBE estar en transacción)
     * @throws Exception si hay error al eliminar, violación FK, o pedido no existe
     */
    void eliminar(int idPedido, Connection con) throws Exception;

    /**
     * Finaliza un pedido: cambia su estado de 'c' a 'f' y registra la fecha actual.
     *
     * Se llama cuando el usuario confirma la compra (solo usuarios registrados
     * pueden finalizar pedidos según el enunciado). Este cambio de estado
     * marca que el carrito ya no es editable y pasa a ser un pedido histórico.
     *
     * La fecha se actualiza a la fecha actual (CURDATE()) que es cuando
     * realmente se confirma la compra, aunque el carrito pudiera existir
     * desde días antes.
     *
     * IMPORTANTE: Esta operación es crítica y debe estar en transacción,
     * pudiendo incluir operaciones adicionales como actualizar stock,
     * enviar emails de confirmación, etc.
     *
     * @param idPedido identificador del pedido/carrito a finalizar
     * @param con conexión activa (DEBE estar en transacción)
     * @throws Exception si hay error al finalizar o el pedido no existe
     */
    void finalizarPedido(int idPedido, Connection con) throws Exception;

    /**
     * Inserta un nuevo pedido en la base de datos y devuelve su ID generado.
     *
     * Se llama cuando un usuario añade el primer producto a su carrito
     * (si no tenía carrito previo) o cuando se confirma una compra.
     *
     * El pedido se crea normalmente en estado 'c' (carrito) con importe
     * e IVA inicializados a 0, que se actualizarán conforme se añadan productos.
     *
     * IMPORTANTE: Este método DEBE ejecutarse dentro de una transacción,
     * ya que inmediatamente después suele insertarse la primera línea del pedido.
     *
     * @param pedido objeto Pedido con los datos iniciales (estado, idusuario, fecha)
     * @param con conexión activa (DEBE estar en transacción para rollback si falla)
     * @return idpedido generado automáticamente por la base de datos
     * @throws SQLException si hay error al insertar o no se puede obtener el ID generado
     */
    int insertar(Pedido pedido, Connection con) throws Exception;

    /**
     * Lista todos los pedidos finalizados ('f') de un usuario.
     *
     * Se usa en la zona privada del usuario para mostrar su historial
     * de compras, permitiéndole revisar pedidos anteriores con sus
     * importes, fechas y productos comprados.
     *
     * Los pedidos se devuelven ordenados del más reciente al más antiguo
     * (fecha descendente) para que aparezcan primero las compras recientes.
     *
     * @param idUsuario identificador del usuario cuyos pedidos se consultan
     * @param con conexión activa a la base de datos
     * @return lista de pedidos finalizados (vacía si el usuario nunca ha comprado)
     * @throws Exception si hay error en la consulta SQL o problemas de conexión
     */
    List<Pedido> listarPedidosFinalizados(int idUsuario, Connection con) throws Exception;
    
}
