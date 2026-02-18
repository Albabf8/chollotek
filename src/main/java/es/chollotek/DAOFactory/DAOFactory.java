package es.chollotek.DAOFactory;

import es.chollotek.DAO.UsuarioDAO;
import es.chollotek.DAO.ProductoDAO;
import es.chollotek.DAO.CategoriaDAO;

/**
 *
 * @author Alba
 */
public abstract class DAOFactory {

    public abstract CategoriaDAO getCategoriaDAO();

    public abstract ProductoDAO getProductoDAO();

    public abstract UsuarioDAO getUsuarioDAO();

    public static DAOFactory getDAOFactory() {

        DAOFactory daof = null;

        daof = new MySQLDAOFactory();

        return daof;

    }
}
