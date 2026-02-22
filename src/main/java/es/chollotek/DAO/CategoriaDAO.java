package es.chollotek.DAO;

import es.chollotek.beans.Categoria;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author Alba
 */
public interface CategoriaDAO {

/**
     * Busca una categoría específica por su identificador único.
     *
     * Se usa cuando se necesita obtener información detallada de una categoría,
     * por ejemplo para mostrar su nombre e imagen en la cabecera de una página
     * filtrada por categoría, o para validar que existe antes de filtrar productos.
     *
     * @param id identificador único de la categoría (idcategoria)
     * @param con conexión activa a la base de datos
     * @return Categoria con todos sus datos si existe, null si no se encuentra
     * @throws Exception si hay error en la consulta SQL o problemas de conexión
     */
    Categoria buscarPorId(byte id, Connection con) throws Exception;

    /**
     * Lista todas las categorías disponibles en el sistema.
     *
     * Se usa principalmente en el AppListener al arrancar la aplicación
     * para cargarlas en el atributo de contexto "categorias", haciendo que
     * estén disponibles para todas las JSPs sin necesidad de consultarlas
     * repetidamente desde la base de datos.
     *
     * Las categorías se devuelven ordenadas alfabéticamente por nombre
     * para mantener un orden consistente en los menús de navegación.
     *
     * @param con conexión activa a la base de datos
     * @return lista de todas las categorías (vacía si no hay categorías configuradas)
     * @throws Exception si hay error en la consulta SQL o problemas de conexión
     */
    List<Categoria> listarTodas(Connection con) throws Exception;
}
