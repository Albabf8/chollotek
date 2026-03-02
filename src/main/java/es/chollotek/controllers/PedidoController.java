package es.chollotek.controllers;

import es.chollotek.DAO.ConnectionFactory;
import es.chollotek.DAO.LineaPedidoDAO;
import es.chollotek.DAO.PedidoDAO;
import es.chollotek.DAO.ProductoDAO;
import es.chollotek.DAOFactory.MySQLDAOFactory;
import es.chollotek.beans.LineaPedido;
import es.chollotek.beans.Pedido;
import es.chollotek.beans.Producto;
import es.chollotek.beans.Usuario;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
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

        request.getRequestDispatcher(url).forward(request, response);
    }

    /**
     * Finaliza el carrito convirtiéndolo en pedido.
     * Solo usuarios registrados pueden finalizar compras.
     */
private String accionTramitarPedido(HttpServletRequest request) {
    Connection con = null;
    
    try {
        HttpSession sesion = request.getSession(false);
        Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;
        if (usuario == null) {
            request.setAttribute("mensajeError", "Debes iniciar sesión para finalizar la compra.");
            return "JSP/login.jsp";
        }

        con = ConnectionFactory.getConnection();
        con.setAutoCommit(false);

        MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
        PedidoDAO pedidoDAO = factory.getPedidoDAO();
        LineaPedidoDAO lineaDAO = factory.getLineaPedidoDAO();
        ProductoDAO productoDAO = factory.getProductoDAO();

        Pedido carrito = pedidoDAO.buscarCarrito(usuario.getIdusuario(), con);
        if (carrito == null) {
            request.setAttribute("mensajeError", "No tienes productos en el carrito.");
            con.rollback();
            return "JSP/carrito.jsp";
        }

        // Recalcular importe antes de finalizar
        List<LineaPedido> lineas = lineaDAO.listarPorPedido(carrito.getIdpedido(), con);
        BigDecimal subtotal = BigDecimal.ZERO;
        for (LineaPedido linea : lineas) {
            Producto producto = productoDAO.buscarPorId(linea.getIdproducto(), con);
            subtotal = subtotal.add(producto.getPrecio().multiply(new BigDecimal(linea.getCantidad())));
        }
        BigDecimal iva = subtotal.multiply(new BigDecimal("0.21"));
        carrito.setImporte(subtotal);
        carrito.setIva(iva);
        pedidoDAO.actualizar(carrito, con);

        // Finalizar pedido
        pedidoDAO.finalizarPedido(carrito.getIdpedido(), con);
        con.commit();

        request.setAttribute("mensajeExito", "¡Pedido realizado con éxito! Nº pedido: " + carrito.getIdpedido());

    } catch (Exception e) {
        if (con != null) {
            try { con.rollback(); } catch (Exception ex) { }
        }
        e.printStackTrace();
        request.setAttribute("mensajeError", "Error al tramitar el pedido: " + e.getMessage());
        return "JSP/carrito.jsp";
    } finally {
        ConnectionFactory.closeConnection(con);
    }

    return "JSP/pedidoConfirmado.jsp";
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
