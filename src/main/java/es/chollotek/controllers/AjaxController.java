package es.chollotek.controllers;

import es.chollotek.DAO.ConnectionFactory;
import es.chollotek.DAO.LineaPedidoDAO;
import es.chollotek.DAO.UsuarioDAO;
import es.chollotek.DAOFactory.MySQLDAOFactory;
import es.chollotek.beans.LineaPedido;
import es.chollotek.beans.Usuario;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AjaxController", urlPatterns = {"/AjaxController"})
public class AjaxController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarPeticion(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procesarPeticion(request, response);
    }

    private void procesarPeticion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String accion = request.getParameter("accion");

        if (accion != null) {
            switch (accion) {
                case "emailExiste":
                    validarEmail(request, response);
                    break;
                case "calcularNIF":
                    calcularLetraNIF(request, response);
                    break;
                case "sumarCantidad":
                    modificarCantidadCarrito(request, response, true);
                    break;
                case "restarCantidad":
                    modificarCantidadCarrito(request, response, false);
                    break;
                default:
                    enviarError(response, "Acción no reconocida");
                    break;
            }
        } else {
            enviarError(response, "Parámetro 'accion' no especificado");
        }
    }

    // ═══════════════════════════════════════════════════
    // VALIDAR EMAIL
    // ═══════════════════════════════════════════════════

    private void validarEmail(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Connection con = null;
        PrintWriter out = response.getWriter();

        try {
            String email = request.getParameter("email");

            if (email == null || email.trim().isEmpty()) {
                out.print("{\"error\": \"Email no proporcionado\"}");
                return;
            }

            con = ConnectionFactory.getConnection();
            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            UsuarioDAO dao = factory.getUsuarioDAO();
            boolean existe = dao.emailExiste(email.trim(), con);

            out.print("{\"existe\": " + existe + "}");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\": \"Error al validar email: " + e.getMessage() + "\"}");
        } finally {
            ConnectionFactory.closeConnection(con);
            out.flush();
        }
    }

    // ═══════════════════════════════════════════════════
    // CALCULAR LETRA NIF
    // ═══════════════════════════════════════════════════

    private void calcularLetraNIF(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        PrintWriter out = response.getWriter();

        try {
            String numeros = request.getParameter("numeros");

            if (numeros == null || !numeros.matches("[0-9]{8}")) {
                out.print("{\"error\": \"Debe proporcionar 8 dígitos\"}");
                return;
            }

            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
            int numero = Integer.parseInt(numeros);
            char letra = letras.charAt(numero % 23);
            String nifCompleto = numeros + letra;

            out.print("{\"letra\": \"" + letra + "\", \"nifCompleto\": \"" + nifCompleto + "\"}");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\": \"Error al calcular NIF: " + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }

    // ═══════════════════════════════════════════════════
    // MODIFICAR CANTIDAD (SUMAR / RESTAR)
    // ═══════════════════════════════════════════════════

    private void modificarCantidadCarrito(HttpServletRequest request, HttpServletResponse response,
            boolean sumar) throws IOException {

        Connection con = null;
        PrintWriter out = response.getWriter();

        try {
            String cantidadActualStr = request.getParameter("cantidadActual");
            if (cantidadActualStr == null) {
                out.print("{\"error\": \"Cantidad actual no proporcionada\"}");
                return;
            }

            short cantidadActual = Short.parseShort(cantidadActualStr);

            HttpSession sesion = request.getSession(false);
            Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

            if (usuario != null) {
                // ── USUARIO REGISTRADO: actualizar en BD ──
                String idLineaStr = request.getParameter("idlinea");
                if (idLineaStr == null) {
                    out.print("{\"error\": \"ID de línea no proporcionado\"}");
                    return;
                }

                short idLinea = Short.parseShort(idLineaStr);
                con = ConnectionFactory.getConnection();
                con.setAutoCommit(false);

                MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
                LineaPedidoDAO dao = factory.getLineaPedidoDAO();

                if (!sumar && cantidadActual <= 1) {
                    dao.eliminar(idLinea, con);
                    con.commit();
                    out.print("{\"exito\": true, \"eliminado\": true}");
                } else {
                    short nuevaCantidad = (short) (sumar ? cantidadActual + 1 : cantidadActual - 1);
                    dao.actualizarCantidad(idLinea, nuevaCantidad, con);
                    con.commit();
                    out.print("{\"exito\": true, \"nuevaCantidad\": " + nuevaCantidad + "}");
                }

            } else if (sesion != null) {
                // ── USUARIO ANÓNIMO: actualizar en sesión ──
                String idProductoStr = request.getParameter("idproducto");
                if (idProductoStr == null) {
                    out.print("{\"error\": \"ID de producto no proporcionado\"}");
                    return;
                }

                int idProducto = Integer.parseInt(idProductoStr);
                List<LineaPedido> carritoSesion = (List<LineaPedido>) sesion.getAttribute("carritoAnonimo");

                if (carritoSesion == null) {
                    out.print("{\"error\": \"Carrito no encontrado en sesión\"}");
                    return;
                }

                if (!sumar && cantidadActual <= 1) {
                    // Eliminar la línea del carrito
                    Iterator<LineaPedido> it = carritoSesion.iterator();
                    while (it.hasNext()) {
                        if (it.next().getIdproducto() == idProducto) {
                            it.remove();
                            break;
                        }
                    }
                    sesion.setAttribute("carritoAnonimo", carritoSesion);
                    out.print("{\"exito\": true, \"eliminado\": true}");

                } else {
                    short nuevaCantidad = (short) (sumar ? cantidadActual + 1 : cantidadActual - 1);
                    for (LineaPedido l : carritoSesion) {
                        if (l.getIdproducto() == idProducto) {
                            l.setCantidad(nuevaCantidad);
                            break;
                        }
                    }
                    sesion.setAttribute("carritoAnonimo", carritoSesion);
                    out.print("{\"exito\": true, \"nuevaCantidad\": " + nuevaCantidad + "}");
                }

            } else {
                out.print("{\"error\": \"Sesión no encontrada\"}");
            }

        } catch (NumberFormatException e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { }
            }
            out.print("{\"error\": \"Parámetros inválidos\"}");
        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { }
            }
            e.printStackTrace();
            out.print("{\"error\": \"Error al modificar cantidad: " + e.getMessage() + "\"}");
        } finally {
            ConnectionFactory.closeConnection(con);
            out.flush();
        }
    }

    private void enviarError(HttpServletResponse response, String mensaje) throws IOException {
        PrintWriter out = response.getWriter();
        out.print("{\"error\": \"" + mensaje + "\"}");
        out.flush();
    }

    @Override
    public String getServletInfo() {
        return "Ajax Controller - Gestión de peticiones asíncronas";
    }
}