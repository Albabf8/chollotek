package es.chollotek.DAO;

import es.chollotek.beans.LineaPedido;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author Alba
 */
public interface LineaPedidoDAO {

    /**
     * Actualiza la cantidad de una línea existente.
     *
     * Se usa principalmente en dos escenarios:
     *
     * 1) Funcionalidad Ajax de aumentar/disminuir cantidad: - Botón [+]: llama
     * a este método con cantidad actual + 1 - Botón [-]: llama a este método
     * con cantidad actual - 1 (si llegaría a 0, en vez de actualizar se elimina
     * la línea)
     *
     * 2) Usuario añade producto que ya estaba en carrito: - Se suma la nueva
     * cantidad a la existente
     *
     * IMPORTANTE: - La cantidad DEBE ser mayor o igual a 1 (si es 0 hay que eliminar la
     * línea) - Debe ejecutarse en la misma transacción que actualizar el
     * importe del pedido
     *
     * @param idLinea identificador único de la línea a modificar
     * @param cantidad nueva cantidad total del producto (no suma, reemplaza)
     * @param con conexión activa (DEBE estar en transacción)
     * @throws Exception si hay error al actualizar, línea no existe, o cantidad
     * inválida
     */
    void actualizarCantidad(int idLinea, int cantidad, Connection con) throws Exception;

    /**
     * Busca una línea concreta dentro de un pedido identificándola por el
     * producto.
     *
     * Se usa antes de añadir un producto al carrito para verificar si ya
     * existe: - Si existe: se actualiza su cantidad (suma la nueva cantidad a
     * la existente) - Si no existe: se inserta una nueva línea con cantidad
     * inicial
     *
     * Esta lógica evita tener líneas duplicadas del mismo producto en un
     * pedido.
     *
     * @param idPedido identificador del pedido donde buscar
     * @param idProducto identificador del producto a buscar dentro del pedido
     * @param con conexión activa a la base de datos (puede estar en
     * transacción)
     * @return LineaPedido si el producto ya está en el carrito, null si no
     * existe
     * @throws Exception si hay error en la consulta SQL o problemas de conexión
     */
    LineaPedido buscarLinea(int idPedido, int idProducto, Connection con) throws Exception;

    /**
     * Elimina una línea concreta del pedido.
     *
     * Se usa cuando: - El usuario pulsa "eliminar" en un producto del carrito -
     * La cantidad llegaría a 0 al disminuir (alternativa a actualizar a 0)
     *
     * Tras eliminar la línea, hay que recalcular y actualizar el importe total
     * del pedido en la misma transacción.
     *
     * Si era la última línea del pedido, el carrito quedaría vacío pero el
     * pedido seguiría existiendo (se puede eliminar o dejar para futuras
     * compras).
     *
     * @param idLinea identificador único de la línea a eliminar
     * @param con conexión activa (DEBE estar en transacción)
     * @throws Exception si hay error al eliminar o la línea no existe
     */
    void eliminar(int idLinea, Connection con) throws Exception;

    /**
     * Elimina TODAS las líneas de un pedido de una sola vez.
     *
     * Se usa en dos escenarios principales:
     *
     * 1) Usuario vacía el carrito por completo: - eliminarPorPedido(idPedido) ←
     * primero - PedidoDAO.eliminar(idPedido) ← luego (opcional)
     *
     * 2) Antes de eliminar un pedido padre (para respetar FK): -
     * eliminarPorPedido(idPedido) ← primero (obligatorio) -
     * PedidoDAO.eliminar(idPedido) ← luego
     *
     * IMPORTANTE: Este método DEBE ejecutarse ANTES de eliminar el pedido
     * padre, ya que existe una FK de lineaspedidos → pedidos.
     *
     * Ambas operaciones deben estar en la misma transacción para garantizar
     * consistencia (o se eliminan ambas o ninguna).
     *
     * @param idPedido identificador del pedido cuyas líneas se eliminarán todas
     * @param con conexión activa (DEBE estar en transacción)
     * @throws Exception si hay error al eliminar o el pedido no existe
     */
    void eliminarPorPedido(int idPedido, Connection con) throws Exception;

    /**
     * Inserta una nueva línea en un pedido.
     *
     * Se llama cuando el usuario añade un producto que NO estaba previamente en
     * su carrito. Antes de llamar a este método, siempre hay que verificar con
     * buscarLinea() que el producto no existe ya.
     *
     * La cantidad inicial suele ser 1, pero puede ser mayor si el usuario
     * especificó cantidad al añadir el producto.
     *
     * IMPORTANTE: Debe ejecutarse en la misma transacción que la actualización
     * del importe total del pedido padre.
     *
     * @param linea objeto LineaPedido con idpedido, idproducto y cantidad
     * @param con conexión activa (DEBE estar en transacción)
     * @throws Exception si hay error al insertar, violación FK, o datos
     * inválidos
     */
    void insertar(LineaPedido linea, Connection con) throws Exception;

    /**
     * Devuelve todas las líneas (productos) de un pedido concreto.
     *
     * Se usa principalmente para: - Mostrar el contenido del carrito con todos
     * sus productos - Generar el detalle de un pedido finalizado en el
     * historial - Calcular el importe total sumando (cantidad × precio) de cada
     * línea
     *
     * Las líneas se devuelven en el orden en que fueron insertadas (por
     * idlinea), aunque la vista puede reordenarlas por nombre de producto.
     *
     * @param idPedido identificador del pedido cuyas líneas se quieren obtener
     * @param con conexión activa a la base de datos (puede estar en
     * transacción)
     * @return lista de líneas del pedido (vacía si el pedido no tiene
     * productos)
     * @throws Exception si hay error en la consulta SQL o problemas de conexión
     */
    List<LineaPedido> listarPorPedido(int idPedido, Connection con) throws Exception;

}
