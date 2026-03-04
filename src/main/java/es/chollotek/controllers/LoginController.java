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
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {

    /**
     * Procesa las peticiones de inicio de sesión mediante el método POST.
     * Valida el email y la contraseña (MD5), actualiza la fecha de último acceso
     * y gestiona la transferencia del carrito de la sesión a la base de datos.
     * * @param request La petición HTTP con credenciales y parámetro de origen.
     * @param response La respuesta HTTP para redirección o gestión de cookies.
     * @throws ServletException Si ocurre un error interno en el servlet.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String url = "JSP/login.jsp";
        Connection con = null;

        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // Validación de campos obligatorios
            if (email == null || email.trim().isEmpty()
                    || password == null || password.trim().isEmpty()) {
                request.setAttribute("mensajeError", "Todos los campos son obligatorios.");
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }

            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            UsuarioDAO dao = factory.getUsuarioDAO();

            Usuario usuario = dao.buscarPorEmail(email.trim(), con);

            // Validación de existencia de usuario
            if (usuario == null) {
                request.setAttribute("mensajeError", "Email o contraseña incorrectos.");
                request.setAttribute("emailIntroducido", email);
                con.rollback();
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }

            // Validación de contraseña encriptada
            String passwordMD5 = MD5.encriptar(password);
            if (!usuario.getPassword().equals(passwordMD5)) {
                request.setAttribute("mensajeError", "Email o contraseña incorrectos.");
                request.setAttribute("emailIntroducido", email);
                con.rollback();
                request.getRequestDispatcher(url).forward(request, response);
                return;
            }

            // Login exitoso: actualizar último acceso
            dao.actualizarUltimoAcceso(usuario.getIdusuario(), con);
            con.commit();

            // Crear sesión
            HttpSession sesion = request.getSession(true);
            sesion.setAttribute("usuario", usuario);
            sesion.setMaxInactiveInterval(2 * 24 * 60 * 60);

            // Traspasar carrito anónimo si existe
            // ultimo_acceso era null ANTES de actualizarUltimoAcceso → primera vez = registro
            // ultimo_acceso NO era null → tiene cuenta, fusionar carrito anónimo con BD
            List<LineaPedido> carritoAnonimo = (List<LineaPedido>) sesion.getAttribute("carritoAnonimo");
            if (carritoAnonimo != null && !carritoAnonimo.isEmpty()) {
                con = ConnectionFactory.getConnection();
                con.setAutoCommit(false);
                traspasarCarritoAnonimo(carritoAnonimo, usuario, con);
                con.commit();
                sesion.removeAttribute("carritoAnonimo");
                // Eliminar cookie
                Cookie cookie = new Cookie("carritoActivo", "");
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }

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

        response.sendRedirect(url);
    }

/**
     * Fusiona el carrito almacenado en la sesión con el carrito del usuario en la base de datos.
     * Si el usuario no posee un pedido abierto (estado 'c'), se crea uno nuevo.
     * En caso de productos coincidentes, se suman las cantidades.
     * * @param carritoAnonimo Lista de líneas de pedido recuperadas de la sesión.
     * @param usuario El objeto usuario autenticado.
     * @param con Conexión JDBC activa bajo transacción.
     * @throws Exception Si ocurre un error en la persistencia de datos.
     */
    private void traspasarCarritoAnonimo(List<LineaPedido> carritoAnonimo,
            Usuario usuario, Connection con) throws Exception {

        MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
        PedidoDAO pedidoDAO = factory.getPedidoDAO();
        LineaPedidoDAO lineaDAO = factory.getLineaPedidoDAO();

        Pedido carrito = pedidoDAO.buscarCarrito(usuario.getIdusuario(), con);
        if (carrito == null) {
            carrito = new Pedido();
            carrito.setEstado('c');
            carrito.setIdusuario(usuario.getIdusuario());
            carrito.setFecha(new java.util.Date());
            carrito.setImporte(BigDecimal.ZERO);
            carrito.setIva(BigDecimal.ZERO);
            int idCarrito = pedidoDAO.insertar(carrito, con);
            carrito.setIdpedido(idCarrito);
        }

        for (LineaPedido lineaAnonima : carritoAnonimo) {
            LineaPedido lineaExistente = lineaDAO.buscarLinea(
                    carrito.getIdpedido(), lineaAnonima.getIdproducto(), con);
            if (lineaExistente != null) {
                lineaDAO.actualizarCantidad(lineaExistente.getIdlinea(),
                        lineaExistente.getCantidad() + lineaAnonima.getCantidad(), con);
            } else {
                lineaAnonima.setIdpedido(carrito.getIdpedido());
                lineaDAO.insertar(lineaAnonima, con);
            }
        }
    }

    /**
     * Redirige las peticiones GET al método doPost para unificar la lógica de acceso.
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
        return "Login Controller - Autenticación de usuarios";
    }
}