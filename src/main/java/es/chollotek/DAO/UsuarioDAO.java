package es.chollotek.DAO;

import es.chollotek.beans.Usuario;

/**
 *
 * @author Alba
 */
public interface UsuarioDAO {
    
    public Boolean registrarUsuario(Usuario usuario);

    public void closeConnection();
    
}


