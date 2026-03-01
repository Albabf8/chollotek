package es.chollotek.controllers;

import es.chollotek.DAO.ConnectionFactory;
import es.chollotek.DAO.PedidoDAO;
import es.chollotek.DAOFactory.MySQLDAOFactory;
import es.chollotek.beans.Pedido;
import es.chollotek.beans.Usuario;
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
    @WebServlet(name = "PedidoController", urlPatterns = {"/PedidoController"})

public class PedidoController extends HttpServlet{

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        String url = "JSP/carrito.jsp";

        if ("tramitarPedido".equals(accion)) {
            url = accionTramitarPedido(request);
        }

        response.sendRedirect(url);
    }

    /**
     * Finaliza el carrito convirtiéndolo en pedido.
     * Solo usuarios registrados pueden finalizar compras.
     */
    private String accionTramitarPedido(HttpServletRequest request) {
        Connection con = null;
        
        try {
            // 1. Verificar que el usuario esté logueado
            HttpSession sesion = request.getSession(false);
            Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

            if (usuario == null) {
                request.setAttribute("mensajeError", 
                    "Debes iniciar sesión para finalizar la compra.");
                return "JSP/login.jsp";
            }

            // 2. Obtener conexión e iniciar transacción
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            PedidoDAO dao = factory.getPedidoDAO();

            // 3. Buscar carrito activo
            Pedido carrito = dao.buscarCarrito(usuario.getIdusuario(), con);

            if (carrito == null) {
                request.setAttribute("mensajeError", "No tienes productos en el carrito.");
                con.rollback();
                return "FrontController?accion=verCarrito";
            }

            // 4. Finalizar pedido (cambiar estado 'c' → 'f')
            dao.finalizarPedido(carrito.getIdpedido(), con);

            con.commit();

            // 5. Mensaje de éxito
            request.setAttribute("mensajeExito", 
                "¡Pedido realizado con éxito! Nº pedido: " + carrito.getIdpedido());

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { }
            }
            e.printStackTrace();
            request.setAttribute("mensajeError", 
                "Error al tramitar el pedido: " + e.getMessage());
            return "JSP/carrito.jsp";
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        return "JSP/privadas/pedidoConfirmado.jsp";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Pedido Controller - Tramitación de pedidos";
    }
}
