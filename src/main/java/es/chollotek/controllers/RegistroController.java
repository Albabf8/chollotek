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

    @WebServlet(name = "RegistroController", urlPatterns = {"/RegistroController"})
@MultipartConfig(
    maxFileSize = 5 * 1024 * 1024,      // 5MB máximo por archivo
    maxRequestSize = 10 * 1024 * 1024   // 10MB máximo petición completa
)
public class RegistroController extends HttpServlet{

    private static final String UPLOAD_DIRECTORY = "avatares";

    /**
     * Procesa el formulario de registro.
     * Valida datos, encripta contraseña, guarda avatar y crea usuario.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String url = "JSP/registro.jsp";
        Connection con = null;

        try {
            // 1. Crear objeto Usuario y cargar datos del formulario
            Usuario nuevoUsuario = new Usuario();
            BeanUtils.populate(nuevoUsuario, request.getParameterMap());

            // 2. Validaciones básicas del lado servidor
            String password = request.getParameter("password");
            String password2 = request.getParameter("password2");

            if (password == null || password.trim().isEmpty() ||
                !password.equals(password2)) {
                request.setAttribute("mensajeError", "Las contraseñas no coinciden.");
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }

            // 3. Encriptar contraseña en MD5
            String passwordMD5 = MD5.encriptar(password);
            nuevoUsuario.setPassword(passwordMD5);

            // 4. Procesar subida de avatar (si existe)
            String nombreAvatar = procesarAvatar(request);
            if (nombreAvatar != null) {
                nuevoUsuario.setAvatar(nombreAvatar);
            } else {
                nuevoUsuario.setAvatar("default-avatar.png");
            }

            // 5. Obtener conexión e iniciar transacción
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            // 6. Verificar que el email no existe
            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            UsuarioDAO dao = factory.getUsuarioDAO();

            if (dao.emailExiste(nuevoUsuario.getEmail(), con)) {
                request.setAttribute("mensajeError", "El email ya está registrado.");
                con.rollback();
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }

            // 7. Insertar usuario en BD
            dao.insertar(nuevoUsuario, con);
            con.commit();

            // 8. Crear sesión automáticamente (login automático tras registro)
            HttpSession sesion = request.getSession(true);
            sesion.setAttribute("usuario", nuevoUsuario);
            sesion.setMaxInactiveInterval(2 * 24 * 60 * 60); // 2 días

            // 9. Redirigir a página principal con mensaje de éxito
            request.setAttribute("mensajeExito", "¡Registro completado con éxito! Bienvenido/a.");
            url = "FrontController?accion=inicio";

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { }
            }
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error al registrar usuario: " + e.getMessage());
            request.getRequestDispatcher("JSP/registro.jsp").forward(request, response);
            return;
            
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        response.sendRedirect(url);
    }

    /**
     * Procesa la subida del archivo avatar.
     * Guarda el archivo en el servidor con nombre único.
     * 
     * @param request petición HTTP con el archivo multipart
     * @return nombre del archivo guardado, o null si no se subió archivo
     * @throws IOException si hay error al guardar el archivo
     * @throws ServletException si hay error al procesar el multipart
     */
    private String procesarAvatar(HttpServletRequest request) 
            throws IOException, ServletException {
        
        Part filePart = request.getPart("avatar");
        
        if (filePart == null || filePart.getSize() == 0) {
            return null; // No se subió archivo
        }

        // Obtener nombre original del archivo
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        
        // Generar nombre único: timestamp + nombre original
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;

        // Ruta absoluta donde guardar (dentro de webapp/avatares)
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        // Guardar archivo
        String filePath = uploadPath + File.separator + uniqueFileName;
        filePart.write(filePath);

        return uniqueFileName;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Registro Controller - Registro de nuevos usuarios";
    }
    
    
}
