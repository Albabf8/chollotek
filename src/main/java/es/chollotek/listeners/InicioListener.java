package es.chollotek.listeners;


import es.chollotek.DAOFactory.DAOFactory;
import es.chollotek.beans.Categoria;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import es.chollotek.DAO.CategoriaDAO;

/**
 *
 * @author Alba
 */
@WebListener
public class InicioListener implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce) {
       System.out.println("--- CHOLLOTEK: CARGANDO DATOS AL ARRANCAR ---");
        
       ServletContext contexto = sce.getServletContext();
       
       try {
            // A) Obtenemos la fábrica y el DAO
            DAOFactory daof = DAOFactory.getDAOFactory();
            CategoriaDAO cdao = daof.getCategoriaDAO();
            
            List<Categoria> listaCategorias = cdao.getCategorias();
            
            // C) Los guardamos en el APPLICATION SCOPE
           
            contexto.setAttribute("categorias", listaCategorias);
            
            System.out.println("-> Éxito: " + listaCategorias.size() + " categorias listas.");
            
        } catch (Exception e) {
            System.err.println("--- ERROR AL ARRANCAR CHOLLOTEK ---");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
       
    }
    
}
