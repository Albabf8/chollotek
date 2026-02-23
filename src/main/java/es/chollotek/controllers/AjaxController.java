package es.chollotek.controllers;

import es.chollotek.DAO.ConnectionFactory;
import es.chollotek.DAO.LineaPedidoDAO;
import es.chollotek.DAO.UsuarioDAO;
import es.chollotek.DAOFactory.MySQLDAOFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Alba
 */

@WebServlet(name = "AjaxController", urlPatterns = {"/AjaxController"})
public class AjaxController extends HttpServlet{

    /**
     * Procesa peticiones GET y POST de la misma forma.
     */
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

    /**
     * Procesa las peticiones Ajax según la acción solicitada.
     * 
     * @param request petición HTTP con parámetro "accion"
     * @param response respuesta HTTP (siempre JSON)
     */
    private void procesarPeticion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configurar respuesta como JSON con UTF-8
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

    /**
     * Valida si un email ya existe en la base de datos.
     * Usado en el formulario de registro para validación en tiempo real.
     * 
     * Respuesta JSON:
     * {"existe": true} o {"existe": false}
     * 
     * @param request debe contener parámetro "email"
     * @param response respuesta JSON con campo "existe"
     */
    private void validarEmail(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Connection con = null;
        PrintWriter out = response.getWriter();

        try {
            // 1. Obtener email del parámetro
            String email = request.getParameter("email");

            if (email == null || email.trim().isEmpty()) {
                out.print("{\"error\": \"Email no proporcionado\"}");
                return;
            }

            // 2. Conectar a la BD
            con = ConnectionFactory.getConnection();

            // 3. Consultar si existe
            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            UsuarioDAO dao = factory.getUsuarioDAO();
            boolean existe = dao.emailExiste(email.trim(), con);

            // 4. Responder en JSON
            out.print("{\"existe\": " + existe + "}");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\": \"Error al validar email: " + e.getMessage() + "\"}");

        } finally {
            ConnectionFactory.closeConnection(con);
            out.flush();
        }
    }

    /**
     * Calcula la letra del NIF a partir de los 8 dígitos.
     * Usado en el formulario de registro para completar el NIF automáticamente.
     * 
     * Respuesta JSON:
     * {"letra": "Z", "nifCompleto": "12345678Z"}
     * 
     * @param request debe contener parámetro "numeros" (8 dígitos)
     * @param response respuesta JSON con la letra y NIF completo
     */
    private void calcularLetraNIF(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        PrintWriter out = response.getWriter();

        try {
            // 1. Obtener números del NIF
            String numeros = request.getParameter("numeros");

            if (numeros == null || !numeros.matches("[0-9]{8}")) {
                out.print("{\"error\": \"Debe proporcionar 8 dígitos\"}");
                return;
            }

            // 2. Calcular letra según algoritmo oficial del NIF
            String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
            int numero = Integer.parseInt(numeros);
            char letra = letras.charAt(numero % 23);

            // 3. Responder en JSON
            String nifCompleto = numeros + letra;
            out.print("{\"letra\": \"" + letra + "\", \"nifCompleto\": \"" + nifCompleto + "\"}");

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\": \"Error al calcular NIF: " + e.getMessage() + "\"}");

        } finally {
            out.flush();
        }
    }

    /**
     * Modifica la cantidad de un producto en el carrito mediante Ajax.
     * Evita recargar toda la página al aumentar/disminuir cantidad.
     * 
     * Respuesta JSON:
     * {"exito": true, "nuevaCantidad": 3, "subtotal": 59.97}
     * 
     * @param request debe contener parámetros "idlinea" y opcionalmente "idpedido"
     * @param response respuesta JSON con resultado de la operación
     * @param sumar true para aumentar, false para disminuir
     */
    private void modificarCantidadCarrito(HttpServletRequest request, HttpServletResponse response,
                                          boolean sumar) throws IOException {

        Connection con = null;
        PrintWriter out = response.getWriter();

        try {
            // 1. Obtener parámetros
            String idLineaStr = request.getParameter("idlinea");

            if (idLineaStr == null || idLineaStr.trim().isEmpty()) {
                out.print("{\"error\": \"ID de línea no proporcionado\"}");
                return;
            }

            short idLinea = Short.parseShort(idLineaStr);

            // 2. Conectar a la BD e iniciar transacción
            con = ConnectionFactory.getConnection();
            con.setAutoCommit(false);

            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            LineaPedidoDAO dao = factory.getLineaPedidoDAO();

            // 3. Obtener la línea actual (necesitamos buscarla)
            // Aquí necesitarías implementar un método buscarPorId en LineaPedidoDAO
            // Por ahora, asumimos que la cantidad se pasa como parámetro alternativo

            String cantidadActualStr = request.getParameter("cantidadActual");
            if (cantidadActualStr == null) {
                out.print("{\"error\": \"Cantidad actual no proporcionada\"}");
                con.rollback();
                return;
            }

            short cantidadActual = Short.parseShort(cantidadActualStr);
            short nuevaCantidad;

            if (sumar) {
                nuevaCantidad = (short) (cantidadActual + 1);
            } else {
                nuevaCantidad = (short) (cantidadActual - 1);

                // Si llega a 0, eliminar la línea
                if (nuevaCantidad <= 0) {
                    dao.eliminar(idLinea, con);
                    con.commit();
                    out.print("{\"exito\": true, \"eliminado\": true}");
                    return;
                }
            }

            // 4. Actualizar cantidad
            dao.actualizarCantidad(idLinea, nuevaCantidad, con);
            con.commit();

            // 5. Responder con éxito
            out.print("{\"exito\": true, \"nuevaCantidad\": " + nuevaCantidad + "}");

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

    /**
     * Envía una respuesta de error en formato JSON.
     * 
     * @param response respuesta HTTP
     * @param mensaje mensaje de error a enviar
     */
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
