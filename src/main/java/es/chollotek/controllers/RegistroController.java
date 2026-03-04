package es.chollotek.controllers;

import es.chollotek.DAO.ConnectionFactory;
import es.chollotek.DAO.LineaPedidoDAO;
import es.chollotek.DAO.PedidoDAO;
import es.chollotek.DAO.UsuarioDAO;
import es.chollotek.DAOFactory.MySQLDAOFactory;
import es.chollotek.beans.LineaPedido;
import es.chollotek.beans.Pedido;
import es.chollotek.beans.Usuario;
import es.chollotek.models.MD5;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.commons.beanutils.BeanUtils;

@WebServlet(name = "RegistroController", urlPatterns = {"/RegistroController"})
@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class RegistroController extends HttpServlet {

    /** Nombre del directorio donde se almacenarán físicamente los avatares subidos */
    private static final String UPLOAD_DIRECTORY = "avatares";

    /**
     * Procesa la solicitud de registro enviada mediante el método POST.
     * Coordina la validación de contraseñas, encriptación, almacenamiento de avatar,
     * inserción en base de datos y la creación de la sesión de usuario.
     * * @param request La petición HTTP que contiene los datos del formulario de registro.
     * @param response La respuesta HTTP para redirección o gestión de errores.
     * @throws ServletException Si ocurre un error específico del servlet.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String url = "JSP/registro.jsp";
        Connection con = null;

        try {
            Usuario nuevoUsuario = new Usuario();
            BeanUtils.populate(nuevoUsuario, request.getParameterMap());

            String password = request.getParameter("password");
            String password2 = request.getParameter("password2");

            if (password == null || password.trim().isEmpty()
                    || !password.equals(password2)) {
                request.setAttribute("mensajeError", "Las contraseñas no coinciden.");
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }

            nuevoUsuario.setPassword(MD5.encriptar(password));

            String nombreAvatar = procesarAvatar(request);
            nuevoUsuario.setAvatar(nombreAvatar != null ? nombreAvatar : "default-avatar.png");

            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            UsuarioDAO dao = factory.getUsuarioDAO();

            if (dao.emailExiste(nuevoUsuario.getEmail(), con)) {
                request.setAttribute("mensajeError", "El email ya está registrado.");
                con.rollback();
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }

            dao.insertar(nuevoUsuario, con);
            con.commit();

            // Crear sesión
            HttpSession sesion = request.getSession(true);
            sesion.setAttribute("usuario", nuevoUsuario);
            sesion.setMaxInactiveInterval(2 * 24 * 60 * 60);

            // Traspasar carrito anónimo a BD (usuario recién registrado, ultimo_acceso = null)
            List<LineaPedido> carritoAnonimo = (List<LineaPedido>) sesion.getAttribute("carritoAnonimo");
            if (carritoAnonimo != null && !carritoAnonimo.isEmpty()) {
                con = ConnectionFactory.getConnection();
                con.setAutoCommit(false);
                traspasarCarritoAnonimo(carritoAnonimo, nuevoUsuario, con);
                con.commit();
                sesion.removeAttribute("carritoAnonimo");
                // Eliminar cookie
                Cookie cookie = new Cookie("carritoActivo", "");
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            url = "FrontController?accion=inicio";

        } catch (Exception e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ex) {
                }
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
     * Traspasa el carrito de compra anónimo a la base de datos tras el registro.
     * Crea un nuevo pedido con estado 'carrito' ('c') vinculado al nuevo usuario e 
     * inserta todas las líneas de producto correspondientes.
     * * @param carritoAnonimo Lista de líneas de pedido almacenadas previamente en la sesión.
     * @param usuario El objeto del usuario recién registrado.
     * @param con Conexión JDBC activa bajo control de transacción.
     * @throws Exception Si ocurre un error durante la inserción de datos.
     */
    private void traspasarCarritoAnonimo(List<LineaPedido> carritoAnonimo,
            Usuario usuario, Connection con) throws Exception {

        MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
        PedidoDAO pedidoDAO = factory.getPedidoDAO();
        LineaPedidoDAO lineaDAO = factory.getLineaPedidoDAO();

        Pedido carrito = new Pedido();
        carrito.setEstado('c');
        carrito.setIdusuario(usuario.getIdusuario());
        carrito.setFecha(new java.util.Date());
        carrito.setImporte(BigDecimal.ZERO);
        carrito.setIva(BigDecimal.ZERO);
        int idCarrito = pedidoDAO.insertar(carrito, con);
        carrito.setIdpedido(idCarrito);

        for (LineaPedido linea : carritoAnonimo) {
            linea.setIdpedido(idCarrito);
            lineaDAO.insertar(linea, con);
        }
    }

    /**
     * Gestiona la recepción y almacenamiento físico de la imagen de avatar.
     * Genera un nombre de archivo único utilizando un timestamp para evitar 
     * conflictos de nombres entre diferentes usuarios.
     * * @param request La petición HTTP que contiene el objeto Part 'avatar'.
     * @return El nombre del archivo guardado o null si no se proporcionó ninguna imagen.
     * @throws IOException Si ocurre un error al escribir el archivo en el disco.
     * @throws ServletException Si la petición no es multipart/form-data.
     */
    private String procesarAvatar(HttpServletRequest request)
            throws IOException, ServletException {

        Part filePart = request.getPart("avatar");
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }

        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;

        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        filePart.write(uploadPath + File.separator + uniqueFileName);
        return uniqueFileName;
    }

    /**
     * Redirige las peticiones GET al método doPost para unificar la lógica de registro.
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
        return "Registro Controller - Registro de nuevos usuarios";
    }
}
