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

/** * Devuelve la instancia única de MySQLDAOFactory.
     * Implementa la inicialización diferida (Lazy Initialization) con un 
     * bloqueo de doble verificación (Double-Checked Locking) para garantizar 
     * la seguridad entre hilos.
     * * @return Instancia única de la factoría.
     */
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

    /**
     * Proporciona una implementación concreta de UsuarioDAO.
     * * @return Una nueva instancia de UsuarioDAOImpl.
     */
    public UsuarioDAO getUsuarioDAO() {
        return new UsuarioDAOImpl();
    }

    /**
     * Proporciona una implementación concreta de ProductoDAO.
     * * @return Una nueva instancia de ProductoDAOImpl.
     */
    public ProductoDAO getProductoDAO() {
        return new ProductoDAOImpl();
    }

    /**
     * Proporciona una implementación concreta de CategoriaDAO.
     * * @return Una nueva instancia de CategoriaDAOImpl.
     */
    public CategoriaDAO getCategoriaDAO() {
        return new CategoriaDAOImpl();
    }

    /**
     * Proporciona una implementación concreta de PedidoDAO.
     * * @return Una nueva instancia de PedidoDAOImpl.
     */
    public PedidoDAO getPedidoDAO() {
        return new PedidoDAOImpl();
    }

    /**
     * Proporciona una implementación concreta de LineaPedidoDAO.
     * * @return Una nueva instancia de LineaPedidoDAOImpl.
     */
    public LineaPedidoDAO getLineaPedidoDAO() {
        return new LineaPedidoDAOImpl();
    }

}
