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
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CarritoController", urlPatterns = {"/CarritoController"})
public class CarritoController extends HttpServlet {

    private static final BigDecimal IVA = new BigDecimal("0.21");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        String url = "FrontController?accion=verCarrito";

        if (accion != null) {
            switch (accion) {
                case "anadir":
                    url = accionAnadir(request, response);
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
                    url = "FrontController?accion=verCarrito";
            }
        }

        response.sendRedirect(url);
    }

    /**
     * Añade un producto al carrito.
     * Si el usuario está logueado, lo guarda en BD.
     * Si es anónimo, lo guarda en sesión con cookie de 2 días.
     */
    private String accionAnadir(HttpServletRequest request, HttpServletResponse response) {
        Connection con = null;

        try {
            String idProductoStr = request.getParameter("idproducto");
            int idProducto = Integer.parseInt(idProductoStr);

            HttpSession sesion = request.getSession(false);
            Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

            if (usuario != null) {
                // USUARIO REGISTRADO: guardar en BD
                con = ConnectionFactory.getConnection();
                con.setAutoCommit(false);

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

                LineaPedido lineaExistente = lineaDAO.buscarLinea(carrito.getIdpedido(), idProducto, con);

                if (lineaExistente != null) {
                    lineaDAO.actualizarCantidad(lineaExistente.getIdlinea(), lineaExistente.getCantidad() + 1, con);
                } else {
                    LineaPedido nuevaLinea = new LineaPedido();
                    nuevaLinea.setIdpedido(carrito.getIdpedido());
                    nuevaLinea.setIdproducto(idProducto);
                    nuevaLinea.setCantidad(1);
                    lineaDAO.insertar(nuevaLinea, con);
                }

                recalcularImporteCarrito(carrito.getIdpedido(), con);
                con.commit();

            } else {
                // USUARIO ANÓNIMO: guardar en sesión
                HttpSession sesionAnonima = request.getSession(true);
                List<LineaPedido> carritoSesion = (List<LineaPedido>) sesionAnonima.getAttribute("carritoAnonimo");
                if (carritoSesion == null) {
                    carritoSesion = new ArrayList<LineaPedido>();
                }

                boolean encontrado = false;
                for (LineaPedido l : carritoSesion) {
                    if (l.getIdproducto() == idProducto) {
                        l.setCantidad(l.getCantidad() + 1);
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    con = ConnectionFactory.getConnection();
                    MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
                    ProductoDAO productoDAO = factory.getProductoDAO();
                    Producto producto = productoDAO.buscarPorId(idProducto, con);

                    LineaPedido nueva = new LineaPedido();
                    nueva.setIdproducto(idProducto);
                    nueva.setCantidad(1);
                    nueva.setProducto(producto);
                    carritoSesion.add(nueva);
                }

                sesionAnonima.setAttribute("carritoAnonimo", carritoSesion);

                // Cookie de 2 días
                Cookie cookie = new Cookie("carritoActivo", "true");
                cookie.setMaxAge(2 * 24 * 60 * 60);
                cookie.setPath("/");
                response.addCookie(cookie);
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
     * Aumenta la cantidad de un producto en el carrito.
     * Para usuario logueado: actualiza en BD.
     * Para usuario anónimo: actualiza en sesión.
     */
    private String accionSumarCantidad(HttpServletRequest request) {
        Connection con = null;

        try {
            HttpSession sesion = request.getSession(false);
            Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

            if (usuario != null) {
                // Usuario registrado: actualizar en BD
                String idLineaStr = request.getParameter("idlinea");
                int idLinea = Integer.parseInt(idLineaStr);

                con = ConnectionFactory.getConnection();
                con.setAutoCommit(false);

                MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
                LineaPedidoDAO dao = factory.getLineaPedidoDAO();
                PedidoDAO pedidoDAO = factory.getPedidoDAO();

                Pedido carrito = pedidoDAO.buscarCarrito(usuario.getIdusuario(), con);
                if (carrito != null) {
                    List<LineaPedido> lineas = dao.listarPorPedido(carrito.getIdpedido(), con);
                    for (LineaPedido l : lineas) {
                        if (l.getIdlinea() == idLinea) {
                            dao.actualizarCantidad(idLinea, l.getCantidad() + 1, con);
                            recalcularImporteCarrito(carrito.getIdpedido(), con);
                            break;
                        }
                    }
                }
                con.commit();

            } else if (sesion != null) {
                // Usuario anónimo: actualizar en sesión
                String idProductoStr = request.getParameter("idproducto");
                int idProducto = Integer.parseInt(idProductoStr);

                List<LineaPedido> carritoSesion = (List<LineaPedido>) sesion.getAttribute("carritoAnonimo");
                if (carritoSesion != null) {
                    for (LineaPedido l : carritoSesion) {
                        if (l.getIdproducto() == idProducto) {
                            l.setCantidad(l.getCantidad() + 1);
                            break;
                        }
                    }
                    sesion.setAttribute("carritoAnonimo", carritoSesion);
                }
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
     * Disminuye la cantidad de un producto en el carrito.
     * Si llega a 0, elimina la línea.
     */
    private String accionRestarCantidad(HttpServletRequest request) {
        Connection con = null;

        try {
            HttpSession sesion = request.getSession(false);
            Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

            if (usuario != null) {
                // Usuario registrado: actualizar en BD
                String idLineaStr = request.getParameter("idlinea");
                int idLinea = Integer.parseInt(idLineaStr);

                con = ConnectionFactory.getConnection();
                con.setAutoCommit(false);

                MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
                LineaPedidoDAO dao = factory.getLineaPedidoDAO();
                PedidoDAO pedidoDAO = factory.getPedidoDAO();

                Pedido carrito = pedidoDAO.buscarCarrito(usuario.getIdusuario(), con);
                if (carrito != null) {
                    List<LineaPedido> lineas = dao.listarPorPedido(carrito.getIdpedido(), con);
                    for (LineaPedido l : lineas) {
                        if (l.getIdlinea() == idLinea) {
                            if (l.getCantidad() <= 1) {
                                dao.eliminar(idLinea, con);
                            } else {
                                dao.actualizarCantidad(idLinea, l.getCantidad() - 1, con);
                            }
                            recalcularImporteCarrito(carrito.getIdpedido(), con);
                            break;
                        }
                    }
                }
                con.commit();

            } else if (sesion != null) {
                // Usuario anónimo: actualizar en sesión
                String idProductoStr = request.getParameter("idproducto");
                int idProducto = Integer.parseInt(idProductoStr);

                List<LineaPedido> carritoSesion = (List<LineaPedido>) sesion.getAttribute("carritoAnonimo");
                if (carritoSesion != null) {
                    java.util.Iterator<LineaPedido> it = carritoSesion.iterator();
                    while (it.hasNext()) {
                        LineaPedido l = it.next();
                        if (l.getIdproducto() == idProducto) {
                            if (l.getCantidad() <= 1) {
                                it.remove();
                            } else {
                                l.setCantidad(l.getCantidad() - 1);
                            }
                            break;
                        }
                    }
                    sesion.setAttribute("carritoAnonimo", carritoSesion);
                }
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
     * Elimina un producto del carrito.
     */
    private String accionEliminarProducto(HttpServletRequest request) {
        Connection con = null;

        try {
            HttpSession sesion = request.getSession(false);
            Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

            if (usuario != null) {
                // Usuario registrado: eliminar de BD
                String idLineaStr = request.getParameter("idlinea");
                int idLinea = Integer.parseInt(idLineaStr);

                con = ConnectionFactory.getConnection();
                con.setAutoCommit(false);

                MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
                LineaPedidoDAO dao = factory.getLineaPedidoDAO();
                PedidoDAO pedidoDAO = factory.getPedidoDAO();

                Pedido carrito = pedidoDAO.buscarCarrito(usuario.getIdusuario(), con);
                dao.eliminar(idLinea, con);
                if (carrito != null) {
                    recalcularImporteCarrito(carrito.getIdpedido(), con);
                }
                con.commit();

            } else if (sesion != null) {
                // Usuario anónimo: eliminar de sesión
                String idProductoStr = request.getParameter("idproducto");
                int idProducto = Integer.parseInt(idProductoStr);

                List<LineaPedido> carritoSesion = (List<LineaPedido>) sesion.getAttribute("carritoAnonimo");
                if (carritoSesion != null) {
                    java.util.Iterator<LineaPedido> it = carritoSesion.iterator();
                    while (it.hasNext()) {
                        LineaPedido l = it.next();
                        if (l.getIdproducto() == idProducto) {
                            it.remove();
                            break;
                        }
                    }
                    sesion.setAttribute("carritoAnonimo", carritoSesion);
                }
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
     * Vacía el carrito por completo.
     */
    private String accionVaciarCarrito(HttpServletRequest request) {
        Connection con = null;

        try {
            HttpSession sesion = request.getSession(false);
            Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

            if (usuario != null) {
                // Usuario registrado: vaciar de BD
                con = ConnectionFactory.getConnection();
                con.setAutoCommit(false);

                MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
                PedidoDAO pedidoDAO = factory.getPedidoDAO();
                LineaPedidoDAO lineaDAO = factory.getLineaPedidoDAO();

                Pedido carrito = pedidoDAO.buscarCarrito(usuario.getIdusuario(), con);
                if (carrito != null) {
                    lineaDAO.eliminarPorPedido(carrito.getIdpedido(), con);
                    pedidoDAO.eliminar(carrito.getIdpedido(), con);
                }
                con.commit();

            } else if (sesion != null) {
                // Usuario anónimo: vaciar sesión
                sesion.removeAttribute("carritoAnonimo");
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
     */
    private void recalcularImporteCarrito(int idPedido, Connection con) throws Exception {
        MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
        LineaPedidoDAO lineaDAO = factory.getLineaPedidoDAO();
        ProductoDAO productoDAO = factory.getProductoDAO();
        PedidoDAO pedidoDAO = factory.getPedidoDAO();

        List<LineaPedido> lineas = lineaDAO.listarPorPedido(idPedido, con);

        BigDecimal subtotal = BigDecimal.ZERO;
        for (LineaPedido linea : lineas) {
            Producto producto = productoDAO.buscarPorId(linea.getIdproducto(), con);
            subtotal = subtotal.add(producto.getPrecio().multiply(new BigDecimal(linea.getCantidad())));
        }

        BigDecimal ivaCalculado = subtotal.multiply(IVA);

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