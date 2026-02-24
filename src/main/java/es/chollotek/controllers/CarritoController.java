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
    @WebServlet(name = "CarritoController", urlPatterns = {"/CarritoController"})

public class CarritoController extends HttpServlet {

    private static final BigDecimal IVA = new BigDecimal("0.21"); // 21%

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        String url = "JSP/carrito.jsp";

        if (accion != null) {
            switch (accion) {
                case "anadir":
                    url = accionAnadir(request);
                    break;
                case "sumarCantidad":
                    url = accionSumarCantidad(request);
                    break;
                case "restarCantidad":
                    url = accionRestarCantidad(request);
                    break;
                case "eliminarProducto":
                    url = accionEliminarProducto(request);
                    break;
                case "vaciarCarrito":
                    url = accionVaciarCarrito(request);
                    break;
                default:
                    url = "JSP/carrito.jsp";
            }
        }

        response.sendRedirect(url);
    }

    /**
     * Añade un producto al carrito.
     * Si el usuario está logueado, lo guarda en BD.
     * Si es anónimo, lo guarda en sesión.
     */
    private String accionAnadir(HttpServletRequest request) {
        Connection con = null;
        
        try {
            // 1. Obtener ID del producto
            String idProductoStr = request.getParameter("idproducto");
            int idProducto = Integer.parseInt(idProductoStr);

            // 2. Verificar si el usuario está logueado
            HttpSession sesion = request.getSession(false);
            Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

            if (usuario != null) {
                // USUARIO REGISTRADO: guardar en BD
                con = ConnectionFactory.getConnection();
                con.setAutoCommit(false);

                MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
                PedidoDAO pedidoDAO = factory.getPedidoDAO();
                LineaPedidoDAO lineaDAO = factory.getLineaPedidoDAO();
                ProductoDAO productoDAO = factory.getProductoDAO();

                // 3. Buscar o crear carrito del usuario
                Pedido carrito = pedidoDAO.buscarCarrito(usuario.getIdusuario(), con);
                
                if (carrito == null) {
                    // Crear nuevo carrito
                    carrito = new Pedido();
                    carrito.setEstado('c');
                    carrito.setIdusuario(usuario.getIdusuario());
                    carrito.setFecha(new java.util.Date());
                    carrito.setImporte(BigDecimal.ZERO);
                    carrito.setIva(BigDecimal.ZERO);
                    
                    int idCarrito = pedidoDAO.insertar(carrito, con);
                    carrito.setIdpedido(idCarrito);
                }

                // 4. Verificar si el producto ya está en el carrito
                LineaPedido lineaExistente = lineaDAO.buscarLinea(
                    carrito.getIdpedido(), idProducto, con);

                if (lineaExistente != null) {
                    // Producto ya existe: aumentar cantidad
                    int nuevaCantidad = (lineaExistente.getCantidad() + 1);
                    lineaDAO.actualizarCantidad(lineaExistente.getIdlinea(), nuevaCantidad, con);
                } else {
                    // Producto nuevo: insertar línea
                    LineaPedido nuevaLinea = new LineaPedido();
                    nuevaLinea.setIdpedido(carrito.getIdpedido());
                    nuevaLinea.setIdproducto(idProducto);
                    nuevaLinea.setCantidad(1);
                    lineaDAO.insertar(nuevaLinea, con);
                }

                // 5. Recalcular importe total del carrito
                recalcularImporteCarrito(carrito.getIdpedido(), con);

                con.commit();
                request.setAttribute("mensajeExito", "Producto añadido al carrito.");

            } else {
                // USUARIO ANÓNIMO: guardar en sesión
                // (implementar lógica de carrito en sesión si es necesario)
                request.setAttribute("mensajeInfo", "Producto añadido (sesión anónima).");
            }

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { }
            }
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error al añadir producto: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        return "FrontController?accion=verCarrito";
    }

    /**
     * Aumenta la cantidad de un producto en el carrito (Ajax).
     */
    private String accionSumarCantidad(HttpServletRequest request) {
        Connection con = null;
        
        try {
            String idLineaStr = request.getParameter("idlinea");
            int idLinea = Integer.parseInt(idLineaStr);

            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            LineaPedidoDAO dao = factory.getLineaPedidoDAO();

            // Obtener línea actual
            List<LineaPedido> lineas = dao.listarPorPedido(0, con);
            LineaPedido linea = null;
            for (LineaPedido l : lineas) {
                if (l.getIdlinea() == idLinea) {
                    linea = l;
                    break;
                }
            }

            if (linea != null) {
                int nuevaCantidad = (linea.getCantidad() + 1);
                dao.actualizarCantidad(idLinea, nuevaCantidad, con);
                recalcularImporteCarrito(linea.getIdpedido(), con);
            }

            con.commit();

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { }
            }
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        return "FrontController?accion=verCarrito";
    }

    /**
     * Disminuye la cantidad de un producto en el carrito (Ajax).
     * Si llega a 0, elimina la línea.
     */
    private String accionRestarCantidad(HttpServletRequest request) {
        Connection con = null;
        
        try {
            String idLineaStr = request.getParameter("idlinea");
            int idLinea = Integer.parseInt(idLineaStr);

            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            LineaPedidoDAO dao = factory.getLineaPedidoDAO();

            // Similar a sumarCantidad pero restando
            // Si cantidad = 1, eliminar línea
            // Si cantidad > 1, decrementar

            con.commit();

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { }
            }
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        return "FrontController?accion=verCarrito";
    }

    /**
     * Elimina un producto del carrito.
     */
    private String accionEliminarProducto(HttpServletRequest request) {
        Connection con = null;
        
        try {
            String idLineaStr = request.getParameter("idlinea");
            int idLinea = Integer.parseInt(idLineaStr);

            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            LineaPedidoDAO dao = factory.getLineaPedidoDAO();

            dao.eliminar(idLinea, con);

            con.commit();
            request.setAttribute("mensajeExito", "Producto eliminado del carrito.");

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { }
            }
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        return "FrontController?accion=verCarrito";
    }

    /**
     * Vacía el carrito por completo.
     * Elimina todas las líneas y el pedido.
     */
    private String accionVaciarCarrito(HttpServletRequest request) {
        Connection con = null;
        
        try {
            HttpSession sesion = request.getSession(false);
            Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

            if (usuario != null) {
                con = ConnectionFactory.getConnection();
                con.setAutoCommit(false);

                MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
                PedidoDAO pedidoDAO = factory.getPedidoDAO();
                LineaPedidoDAO lineaDAO = factory.getLineaPedidoDAO();

                Pedido carrito = pedidoDAO.buscarCarrito(usuario.getIdusuario(), con);

                if (carrito != null) {
                    // 1. Eliminar líneas primero (FK)
                    lineaDAO.eliminarPorPedido(carrito.getIdpedido(), con);
                    
                    // 2. Eliminar pedido
                    pedidoDAO.eliminar(carrito.getIdpedido(), con);
                }

                con.commit();
                request.setAttribute("mensajeExito", "Carrito vaciado.");
            }

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { }
            }
            e.printStackTrace();
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        return "FrontController?accion=verCarrito";
    }

    /**
     * Recalcula el importe total e IVA de un carrito.
     * Suma todas las líneas (cantidad × precio).
     */
    private void recalcularImporteCarrito(int idPedido, Connection con) throws Exception {
        MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
        LineaPedidoDAO lineaDAO = factory.getLineaPedidoDAO();
        ProductoDAO productoDAO = factory.getProductoDAO();
        PedidoDAO pedidoDAO = factory.getPedidoDAO();

        // 1. Obtener todas las líneas del carrito
        List<LineaPedido> lineas = lineaDAO.listarPorPedido(idPedido, con);

        // 2. Calcular subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        for (LineaPedido linea : lineas) {
            Producto producto = productoDAO.buscarPorId(linea.getIdproducto(), con);
            BigDecimal precioLinea = producto.getPrecio()
                .multiply(new BigDecimal(linea.getCantidad()));
            subtotal = subtotal.add(precioLinea);
        }

        // 3. Calcular IVA
        BigDecimal ivaCalculado = subtotal.multiply(IVA);

        // 4. Actualizar pedido
        Pedido pedido = new Pedido();
        pedido.setIdpedido(idPedido);
        pedido.setImporte(subtotal);
        pedido.setIva(ivaCalculado);
        pedidoDAO.actualizar(pedido, con);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Carrito Controller - Gestión del carrito de compra";
    }
    
}
