package es.chollotek.DAO;

import es.chollotek.beans.Producto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author Alba
 */
public interface ProductoDAO {

    /**
     * Busca productos aplicando múltiples filtros de forma dinámica. Se usa en
     * la funcionalidad de búsqueda avanzada donde el usuario puede combinar
     * varios criterios: nombre, marca y rango de precio.
     *
     * Los parámetros null o vacíos se ignoran en la búsqueda, permitiendo
     * búsquedas flexibles (por ejemplo, solo por marca, o solo por precio).
     *
     * IMPORTANTE: Usa LIKE con % para búsquedas parciales en nombre y marca,
     * permitiendo al usuario encontrar productos sin escribir el nombre
     * completo.
     *
     * @param nombre texto a buscar en el nombre del producto (null para
     * ignorar)
     * @param marca texto a buscar en la marca del producto (null para ignorar)
     * @param precioMin precio mínimo del rango (null para no poner límite
     * inferior)
     * @param precioMax precio máximo del rango (null para no poner límite
     * superior)
     * @param con conexión activa a la base de datos
     * @return lista de productos que cumplen TODOS los criterios especificados
     * @throws Exception si hay error en la consulta SQL o problemas de conexión
     */
    List<Producto> buscarPorFiltros(String nombre, String marca, BigDecimal precioMin, BigDecimal precioMax, Connection con) throws Exception;

    /**
     * Busca un producto específico por su identificador único. Se usa para
     * mostrar la ficha detallada de un producto cuando el usuario hace clic en
     * él desde el catálogo o búsqueda.
     *
     * @param id identificador único del producto (idproducto)
     * @param con conexión activa a la base de datos
     * @return Producto con todos sus datos si existe, null si no se encuentra
     * @throws Exception si hay error en la consulta SQL o problemas de conexión
     */
    Producto buscarPorId(int id, Connection con) throws Exception;

    /**
     * Lista los productos de una categoría específica. Se usa cuando el usuario
     * filtra por categoría desde el menú lateral o hace clic en una categoría
     * concreta.
     *
     * Los productos se devuelven ordenados por nombre para mantener
     * consistencia con otras listados.
     *
     * @param idCategoria identificador de la categoría a filtrar
     * @param con conexión activa a la base de datos
     * @return lista de productos de esa categoría (vacía si no hay productos en
     * ella)
     * @throws Exception si hay error en la consulta SQL o problemas de conexión
     */
    List<Producto> listarPorCategoria(int idCategoria, Connection con) throws Exception;

    /**
     * Lista todos los productos disponibles en la tienda. Se usa para mostrar
     * el catálogo completo de productos, ordenados alfabéticamente por nombre
     * para facilitar la navegación.
     *
     * @param con conexión activa a la base de datos
     * @return lista de todos los productos (lista vacía si no hay productos)
     * @throws Exception si hay error en la consulta SQL o problemas de conexión
     */
    List<Producto> listarTodos(Connection con) throws Exception;

    /**
     * Obtiene todas las marcas únicas que existen en la tabla productos. Se usa
     * para popular el filtro desplegable de marcas en el buscador, mostrando
     * solo las marcas que tienen al menos un producto.
     *
     * Las marcas se devuelven ordenadas alfabéticamente y sin duplicados.
     *
     * @param con conexión activa a la base de datos
     * @return lista de strings con los nombres únicos de marcas (vacía si no
     * hay productos)
     * @throws Exception si hay error en la consulta SQL o problemas de conexión
     */
    List<String> obtenerMarcas(Connection con) throws Exception;

    /**
     * Obtiene el precio máximo entre todos los productos de la tienda. Se usa
     * para establecer el límite superior del filtro de precio en la búsqueda,
     * ajustándose dinámicamente al catálogo real.
     *
     * @param con conexión activa a la base de datos
     * @return precio máximo encontrado, o BigDecimal.ZERO si no hay productos
     * @throws Exception si hay error en la consulta SQL o problemas de conexión
     */
    BigDecimal obtenerPrecioMaximo(Connection con) throws Exception;

    /**
     * Obtiene el precio mínimo entre todos los productos de la tienda. Se usa
     * para establecer el límite inferior del filtro de precio en la búsqueda,
     * ajustándose dinámicamente al catálogo real.
     *
     * @param con conexión activa a la base de datos
     * @return precio mínimo encontrado, o BigDecimal.ZERO si no hay productos
     * @throws Exception si hay error en la consulta SQL o problemas de conexión
     */
    BigDecimal obtenerPrecioMinimo(Connection con) throws Exception;

    /**
     * Obtiene un número determinado de productos de forma aleatoria. Se usa
     * para mostrar productos destacados en la página principal (landing page) o
     * secciones de "productos recomendados", manteniendo variedad en cada
     * visita.
     *
     * Usa la función RAND() de MySQL para aleatorización, limitando el
     * resultado a la cantidad solicitada.
     *
     * @param cantidad número de productos aleatorios a obtener (típicamente 8
     * para landing)
     * @param con conexión activa a la base de datos
     * @return lista de productos aleatorios (puede tener menos elementos si no
     * hay suficientes productos)
     * @throws Exception si hay error en la consulta SQL o problemas de conexión
     */
    List<Producto> obtenerProductosAleatorios(int cantidad, Connection con) throws Exception;

}
