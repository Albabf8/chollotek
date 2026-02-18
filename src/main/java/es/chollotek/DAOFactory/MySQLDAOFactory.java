package es.chollotek.DAOFactory;

import es.chollotek.DAO.CategoriaDAOImpl;
import es.chollotek.DAO.ProductoDAOImpl;
import es.chollotek.DAO.UsuarioDAOImpl;
import es.chollotek.DAO.UsuarioDAO;
import es.chollotek.DAO.ProductoDAO;
import es.chollotek.DAO.CategoriaDAO;

/**
 *
 * @author Alba
 */
public class MySQLDAOFactory extends DAOFactory{

    @Override
    public CategoriaDAO getCategoriaDAO() {
        return new CategoriaDAOImpl();
    }
    
    @Override
    public ProductoDAO getProductoDAO() {
        return new ProductoDAOImpl();
    }
    
    @Override
    public UsuarioDAO getUsuarioDAO() {
        return new UsuarioDAOImpl();
    }
    
}
