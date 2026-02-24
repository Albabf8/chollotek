package es.chollotek.controllers;

import es.chollotek.DAO.ConnectionFactory;
import es.chollotek.DAO.UsuarioDAO;
import es.chollotek.DAOFactory.MySQLDAOFactory;
import es.chollotek.beans.Usuario;
import es.chollotek.models.MD5;
import java.io.IOException;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Alba
 */

    @WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})

public class LoginController extends HttpServlet{

    /**
     * Procesa la petición de login del usuario.
     * Verifica email y contraseña, actualiza último acceso y crea sesión.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String url = "JSP/login.jsp";
        Connection con = null;

        try {
            // 1. Recoger parámetros del formulario
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // 2. Validar que no vengan vacíos
            if (email == null || email.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                request.setAttribute("mensajeError", "Todos los campos son obligatorios.");
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }

            // 3. Obtener conexión del pool
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false); // Iniciar transacción

            // 4. Obtener DAO
            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            UsuarioDAO dao = factory.getUsuarioDAO();

            // 5. Buscar usuario por email
            Usuario usuario = dao.buscarPorEmail(email.trim(), con);

            // 6. Verificar si existe el usuario
            if (usuario == null) {
                request.setAttribute("mensajeError", "Email o contraseña incorrectos.");
                request.setAttribute("emailIntroducido", email);
                con.rollback();
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }

            // 7. Verificar contraseña (comparar MD5)
            String passwordMD5 = MD5.encriptar(password);
            if (!usuario.getPassword().equals(passwordMD5)) {
                request.setAttribute("mensajeError", "Email o contraseña incorrectos.");
                request.setAttribute("emailIntroducido", email);
                con.rollback();
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }

            // 8. Login exitoso: actualizar último acceso
            dao.actualizarUltimoAcceso(usuario.getIdusuario(), con);
            con.commit(); // Confirmar transacción

            // 9. Crear sesión
            HttpSession sesion = request.getSession(true);
            sesion.setAttribute("usuario", usuario);
            sesion.setMaxInactiveInterval(2 * 24 * 60 * 60); // 2 días en segundos

            // 10. Redirigir a página principal o a donde venía
            String origen = request.getParameter("origen");
            if (origen != null && !origen.isEmpty()) {
                url = origen;
            } else {
                url = "FrontController?accion=inicio";
            }

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { }
            }
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error en el sistema: " + e.getMessage());
            request.getRequestDispatcher("JSP/login.jsp").forward(request, response);
            return;
            
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        // Redirigir (no forward) para evitar reenvío de formulario
        response.sendRedirect(url);
    }

    /**
     * Maneja peticiones GET redirigiendo a POST.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Login Controller - Autenticación de usuarios";
    }
    
}
