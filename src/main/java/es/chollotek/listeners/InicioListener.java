package es.chollotek.listeners;

import es.chollotek.beans.Categoria;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import es.chollotek.DAO.CategoriaDAO;
import es.chollotek.DAO.ConnectionFactory;
import es.chollotek.DAO.ProductoDAO;
import es.chollotek.DAOFactory.MySQLDAOFactory;
import java.math.BigDecimal;
import java.sql.Connection;

/**
 *
 * @author Alba
 */
@WebListener
public class InicioListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        Connection con = null;

        try {
            con = ConnectionFactory.getConnection();
            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();

            // Categorías
            CategoriaDAO categoriaDAO = factory.getCategoriaDAO();
            List<Categoria> categorias = categoriaDAO.listarTodas(con);
            ctx.setAttribute("categorias", categorias);

            // Marcas
            ProductoDAO productoDAO = factory.getProductoDAO();
            List<String> marcas = productoDAO.obtenerMarcas(con);
            ctx.setAttribute("marcas", marcas);

            // Precio máximo
            BigDecimal precioMax = productoDAO.obtenerPrecioMaximo(con);
            ctx.setAttribute("precioMaxGlobal", precioMax);

            // Precio máximo
            BigDecimal precioMin = productoDAO.obtenerPrecioMinimo(con);
            ctx.setAttribute("precioMinGlobal", precioMin);

            ctx.log("✅ Chollotek iniciada. Categorías: " + categorias.size()
                    + " | Marcas: " + marcas.size()
                    + " | Precio máx: " + precioMax + "€"
                    + " | Precio min: " + precioMin + "€");

        } catch (Exception e) {
            ctx.log("❌ ERROR cargando categorías al inicio: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.closeConnection(con);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().log("Chollotek cerrada. Pool gestionado por Tomcat.");
    }
}
