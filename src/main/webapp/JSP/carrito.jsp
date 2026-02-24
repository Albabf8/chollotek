<%-- 
    Document   : carrito
    Created on : 20 feb. 2026, 0:42:27
    Author     : Alba
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carrito - Chollotek</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilo.css">
</head>
<body>
    
    <jsp:include page="INC/header.jsp" />

    <div class="container">
        <h2>🛒 Mi carrito</h2>

        <!-- MENSAJES -->
        <c:if test="${not empty mensajeError}">
            <div class="alert alert-error">${mensajeError}</div>
        </c:if>
        <c:if test="${not empty mensajeExito}">
            <div class="alert alert-success">${mensajeExito}</div>
        </c:if>

        <c:choose>
            <c:when test="${empty lineasCarrito}">
                <div class="carrito-vacio">
                    <p>Tu carrito está vacío</p>
                    <a href="${pageContext.request.contextPath}/FrontController?accion=inicio" 
                       class="btn btn-primary">
                        Ir a comprar
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <!-- TABLA DEL CARRITO -->
                <table class="tabla-carrito">
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Precio</th>
                            <th>Cantidad</th>
                            <th>Subtotal</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:set var="subtotal" value="0" />
                        <c:forEach items="${lineasCarrito}" var="linea">
                            <c:set var="subtotalLinea" value="${linea.producto.precio * linea.cantidad}" />
                            <c:set var="subtotal" value="${subtotal + subtotalLinea}" />
                            
                            <tr>
                                <td>
                                    <div class="producto-carrito">
                                        <img src="${pageContext.request.contextPath}/imagenes/productos/${linea.producto.imagen}" 
                                             alt="${linea.producto.nombre}">
                                        <div>
                                            <strong>${linea.producto.nombre}</strong>
                                            <small>${linea.producto.marca}</small>
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <fmt:formatNumber value="${linea.producto.precio}" type="currency" currencySymbol="€"/>
                                </td>
                                <td>
                                    <div class="cantidad-controls">
                                        <form action="CarritoController" method="post" class="form-inline">
                                            <input type="hidden" name="accion" value="restarCantidad">
                                            <input type="hidden" name="idlinea" value="${linea.idlinea}">
                                            <button type="submit" class="btn-cantidad">-</button>
                                        </form>
                                        
                                        <span class="cantidad">${linea.cantidad}</span>
                                        
                                        <form action="CarritoController" method="post" class="form-inline">
                                            <input type="hidden" name="accion" value="sumarCantidad">
                                            <input type="hidden" name="idlinea" value="${linea.idlinea}">
                                            <button type="submit" class="btn-cantidad">+</button>
                                        </form>
                                    </div>
                                </td>
                                <td>
                                    <strong>
                                        <fmt:formatNumber value="${subtotalLinea}" type="currency" currencySymbol="€"/>
                                    </strong>
                                </td>
                                <td>
                                    <form action="CarritoController" method="post" class="form-inline">
                                        <input type="hidden" name="accion" value="eliminarProducto">
                                        <input type="hidden" name="idlinea" value="${linea.idlinea}">
                                        <button type="submit" class="btn btn-danger btn-sm">🗑️ Eliminar</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <!-- RESUMEN DEL PEDIDO -->
                <div class="carrito-resumen">
                    <div class="resumen-card">
                        <h3>Resumen del pedido</h3>
                        
                        <div class="resumen-linea">
                            <span>Subtotal:</span>
                            <span>
                                <fmt:formatNumber value="${subtotal}" type="currency" currencySymbol="€"/>
                            </span>
                        </div>
                        
                        <c:set var="iva" value="${subtotal * 0.21}" />
                        <div class="resumen-linea">
                            <span>IVA (21%):</span>
                            <span>
                                <fmt:formatNumber value="${iva}" type="currency" currencySymbol="€"/>
                            </span>
                        </div>
                        
                        <div class="resumen-linea resumen-total">
                            <span>TOTAL:</span>
                            <span>
                                <fmt:formatNumber value="${subtotal + iva}" type="currency" currencySymbol="€"/>
                            </span>
                        </div>

                        <div class="carrito-acciones">
                            <c:choose>
                                <c:when test="${not empty sessionScope.usuario}">
                                    <form action="PedidoController" method="post">
                                        <input type="hidden" name="accion" value="tramitarPedido">
                                        <button type="submit" class="btn btn-primary btn-block">
                                            Finalizar compra
                                        </button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <p class="alert alert-info">
                                        Para finalizar la compra debes 
                                        <a href="${pageContext.request.contextPath}/FrontController?accion=verLogin">
                                            iniciar sesión
                                        </a>
                                    </p>
                                </c:otherwise>
                            </c:choose>

                            <form action="CarritoController" method="post">
                                <input type="hidden" name="accion" value="vaciarCarrito">
                                <button type="submit" class="btn btn-danger btn-block">
                                    Vaciar carrito
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <jsp:include page="includes/footer.jsp" />

</body>
</html>
