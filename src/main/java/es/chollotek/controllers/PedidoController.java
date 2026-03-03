package es.chollotek.controllers;

import es.chollotek.DAO.ConnectionFactory;
import es.chollotek.DAO.LineaPedidoDAO;
import es.chollotek.DAO.PedidoDAO;
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

@WebServlet(name = "PedidoController", urlPatterns = {"/PedidoController"})
public class PedidoController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        String url    = "JSP/carrito.jsp";

        if (accion != null) {
            switch (accion) {
                case "tramitarPedido":
                    url = accionTramitarPedido(request);
                    break;
                case "verDetalle":
                    url = accionVerDetalle(request);
                    break;
                default:
                    url = "JSP/pedidos.jsp";
                    break;
            }
        }

        request.getRequestDispatcher(url).forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");

        if ("verDetalle".equals(accion)) {
            response.sendRedirect(request.getContextPath()
                    + "/FrontController?accion=verPedidos");
            return;
        }

        request.getRequestDispatcher("JSP/pedidos.jsp").forward(request, response);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // VER DETALLE DE UN PEDIDO
    // ═══════════════════════════════════════════════════════════════════════
    private String accionVerDetalle(HttpServletRequest request) {

        Connection con = null;

        try {
            // ── 1. Seguridad: usuario logado ────────────────────────────────
            HttpSession sesion = request.getSession(false);
            Usuario usuario = (sesion != null)
                    ? (Usuario) sesion.getAttribute("usuario") : null;

            if (usuario == null) {
                request.setAttribute("mensajeError",
                        "Debes iniciar sesión para ver tus pedidos.");
                return "JSP/login.jsp";
            }

            // ── 2. Leer y validar parámetro idpedido ────────────────────────
            String idPedidoStr = request.getParameter("idpedido");
            if (idPedidoStr == null || idPedidoStr.trim().isEmpty()) {
                request.setAttribute("mensajeError", "Pedido no especificado.");
                return "JSP/pedidos.jsp";
            }
            int idPedido = Integer.parseInt(idPedidoStr.trim());

            con = ConnectionFactory.getConnection();
            MySQLDAOFactory factory  = MySQLDAOFactory.getInstancia();
            PedidoDAO      pedidoDAO = factory.getPedidoDAO();
            LineaPedidoDAO lineaDAO  = factory.getLineaPedidoDAO();

            // ── 3. Cargar pedido y verificar que pertenece al usuario ────────
            //       Evita que un usuario vea pedidos ajenos aunque envíe
            //       un idpedido diferente en el campo oculto del formulario.
            Pedido pedido = pedidoDAO.buscarPorId(idPedido, con);
            if (pedido == null || pedido.getIdusuario() != usuario.getIdusuario()) {
                request.setAttribute("mensajeError", "Pedido no encontrado.");
                return "JSP/pedidos.jsp";
            }

            // ── 4. Cargar líneas ─────────────────────────────────────────────
            //       listarPorPedido hace JOIN con productos internamente
            //       → linea.getProducto() ya viene relleno, sin bucle extra.
            List<LineaPedido> lineas = lineaDAO.listarPorPedido(idPedido, con);

            // ── 5. Calcular totales ──────────────────────────────────────────
            BigDecimal subtotal = BigDecimal.ZERO;
            for (LineaPedido linea : lineas) {
                Producto p = linea.getProducto();
                if (p != null && p.getPrecio() != null) {
                    subtotal = subtotal.add(
                        p.getPrecio().multiply(new BigDecimal(linea.getCantidad()))
                    );
                }
            }
            BigDecimal iva   = subtotal.multiply(new BigDecimal("0.21"));
            BigDecimal total = subtotal.add(iva);

            // ── 6. Pasar atributos a la vista ────────────────────────────────
            request.setAttribute("pedido",   pedido);
            request.setAttribute("lineas",   lineas);
            request.setAttribute("subtotal", subtotal);
            request.setAttribute("iva",      iva);
            request.setAttribute("total",    total);

        } catch (NumberFormatException e) {
            request.setAttribute("mensajeError", "ID de pedido inválido.");
            return "JSP/pedidos.jsp";
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensajeError",
                    "Error al cargar el pedido: " + e.getMessage());
            return "JSP/pedidos.jsp";
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        return "JSP/detallePedido.jsp";
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TRAMITAR PEDIDO
    // ═══════════════════════════════════════════════════════════════════════
    private String accionTramitarPedido(HttpServletRequest request) {

        Connection con = null;

        try {
            HttpSession sesion = request.getSession(false);
            Usuario usuario = (sesion != null)
                    ? (Usuario) sesion.getAttribute("usuario") : null;

            if (usuario == null) {
                request.setAttribute("mensajeError",
                        "Debes iniciar sesión para finalizar la compra.");
                return "JSP/login.jsp";
            }

            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            MySQLDAOFactory factory  = MySQLDAOFactory.getInstancia();
            PedidoDAO      pedidoDAO = factory.getPedidoDAO();
            LineaPedidoDAO lineaDAO  = factory.getLineaPedidoDAO();

            Pedido carrito = pedidoDAO.buscarCarrito(usuario.getIdusuario(), con);
            if (carrito == null) {
                request.setAttribute("mensajeError",
                        "No tienes productos en el carrito.");
                con.rollback();
                return "JSP/carrito.jsp";
            }

            // listarPorPedido ya trae producto + precio vía JOIN ─────────────
            List<LineaPedido> lineas = lineaDAO.listarPorPedido(
                    carrito.getIdpedido(), con);

            BigDecimal subtotal = BigDecimal.ZERO;
            for (LineaPedido linea : lineas) {
                Producto p = linea.getProducto();
                if (p != null && p.getPrecio() != null) {
                    subtotal = subtotal.add(
                        p.getPrecio().multiply(new BigDecimal(linea.getCantidad()))
                    );
                }
            }

            BigDecimal iva = subtotal.multiply(new BigDecimal("0.21"));
            carrito.setImporte(subtotal);
            carrito.setIva(iva);
            pedidoDAO.actualizar(carrito, con);
            pedidoDAO.finalizarPedido(carrito.getIdpedido(), con);
            con.commit();

            request.setAttribute("mensajeExito",
                    "¡Pedido realizado con éxito! Nº pedido: "
                    + carrito.getIdpedido());

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { /* ignorar */ }
            }
            e.printStackTrace();
            request.setAttribute("mensajeError",
                    "Error al tramitar el pedido: " + e.getMessage());
            return "JSP/carrito.jsp";
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        return "JSP/pedidoConfirmado.jsp";
    }

    @Override
    public String getServletInfo() {
        return "Pedido Controller - Tramitación de pedidos";
    }
}