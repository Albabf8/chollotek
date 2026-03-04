package es.chollotek.controllers;

import es.chollotek.DAO.CategoriaDAO;
import es.chollotek.DAO.ConnectionFactory;
import es.chollotek.DAO.LineaPedidoDAO;
import es.chollotek.DAO.PedidoDAO;
import es.chollotek.DAO.ProductoDAO;
import es.chollotek.DAOFactory.MySQLDAOFactory;
import es.chollotek.beans.Categoria;
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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "FrontController", urlPatterns = {"/FrontController"})
public class FrontController extends HttpServlet {

    /**
     * Gestiona las peticiones GET redirigiéndolas al método doPost.
     * * @param request La petición HTTP.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error específico del servlet.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Método principal que procesa todas las peticiones POST y GET.
     * Analiza el parámetro 'accion' para determinar el flujo de navegación
     * y despacha la petición al recurso o controlador correspondiente.
     * * @param request La petición HTTP.
     * @param response La respuesta HTTP.
     * @throws ServletException Si ocurre un error en el despacho de la petición.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String url = "inicio.jsp";
        String accion = request.getParameter("accion");

        if (accion != null) {
            switch (accion) {

                case "inicio":
                    url = accionInicio(request);
                    break;

                case "filtrar":
                    url = accionFiltrar(request);
                    break;

                case "buscar":
                    url = accionBuscar(request);
                    break;

                case "verCarrito":
                    url = accionVerCarrito(request);
                    break;

                case "anadir":
                case "sumarCantidad":
                case "restarCantidad":
                case "vaciarCarrito":
                case "eliminarProducto":
                    url = "CarritoController";
                    break;

                case "tramitarPedido":
                    url = accionTramitarPedido(request);
                    break;

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
                    url = "JSP/perfil.jsp";
                    break;

                case "verPedidos":
                     url = accionVerPedidos(request);
                    break;

                case "logout":
                    request.getSession().invalidate();
                    url = "inicio.jsp";
                    break;

                case "verDetalle":
                    url = accionVerDetalle(request);
                    break;

                default:
                    url = accionInicio(request);
                    break;
            }
        } else {
            url = accionInicio(request);
        }

        request.getRequestDispatcher(url).forward(request, response);
    }

    /**
     * Prepara los datos para la página de inicio.
     * Obtiene una selección aleatoria de productos para mostrar como destacados.
     * * @param request La petición HTTP donde se guardará la lista de productos.
     * @return La URL de la página de inicio ("inicio.jsp").
     */
    private String accionInicio(HttpServletRequest request) {
        Connection con = null;
        try {
            con = ConnectionFactory.getConnection();
            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            ProductoDAO pdao = factory.getProductoDAO();
            List<Producto> lista = pdao.obtenerProductosAleatorios(8, con);
            request.setAttribute("productos", lista);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error cargando los productos: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return "inicio.jsp";
    }

    /**
     * Gestiona el filtrado avanzado de productos.
     * Permite filtrar por categoría, marca, nombre y rango de precios.
     * * @param request La petición HTTP con los parámetros de filtrado.
     * @return La URL de la página de resultados ("JSP/resultados.jsp").
     */
    private String accionFiltrar(HttpServletRequest request) {
        Connection con = null;
        try {
            con = ConnectionFactory.getConnection();

            String idCatStr = request.getParameter("idCategoria");
            String marca = request.getParameter("marca");
            String precioMinStr = request.getParameter("precioMin");
            String precioMaxStr = request.getParameter("precioMax");
            String nombre = request.getParameter("nombre");

            Integer idCategoria = null;
            if (idCatStr != null && !idCatStr.trim().isEmpty() && !idCatStr.equals("0")) {
                try { idCategoria = Integer.parseInt(idCatStr); } catch (NumberFormatException e) { }
            }

            BigDecimal precioMin = null;
            if (precioMinStr != null && !precioMinStr.trim().isEmpty()) {
                try { precioMin = new BigDecimal(precioMinStr); } catch (NumberFormatException e) { }
            }

            BigDecimal precioMax = null;
            if (precioMaxStr != null && !precioMaxStr.trim().isEmpty()) {
                try { precioMax = new BigDecimal(precioMaxStr); } catch (NumberFormatException e) { }
            }

            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            ProductoDAO pdao = factory.getProductoDAO();

            List<Producto> filtrados;
            if (idCategoria != null) {
                filtrados = pdao.listarPorCategoria(idCategoria, con);
            } else {
                filtrados = pdao.buscarPorFiltros(nombre, marca, precioMin, precioMax, con);
            }

            request.setAttribute("productos", filtrados);
            request.setAttribute("catSeleccionada", idCatStr);
            request.setAttribute("marcaSeleccionada", marca);
            request.setAttribute("precioMinSeleccionado", precioMinStr);
            request.setAttribute("precioMaxSeleccionado", precioMaxStr);
            request.setAttribute("nombreBuscado", nombre);

            if (filtrados.isEmpty()) {
                request.setAttribute("mensajeInfo", "No se encontraron productos con los filtros seleccionados.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error al filtrar productos: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }
        return "JSP/resultados.jsp";
    }

    /**
     * Procesa la búsqueda rápida de productos por texto.
     * * @param request La petición HTTP con el parámetro 'textoBusqueda'.
     * @return La URL de la página de resultados ("JSP/resultados.jsp").
     */
    private String accionBuscar(HttpServletRequest request) {
        Connection con = null;
        try {
            String texto = request.getParameter("textoBusqueda");

            if (texto != null && !texto.trim().isEmpty()) {
                con = ConnectionFactory.getConnection();
                MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
                ProductoDAO pdao = factory.getProductoDAO();
                List<Producto> resultados = pdao.buscarPorFiltros(texto, null, null, null, con);

                request.setAttribute("productos", resultados);
                request.setAttribute("textoBuscado", texto);

                if (resultados.isEmpty()) {
                    request.setAttribute("mensajeInfo", "No se encontraron productos con: \"" + texto + "\"");
                } else {
                    request.setAttribute("mensajeInfo", "Se encontraron " + resultados.size()
                            + " productos para: \"" + texto + "\"");
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
        return "JSP/resultados.jsp";
    }

/**
     * Recupera y muestra el contenido del carrito del usuario.
     * Para usuarios registrados, carga las líneas y los detalles completos de cada producto desde la BD.
     * * @param request La petición HTTP para acceder a la sesión y atributos de vista.
     * @return La URL de la vista del carrito ("JSP/carrito.jsp").
     */
    private String accionVerCarrito(HttpServletRequest request) {
        Connection con = null;

        try {
            // 1. Verificar si hay usuario logueado
            HttpSession sesion = request.getSession(false);
            Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

            if (usuario == null) {
                // Usuario anónimo: carrito vacío por ahora
                request.setAttribute("lineasCarrito", new ArrayList<LineaPedido>());
                return "JSP/carrito.jsp";
            }

            // 2. Obtener conexión
            con = ConnectionFactory.getConnection();

            // 3. Obtener DAOs
            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            PedidoDAO pedidoDAO = factory.getPedidoDAO();
            LineaPedidoDAO lineaDAO = factory.getLineaPedidoDAO();
            ProductoDAO productoDAO = factory.getProductoDAO();

            // 4. Buscar carrito del usuario
            Pedido carrito = pedidoDAO.buscarCarrito(usuario.getIdusuario(), con);

            if (carrito == null) {
                // No tiene carrito: mostrar vacío
                request.setAttribute("lineasCarrito", new ArrayList<LineaPedido>());
                return "JSP/carrito.jsp";
            }

            // 5. Obtener líneas del carrito
            List<LineaPedido> lineas = lineaDAO.listarPorPedido(carrito.getIdpedido(), con);

            // 6. CARGAR PRODUCTOS COMPLETOS EN CADA LÍNEA
            for (LineaPedido linea : lineas) {
                Producto producto = productoDAO.buscarPorId(linea.getIdproducto(), con);
                linea.setProducto(producto);
            }

            // 7. Guardar en request
            request.setAttribute("lineasCarrito", lineas);
            request.setAttribute("carrito", carrito);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error al cargar el carrito: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        return "JSP/carrito.jsp";
    }

    /**
     * Verifica la autenticación antes de permitir la tramitación de un pedido.
     * * @param request La petición HTTP para validar la sesión del usuario.
     * @return El controlador de pedidos o la página de login si no está autenticado.
     */
    private String accionTramitarPedido(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);

        if (sesion == null || sesion.getAttribute("usuario") == null) {
            request.setAttribute("mensajeError",
                    "Para tramitar el pedido necesitas iniciar sesión o registrarte.");
            return "JSP/login.jsp";
        }

        return "PedidoController";
    }

    /**
     * Prepara la ficha detallada de un producto.
     * Carga los datos del producto, su categoría y una lista de productos relacionados.
     * * @param request La petición HTTP con el parámetro 'idproducto'.
     * @return La URL de la página de detalle ("JSP/detalleProducto.jsp").
     */
    private String accionVerDetalle(HttpServletRequest request) {
        Connection con = null;
        try {
            String idProductoStr = request.getParameter("idproducto");

            if (idProductoStr == null || idProductoStr.trim().isEmpty()) {
                request.setAttribute("mensajeError", "Producto no especificado.");
                return "FrontController?accion=inicio";
            }

            short idProducto = Short.parseShort(idProductoStr);
            con = ConnectionFactory.getConnection();

            MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
            ProductoDAO productoDAO = factory.getProductoDAO();
            CategoriaDAO categoriaDAO = factory.getCategoriaDAO();

            Producto producto = productoDAO.buscarPorId(idProducto, con);

            if (producto == null) {
                request.setAttribute("mensajeError", "Producto no encontrado.");
                return "FrontController?accion=inicio";
            }

            Categoria categoria = categoriaDAO.buscarPorId(producto.getIdcategoria(), con);

            List<Producto> relacionados = productoDAO.listarPorCategoria(producto.getIdcategoria(), con);

            java.util.Iterator<Producto> it = relacionados.iterator();
            while (it.hasNext()) {
                Producto p = it.next();
                if (p.getIdproducto() == idProducto) {
                    it.remove();
                }
            }
            if (relacionados.size() > 4) {
                relacionados = relacionados.subList(0, 4);
            }

            request.setAttribute("producto", producto);
            request.setAttribute("categoria", categoria);
            request.setAttribute("relacionados", relacionados);

        } catch (NumberFormatException e) {
            request.setAttribute("mensajeError", "ID de producto inválido.");
            return "FrontController?accion=inicio";
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("mensajeError", "Error al cargar el producto: " + e.getMessage());
            return "FrontController?accion=inicio";
        } finally {
            ConnectionFactory.closeConnection(con);
        }

        return "JSP/detalleProducto.jsp";
    }
    
    /**
     * Recupera el historial de pedidos finalizados del usuario actual.
     * * @param request La petición HTTP para obtener el ID del usuario logueado.
     * @return La URL de la vista de pedidos ("JSP/pedidos.jsp").
     */
    private String accionVerPedidos(HttpServletRequest request) {
    Connection con = null;
    try {
        HttpSession sesion = request.getSession(false);
        Usuario usuario = (sesion != null) ? (Usuario) sesion.getAttribute("usuario") : null;

        if (usuario == null) {
            return "JSP/login.jsp";
        }

        con = ConnectionFactory.getConnection();
        MySQLDAOFactory factory = MySQLDAOFactory.getInstancia();
        PedidoDAO pedidoDAO = factory.getPedidoDAO();

        List<Pedido> pedidos = pedidoDAO.listarPedidosFinalizados(usuario.getIdusuario(), con);
        request.setAttribute("pedidos", pedidos);

    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("mensajeError", "Error al cargar los pedidos: " + e.getMessage());
    } finally {
        ConnectionFactory.closeConnection(con);
    }
    return "JSP/pedidos.jsp";
}

    @Override
    public String getServletInfo() {
        return "Front Controller - Controlador principal de Chollotek";
    }
}
