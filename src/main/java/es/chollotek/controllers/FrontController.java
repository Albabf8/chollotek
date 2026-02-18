package es.chollotek.controllers;

import es.chollotek.beans.Producto;
import es.chollotek.DAOFactory.DAOFactory;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import es.chollotek.DAO.ProductoDAO;

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

        String url = "index.jsp";

        String accion = request.getParameter("accion");

        if (accion != null) {
            switch (accion) {

                case "inicio":
                    try {
                    DAOFactory daof = DAOFactory.getDAOFactory();
                    ProductoDAO pdao = daof.getProductoDAO();
                    List<Producto> lista = pdao.getProductosLanding();

                    // Guardamos la lista en la request para mostrar en index.jsp
                    request.setAttribute("productos", lista);

                    

                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("mensajeError", "Error cargando los productos.");
                }
                url = "index.jsp";
                break;


//                case "filtrar":
//                    try {
//                    DAOFactory daof = DAOFactory.getDAOFactory();
//                    IProductoDAO pdao = daof.getProductoDAO();
//
//                    // A. Recoger parámetros del formulario/enlace
//                    String idCat = request.getParameter("idCategoria");
//                    String marca = request.getParameter("marca");
//                    String precioStr = request.getParameter("precio"); // El name del input range
//
//                    // B. Gestionar el precio (parsear y validar)
//                    double precioTope = -1;
//
//                    // Recuperamos el tope global del Listener por si hay error o viene vacío
//                    Object precioGlobalObj = getServletContext().getAttribute("precioTopeGlobal");
//                    double precioGlobal = (precioGlobalObj != null) ? (Double) precioGlobalObj : 1000.00;
//
//                    if (precioStr != null && !precioStr.isEmpty()) {
//                        try {
//                            precioTope = Double.parseDouble(precioStr);
//                        } catch (NumberFormatException e) {
//                            precioTope = precioGlobal;
//                        }
//                    } else {
//                        precioTope = precioGlobal;
//                    }
//
//                    // C. Llamar al DAO Filtrado
//                    //List<Producto> filtrados = pdao.getProductosFiltrados(idCat, marca, precioTope);
//                    //request.setAttribute("productos", filtrados);
//
//                    // D. Mantener la selección en la vista (para que no se resetee el formulario)
//                    request.setAttribute("catSeleccionada", idCat);
//                    request.setAttribute("marcaSeleccionada", marca);
//                    request.setAttribute("precioSeleccionado", precioTope);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    request.setAttribute("mensajeError", "Error al filtrar productos.");
//                }
//
//                url = "index.jsp";
//                break;
//

//                case "buscar":
//                    String texto = request.getParameter("textoBusqueda");
//                    if (texto != null && !texto.trim().isEmpty()) {
//                        DAOFactory daof = DAOFactory.getDAOFactory();
//                        IProductoDAO pdao = daof.getProductoDAO();
//                        List<Producto> resultados = pdao.buscarProductos(texto);
//
//                        // Guardamos los resultados en la request para mostrarlos
//                        //request.setAttribute("resultadosBusqueda", resultados);
//                        request.setAttribute("productos", resultados);
//
//                        // Mensaje informativo
//                        if (resultados.isEmpty()) {
//                            request.setAttribute("mensajeError", "No se encontraron productos con: " + texto);
//                        }
//                    }
//
//                    url = "index.jsp"; 
//                    break;

                case "verCarrito":
                    url = "JSP/carrito.jsp";
                    break;

                case "anadir":
                case "sumarCantidad":
                case "restarCantidad":
                case "vaciarCarrito":
                case "eliminarProducto":
                    url = "CarritoController";
                    break;

//                case "tramitarPedido":
//                    HttpSession sesion = request.getSession();
//                    if (sesion.getAttribute("usuario") == null) {
//                        request.setAttribute("mensajeError", "Para tramitar el pedido necesitas iniciar sesión o registrarte.");
//                        url = "JSP/login.jsp";
//                    } else {
//                        // ÉXITO: Está logueado
//                        // Aquí iría la lógica de guardar el pedido en BBDD
//                        // Por ahora lo mandamos a una página de éxito o resumen
//                        // url = "pedidoFinalizado.jsp"; 
//                        System.out.println("Usuario logueado, tramitando pedido...");
//                    }
//                    break;

                case "verRegistro":
                    url = "JSP/registro.jsp";
                    break;

                case "verLogin":
                    url = "JSP/login.jsp";
                    break;

                case "registrarUsuario":
                    url = "RegistroController";
                    break;

                default:
                    url = "index.jsp";
                    break;
            }
        }

        request.getRequestDispatcher(url).forward(request, response);
    }
        
    }

