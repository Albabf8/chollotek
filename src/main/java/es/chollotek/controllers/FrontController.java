package es.chollotek.controllers;

import es.chollotek.DAO.ConnectionFactory;
import es.chollotek.beans.Producto;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import es.chollotek.DAO.ProductoDAO;
import es.chollotek.DAOFactory.MySQLDAOFactory;
import java.math.BigDecimal;
import java.sql.Connection;

/**
 *
 * @author Alba
 */
@WebServlet(name = "FrontController", urlPatterns = {"/FrontController"})
public class FrontController extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

            doPost(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String url = "index.jsp";
        String accion = request.getParameter("accion");

        if (accion != null) {
            switch (accion) {

                // ═══════════════════════════════════════════════════════
                // INICIO - Página principal con productos aleatorios
                // ═══════════════════════════════════════════════════════
                case "inicio":
                    url = accionInicio(request);
                    break;

                // ═══════════════════════════════════════════════════════
                // FILTRAR - Búsqueda avanzada con múltiples criterios
                // ═══════════════════════════════════════════════════════
                case "filtrar":
                    url = accionFiltrar(request);
                    break;

                // ═══════════════════════════════════════════════════════
                // BUSCAR - Búsqueda simple por texto
                // ═══════════════════════════════════════════════════════
                case "buscar":
                    url = accionBuscar(request);
                    break;

                // ═══════════════════════════════════════════════════════
                // CARRITO - Visualizar y gestionar carrito
                // ═══════════════════════════════════════════════════════
                case "verCarrito":
                    url = "JSP/carrito.jsp";
                    break;

                case "anadir":
                case "sumarCantidad":
                case "restarCantidad":
                case "vaciarCarrito":
                case "eliminarProducto":
                    // Estas acciones se delegan al CarritoController
                    url = "CarritoController";
                    break;

                // ═══════════════════════════════════════════════════════
                // PEDIDOS - Tramitar compra
                // ═══════════════════════════════════════════════════════
                case "tramitarPedido":
                    url = accionTramitarPedido(request);
                    break;

                // ═══════════════════════════════════════════════════════
                // USUARIO - Login, registro, perfil
                // ═══════════════════════════════════════════════════════
                case "verRegistro":
                    url = "JSP/registro.jsp";
                    break;

                case "verLogin":
                    url = "JSP/login.jsp";
                    break;

                case "registrarUsuario":
                    url = "RegistroController";
                    break;

                case "verPerfil":
                    url = "JSP/privadas/perfil.jsp";
                    break;

                case "verPedidos":
                    url = "JSP/privadas/pedidos.jsp";
                    break;

                case "logout":
                    request.getSession().invalidate();
                    url = "index.jsp";
                    break;

                // ═══════════════════════════════════════════════════════
                // DEFAULT - Página de inicio por defecto
                // ═══════════════════════════════════════════════════════
                default:
                    url = accionInicio(request);
                    break;
            }
        } else {
            // Si no viene acción, mostrar inicio
            url = accionInicio(request);
        }

        request.getRequestDispatcher(url).forward(request, response);
    }

    // ═════════════════════════════════════════════════════════════════
    // MÉTODOS AUXILIARES - Cada acción en su propio método
    // ═════════════════════════════════════════════════════════════════

    /**
     * Muestra la página de inicio con productos aleatorios (landing page).
     * Carga 8 productos aleatorios para mostrar variedad en cada visita.
     * 
     * @param request petición HTTP
     * @return URL de la vista a mostrar
     */
    private String accionInicio(HttpServletRequest request) {
        Connection con = null;
        try {
            // 1. Obtener conexión del pool
            con = ConnectionFactory.getConnection();

            // 2. Obtener DAO
            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            ProductoDAO pdao = factory.getProductoDAO();

            // 3. Obtener productos aleatorios (8 para landing)
            List<Producto> lista = pdao.obtenerProductosAleatorios(8, con);

            // 4. Guardar en request para la vista
            request.setAttribute("productos", lista);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error cargando los productos: " + e.getMessage());
        } finally {
            // 5. SIEMPRE cerrar la conexión (la devuelve al pool)
            ConnectionFactory.closeConnection(con);
        }

        return "index.jsp";
    }

    /**
     * Filtra productos por categoría, marca y rango de precio.
     * Implementa la búsqueda avanzada combinando múltiples criterios.
     * 
     * @param request petición HTTP con parámetros: idCategoria, marca, precioMin, precioMax
     * @return URL de la vista a mostrar
     */
    private String accionFiltrar(HttpServletRequest request) {
        Connection con = null;
        try {
            // 1. Obtener conexión del pool
            con = ConnectionFactory.getConnection();

            // 2. Recoger parámetros del formulario
            String idCatStr = request.getParameter("idCategoria");
            String marca = request.getParameter("marca");
            String precioMinStr = request.getParameter("precioMin");
            String precioMaxStr = request.getParameter("precioMax");
            String nombre = request.getParameter("nombre");

            // 3. Parsear parámetros numéricos
            Byte idCategoria = null;
            if (idCatStr != null && !idCatStr.trim().isEmpty() && !idCatStr.equals("0")) {
                try {
                    idCategoria = Byte.parseByte(idCatStr);
                } catch (NumberFormatException e) {
                    // Si no es válido, se ignora
                }
            }

            BigDecimal precioMin = null;
            if (precioMinStr != null && !precioMinStr.trim().isEmpty()) {
                try {
                    precioMin = new BigDecimal(precioMinStr);
                } catch (NumberFormatException e) {
                    // Si no es válido, se ignora
                }
            }

            BigDecimal precioMax = null;
            if (precioMaxStr != null && !precioMaxStr.trim().isEmpty()) {
                try {
                    precioMax = new BigDecimal(precioMaxStr);
                } catch (NumberFormatException e) {
                    // Si no es válido, se ignora
                }
            }

            // 4. Obtener DAO
            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            ProductoDAO pdao = factory.getProductoDAO();

            // 5. Ejecutar filtrado
            List<Producto> filtrados;
            if (idCategoria != null) {
                // Si hay categoría, filtrar solo por categoría
                filtrados = pdao.listarPorCategoria(idCategoria, con);
            } else {
                // Si no, usar filtros combinados
                filtrados = pdao.buscarPorFiltros(nombre, marca, precioMin, precioMax, con);
            }

            // 6. Guardar resultados y mantener selección en la vista
            request.setAttribute("productos", filtrados);
            request.setAttribute("catSeleccionada", idCatStr);
            request.setAttribute("marcaSeleccionada", marca);
            request.setAttribute("precioMinSeleccionado", precioMinStr);
            request.setAttribute("precioMaxSeleccionado", precioMaxStr);
            request.setAttribute("nombreBuscado", nombre);

            // 7. Mensaje si no hay resultados
            if (filtrados.isEmpty()) {
                request.setAttribute("mensajeInfo", "No se encontraron productos con los filtros seleccionados.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error al filtrar productos: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        return "index.jsp";
    }

    /**
     * Búsqueda simple por texto en el nombre de los productos.
     * Busca productos que contengan el texto en su nombre (LIKE %texto%).
     * 
     * @param request petición HTTP con parámetro: textoBusqueda
     * @return URL de la vista a mostrar
     */
    private String accionBuscar(HttpServletRequest request) {
        Connection con = null;
        try {
            // 1. Recoger texto de búsqueda
            String texto = request.getParameter("textoBusqueda");

            if (texto != null && !texto.trim().isEmpty()) {
                // 2. Obtener conexión
                con = ConnectionFactory.getConnection();

                // 3. Obtener DAO
                MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
                ProductoDAO pdao = factory.getProductoDAO();

                // 4. Buscar por nombre (usa buscarPorFiltros con solo nombre)
                List<Producto> resultados = pdao.buscarPorFiltros(texto, null, null, null, con);

                // 5. Guardar resultados
                request.setAttribute("productos", resultados);
                request.setAttribute("textoBuscado", texto);

                // 6. Mensaje si no hay resultados
                if (resultados.isEmpty()) {
                    request.setAttribute("mensajeInfo", "No se encontraron productos con: \"" + texto + "\"");
                } else {
                    request.setAttribute("mensajeInfo", "Se encontraron " + resultados.size() + 
                                                       " productos para: \"" + texto + "\"");
                }
            } else {
                request.setAttribute("mensajeError", "Debes introducir un texto para buscar.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error en la búsqueda: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        return "index.jsp";
    }

    /**
     * Tramita el pedido (finaliza el carrito).
     * Verifica que el usuario esté logueado antes de permitir la compra.
     * 
     * @param request petición HTTP con sesión del usuario
     * @return URL de la vista a mostrar
     */
    private String accionTramitarPedido(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);

        // Verificar si el usuario está logueado
        if (sesion == null || sesion.getAttribute("usuario") == null) {
            request.setAttribute("mensajeError", 
                "Para tramitar el pedido necesitas iniciar sesión o registrarte.");
            return "JSP/login.jsp";
        }

        // Usuario logueado: delegar al PedidoController
        // (Aquí se implementará la lógica completa de finalización)
        return "PedidoController";
    }

    /**
     * Devuelve una descripción breve del servlet.
     */
    @Override
    public String getServletInfo() {
        return "Front Controller - Controlador principal de Chollotek";
    }
    }
        
    

