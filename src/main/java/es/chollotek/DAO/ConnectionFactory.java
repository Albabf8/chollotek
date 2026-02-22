package es.chollotek.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author Alba
 */
public class ConnectionFactory {
    
        private static final Logger logger = Logger.getLogger(ConnectionFactory.class.getName());
    private static DataSource dataSource = null;
    private static final String DATASOURCE_NAME = "java:comp/env/jdbc/chollotek";
    
    // Bloque estático: se ejecuta UNA VEZ al cargar la clase
    static {
        try {
            Context contextoInicial = new InitialContext();
            dataSource = (DataSource) contextoInicial.lookup(DATASOURCE_NAME);
            logger.info("✅ DataSource inicializado correctamente: " + DATASOURCE_NAME);
        } catch (NamingException ex) {
            logger.log(Level.SEVERE, "❌ ERROR: No se pudo inicializar el DataSource. "
                    + "Verifica que context.xml tenga configurado: " + DATASOURCE_NAME, ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    /**
     * Obtiene una conexión del pool de Tomcat.
     * 
     * IMPORTANTE: Debes cerrar la conexión con closeConnection(con) cuando termines,
     * esto la devuelve al pool (no la destruye).
     * 
     * @return Connection del pool
     * @throws SQLException si no hay conexiones disponibles o hay error
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource no inicializado. Verifica context.xml");
        }
        
        Connection con = dataSource.getConnection();
        logger.fine("Conexión obtenida del pool");
        return con;
    }
    
    /**
     * Cierra una conexión de forma segura (la devuelve al pool).
     * 
     * @param con conexión a cerrar (puede ser null)
     */
    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                if (!con.isClosed()) {
                    con.close();  // la devuelve al pool
                    logger.fine("Conexión cerrada (devuelta al pool)");
                }
            } catch (SQLException ex) {
                logger.log(Level.WARNING, "Error al cerrar conexión", ex);
            }
        }
    }
    
    /**
     * Cierra una conexión sin lanzar excepciones.
     * Útil para bloques finally.
     * 
     * @param con conexión a cerrar
     */
    public static void closeQuietly(Connection con) {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (SQLException ex) {
            // Ignorar silenciosamente
        }
    }
 
}
