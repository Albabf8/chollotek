package es.chollotek.controllers;

import es.chollotek.DAO.ConnectionFactory;
import es.chollotek.DAO.UsuarioDAO;
import es.chollotek.DAOFactory.MySQLDAOFactory;
import es.chollotek.beans.Usuario;
import es.chollotek.models.MD5;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.commons.beanutils.BeanUtils;

/**
 *
 * @author Alba
 */
@WebServlet(name = "PerfilController", urlPatterns = {"/PerfilController"})
@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class PerfilController extends HttpServlet {

    private static final String UPLOAD_DIRECTORY = "avatares";

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
     * Actualiza los datos editables del perfil. No se puede cambiar email ni
     * NIF.
     */
    /**
     * Procesa la edición de datos del usuario y la subida de un nuevo avatar.
     * Tras la actualización, se refresca el objeto de sesión con los datos
     * limpios de la base de datos.
     *
     * @return URL de destino.
     */
    private String accionActualizarPerfil(HttpServletRequest request) {
        Connection con = null;

        try {
            // 1. Verificar sesión
            HttpSession sesion = request.getSession(false);
            Usuario usuarioSesion = (sesion != null)
                    ? (Usuario) sesion.getAttribute("usuario") : null;

            if (usuarioSesion == null) {
                return "JSP/login.jsp";
            }

            // 2. Cargar datos del formulario
            Usuario usuarioActualizado = new Usuario();
            BeanUtils.populate(usuarioActualizado, request.getParameterMap());
            usuarioActualizado.setIdusuario(usuarioSesion.getIdusuario());

            // 3. Procesar avatar si se ha subido uno nuevo
            Part filePart = request.getPart("avatar");
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String uniqueFileName = System.currentTimeMillis() + "_" + fileName;

                String uploadPath = getServletContext().getRealPath("")
                        + File.separator + UPLOAD_DIRECTORY;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                filePart.write(uploadPath + File.separator + uniqueFileName);
                usuarioActualizado.setAvatar(uniqueFileName);
            } else {
                // Mantener el avatar actual si no se sube uno nuevo
                usuarioActualizado.setAvatar(usuarioSesion.getAvatar());
            }

            // 4. Actualizar en BD
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            UsuarioDAO dao = factory.getUsuarioDAO();

            dao.actualizar(usuarioActualizado, con);
            con.commit();

            // 5. Actualizar objeto en sesión
            Usuario usuarioCompleto = dao.buscarPorId(usuarioSesion.getIdusuario(), con);
            sesion.setAttribute("usuario", usuarioCompleto);

            request.setAttribute("mensajeExito", "Perfil actualizado correctamente.");

        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                }
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
     * Realiza el cambio de contraseña. Compara el hash MD5 de la contraseña
     * actual introducida con el almacenado en el objeto de sesión antes de
     * proceder al cambio. Ambas contraseñas nuevas deben ser idénticas.
     *
     * @return URL de retorno al formulario de perfil con mensajes de estado.
     */
    private String accionCambiarPassword(HttpServletRequest request) {
        Connection con = null;

        try {
            // 1. Verificar sesión
            HttpSession sesion = request.getSession(false);
            Usuario usuario = (sesion != null)
                    ? (Usuario) sesion.getAttribute("usuario") : null;

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
                try {
                    con.rollback();
                } catch (Exception ex) {
                }
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
