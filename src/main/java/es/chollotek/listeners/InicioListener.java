package es.chollotek.listeners;

import es.chollotek.beans.Categoria;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import es.chollotek.DAO.CategoriaDAO;
import es.chollotek.DAO.ConnectionFactory;
import es.chollotek.DAOFactory.MySQLDAOFactory;
import java.sql.Connection;

/**
 *
 * @author Alba
 */
@WebListener
public class InicioListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        Connection con = null;
        
        try {
            con = ConnectionFactory.getConnection();
            CategoriaDAO dao = MySQLDAOFactory.getInstancia().getCategoriaDAO();
            List<Categoria> categorias = dao.listarTodas(con);
            
            ctx.setAttribute("categorias", categorias);
            ctx.log("✅ Chollotek iniciada. Categorías cargadas: " + categorias.size());
            
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
