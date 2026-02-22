package es.chollotek.DAOFactory;

import es.chollotek.DAO.CategoriaDAOImpl;
import es.chollotek.DAO.ProductoDAOImpl;
import es.chollotek.DAO.UsuarioDAOImpl;
import es.chollotek.DAO.UsuarioDAO;
import es.chollotek.DAO.ProductoDAO;
import es.chollotek.DAO.CategoriaDAO;
import es.chollotek.DAO.LineaPedidoDAO;
import es.chollotek.DAO.LineaPedidoDAOImpl;
import es.chollotek.DAO.PedidoDAO;
import es.chollotek.DAO.PedidoDAOImpl;

/**
 *
 * @author Alba
 */
public class MySQLDAOFactory{

       private static MySQLDAOFactory instancia;

    private MySQLDAOFactory() {}

    /** Singleton */
    public static MySQLDAOFactory getInstancia() {
        if (instancia == null) {
            synchronized (MySQLDAOFactory.class) {
                if (instancia == null) {
                    instancia = new MySQLDAOFactory();
                }
            }
        }
        return instancia;
    }

    public UsuarioDAO getUsuarioDAO() {
        return new UsuarioDAOImpl();
    }

    public ProductoDAO getProductoDAO() {
        return new ProductoDAOImpl();
    }

    public CategoriaDAO getCategoriaDAO() {
        return new CategoriaDAOImpl();
    }

    public PedidoDAO getPedidoDAO() {
        return new PedidoDAOImpl();
    }

    public LineaPedidoDAO getLineaPedidoDAO() {
        return new LineaPedidoDAOImpl();
    }
    
}
