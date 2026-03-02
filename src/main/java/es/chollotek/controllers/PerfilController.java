package es.chollotek.controllers;

import es.chollotek.DAO.ConnectionFactory;
import es.chollotek.DAO.UsuarioDAO;
import es.chollotek.DAOFactory.MySQLDAOFactory;
import es.chollotek.beans.Usuario;
import es.chollotek.models.MD5;
import java.io.IOException;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.beanutils.BeanUtils;

/**
 *
 * @author Alba
 */

    @WebServlet(name = "PerfilController", urlPatterns = {"/PerfilController"})
@MultipartConfig
public class PerfilController extends HttpServlet{

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        String url = "JSP/perfil.jsp";

        if ("actualizarPerfil".equals(accion)) {
            url = accionActualizarPerfil(request);
        } else if ("cambiarPassword".equals(accion)) {
            url = accionCambiarPassword(request);
        }

        request.getRequestDispatcher(url).forward(request, response);
    }

    /**
     * Actualiza los datos editables del perfil.
     * No se puede cambiar email ni NIF.
     */
    private String accionActualizarPerfil(HttpServletRequest request) {
        Connection con = null;
        
        try {
            // 1. Verificar sesión
            HttpSession sesion = request.getSession(false);
            Usuario usuarioSesion = (sesion != null) ? 
                (Usuario) sesion.getAttribute("usuario") : null;

            if (usuarioSesion == null) {
                return "JSP/login.jsp";
            }

            // 2. Cargar datos del formulario
            Usuario usuarioActualizado = new Usuario();
            BeanUtils.populate(usuarioActualizado, request.getParameterMap());
            usuarioActualizado.setIdusuario(usuarioSesion.getIdusuario());

            // 3. Actualizar en BD
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            UsuarioDAO dao = factory.getUsuarioDAO();

            dao.actualizar(usuarioActualizado, con);
            con.commit();

            // 4. Actualizar objeto en sesión
            Usuario usuarioCompleto = dao.buscarPorId(usuarioSesion.getIdusuario(), con);
            sesion.setAttribute("usuario", usuarioCompleto);

            request.setAttribute("mensajeExito", "Perfil actualizado correctamente.");

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { }
            }
            e.printStackTrace();
            request.setAttribute("mensajeError", 
                "Error al actualizar perfil: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        return "JSP/perfil.jsp";
    }

    /**
     * Cambia la contraseña del usuario.
     * Valida que la contraseña actual sea correcta.
     */
    private String accionCambiarPassword(HttpServletRequest request) {
        Connection con = null;
        
        try {
            // 1. Verificar sesión
            HttpSession sesion = request.getSession(false);
            Usuario usuario = (sesion != null) ? 
                (Usuario) sesion.getAttribute("usuario") : null;

            if (usuario == null) {
                return "JSP/login.jsp";
            }

            // 2. Recoger contraseñas
            String passwordActual = request.getParameter("passwordActual");
            String passwordNueva = request.getParameter("passwordNueva");
            String passwordNueva2 = request.getParameter("passwordNueva2");

            // 3. Validar
            if (!passwordNueva.equals(passwordNueva2)) {
                request.setAttribute("mensajeError", "Las contraseñas nuevas no coinciden.");
                return "JSP/perfil.jsp";
            }

            String passwordActualMD5 = MD5.encriptar(passwordActual);
            if (!usuario.getPassword().equals(passwordActualMD5)) {
                request.setAttribute("mensajeError", "La contraseña actual es incorrecta.");
                return "JSP/perfil.jsp";
            }

            // 4. Actualizar contraseña
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            UsuarioDAO dao = factory.getUsuarioDAO();

            String passwordNuevaMD5 = MD5.encriptar(passwordNueva);
            dao.actualizarPassword(usuario.getIdusuario(), passwordNuevaMD5, con);

            con.commit();

            // 5. Actualizar sesión
            usuario.setPassword(passwordNuevaMD5);
            sesion.setAttribute("usuario", usuario);

            request.setAttribute("mensajeExito", "Contraseña actualizada correctamente.");

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { }
            }
            e.printStackTrace();
            request.setAttribute("mensajeError", 
                "Error al cambiar contraseña: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        return "JSP/perfil.jsp";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Perfil Controller - Gestión del perfil de usuario";
    }
    
}

