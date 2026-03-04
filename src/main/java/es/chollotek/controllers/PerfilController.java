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

    //Directorio físico donde se almacenarán las imágenes de avatar
    private static final String UPLOAD_DIRECTORY = "avatares";

    /**
     * Procesa las peticiones POST delegando la lógica según el parámetro 'accion'.
     * Permite actualizar los datos básicos del perfil o proceder al cambio de contraseña.
     * * @param request La petición HTTP con los datos del formulario.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error interno en el servlet.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
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
     * Procesa la actualización de los datos editables del usuario y la subida de avatar.
     * Implementa el requisito de no permitir el cambio de Email ni NIF. Utiliza transacciones
     * para asegurar la persistencia y refresca el objeto de usuario en la sesión HTTP
     * tras la operación exitosa.
     * * @param request La petición HTTP con los campos del perfil y el archivo Part 'avatar'.
     * @return URL de retorno a la vista de perfil ("JSP/perfil.jsp") con mensajes de estado.
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
     * Realiza el cambio de contraseña del usuario validando la seguridad.
     * Compara el hash MD5 de la contraseña actual introducida con el almacenado.
     * Exige que las dos repeticiones de la nueva contraseña sean idénticas.
     * * @param request La petición con 'passwordActual', 'passwordNueva' y 'passwordNueva2'.
     * @return URL de retorno a la vista de perfil ("JSP/perfil.jsp") con mensajes de estado.
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

    /**
     * Redirige las peticiones GET al método doPost para unificar la gestión de peticiones.
     * * @param request La petición HTTP.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error en el servlet.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
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
