package es.chollotek.DAO;

import es.chollotek.beans.Usuario;
import java.sql.Connection;
import java.util.List;

/**
 *
 * @author Alba
 */
public interface UsuarioDAO {
    
/**
     * Actualiza los datos editables de un usuario.
     *
     * Según el enunciado:
     *   - NO se puede cambiar: email, NIF
     *   - SÍ se puede cambiar: nombre, apellidos, teléfono, dirección,
     *                          código postal, localidad, provincia, avatar
     *   - La contraseña se cambia con actualizarPassword()
     *
     * @param usuario objeto con los nuevos datos (debe tener idusuario)
     * @param con conexión activa (debe estar en transacción)
     * @throws Exception si hay error o el usuario no existe
     */
    void actualizar(Usuario usuario, Connection con) throws Exception;

    /**
     * Actualiza únicamente la contraseña de un usuario.
     *
     * IMPORTANTE:
     *   - La validación de "contraseña actual correcta" se hace ANTES
     *     de llamar a este método, en la capa de lógica de negocio.
     *   - La contraseña nueva debe venir YA ENCRIPTADA en MD5.
     *
     * @param id identificador del usuario
     * @param passwordNueva contraseña nueva ya encriptada en MD5
     * @param con conexión activa (debe estar en transacción)
     * @throws Exception si hay error o el usuario no existe
     */
    void actualizarPassword(int id, String passwordNueva, Connection con) throws Exception;

    /**
     * Actualiza la fecha y hora del último acceso del usuario.
     * Se llama automáticamente cada vez que el usuario hace login.
     *
     * @param id identificador del usuario
     * @param con conexión activa
     * @throws Exception si hay error al actualizar
     */
    void actualizarUltimoAcceso(int id, Connection con) throws Exception;

    /**
     * Busca un usuario por su email.
     * Se usa principalmente en el proceso de login.
     *
     * @param email dirección de correo electrónico
     * @param con conexión activa a la base de datos
     * @return UsuarioDTO si existe, null si no se encuentra
     * @throws Exception si hay error en la consulta
     */
    Usuario buscarPorEmail(String email, Connection con) throws Exception;

    /**
     * Busca un usuario por su ID.
     * Se usa para cargar el perfil del usuario o verificar su existencia.
     *
     * @param id identificador del usuario
     * @param con conexión activa a la base de datos
     * @return UsuarioDTO si existe, null si no se encuentra
     * @throws Exception si hay error en la consulta
     */
    Usuario buscarPorId(int id, Connection con) throws Exception;

    /**
     * Verifica si un email ya está registrado en la base de datos.
     * Se usa en el proceso de registro para validación Ajax.
     *
     * @param email dirección de correo a verificar
     * @param con conexión activa a la base de datos
     * @return true si el email ya existe, false si está disponible
     * @throws Exception si hay error en la consulta
     */
    boolean emailExiste(String email, Connection con) throws Exception;

    /**
     * Inserta un nuevo usuario en la base de datos.
     * Se usa en el proceso de registro.
     *
     * IMPORTANTE: La contraseña debe venir YA ENCRIPTADA en MD5.
     *
     * @param usuario objeto con todos los datos del nuevo usuario
     * @param con conexión activa (debe estar en transacción)
     * @throws Exception si hay error al insertar o email duplicado
     */
    void insertar(Usuario usuario, Connection con) throws Exception;

    /**
     * Lista todos los usuarios registrados en el sistema.
     * Se usa principalmente para reportes o administración.
     *
     * @param con conexión activa a la base de datos
     * @return lista de todos los usuarios (vacía si no hay ninguno)
     * @throws Exception si hay error en la consulta
     */
    List<Usuario> listarTodos(Connection con) throws Exception; 
    
}


