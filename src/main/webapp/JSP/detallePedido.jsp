<%--
    Document   : detallePedido
    Created on : 03 mar. 2026
    Author     : Alba
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"   prefix="c"   %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"    prefix="fmt" %>

<c:if test="${empty sessionScope.usuario}">
    <c:redirect url="${pageContext.request.contextPath}/FrontController?accion=verLogin"/>
</c:if>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pedido #${pedido.idpedido} - Chollotek</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilo.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@400;700;900&family=Exo+2:wght@300;400;600;700&display=swap"
          rel="stylesheet">
</head>
<body>

    <jsp:include page="../INC/header.jsp" />

    <%-- MENSAJES --%>
    <c:if test="${not empty mensajeError}">
        <div class="container container-detalle-pedido">
            <div class="alert alert-error">${mensajeError}</div>
        </div>
    </c:if>
    <c:if test="${not empty mensajeExito}">
        <div class="container container-detalle-pedido">
            <div class="alert alert-success">${mensajeExito}</div>
        </div>
    </c:if>

    <div class="container container-detalle-pedido">

        <%-- BREADCRUMB --%>
        <nav class="breadcrumb">
            <a href="${pageContext.request.contextPath}/FrontController?accion=inicio">Inicio</a>
            <span class="separator">›</span>
            <a href="${pageContext.request.contextPath}/FrontController?accion=verPedidos">Mis pedidos</a>
            <span class="separator">›</span>
            <span class="current">Pedido #${pedido.idpedido}</span>
        </nav>

        <%-- CABECERA --%>
        <div class="dp-header">
            <div class="dp-header-left">
                <h2 class="dp-titulo">
                    📦 Pedido <span class="dp-id">#${pedido.idpedido}</span>
                </h2>
                <span class="dp-badge-estado">Finalizado ✅</span>
            </div>
            <div class="dp-header-meta">
                <div class="dp-meta-item">
                    <span class="dp-meta-label">Fecha</span>
                    <span class="dp-meta-value">
                        <fmt:formatDate value="${pedido.fecha}" pattern="dd/MM/yyyy"/>
                    </span>
                </div>
                <div class="dp-meta-item">
                    <span class="dp-meta-label">Nº pedido</span>
                    <span class="dp-meta-value">${pedido.idpedido}</span>
                </div>
                <div class="dp-meta-item">
                    <span class="dp-meta-label">Artículos</span>
                    <span class="dp-meta-value">
                        <c:set var="totalUds" value="0"/>
                        <c:forEach items="${lineas}" var="l">
                            <c:set var="totalUds" value="${totalUds + l.cantidad}"/>
                        </c:forEach>
                        ${totalUds}
                    </span>
                </div>
            </div>
        </div>

        <%-- LAYOUT DOS COLUMNAS --%>
        <div class="dp-layout">

            <%-- COLUMNA IZQUIERDA: líneas del pedido --%>
            <div class="dp-lineas">
                <h3 class="dp-seccion-titulo">🛒 Artículos del pedido</h3>

                <c:choose>
                    <c:when test="${empty lineas}">
                        <div class="alert alert-info">
                            Este pedido no tiene artículos registrados.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="dp-tabla-wrapper">
                            <table class="dp-tabla">
                                <thead>
                                    <tr>
                                        <th>Producto</th>
                                        <th class="dp-col-centro">Precio ud.</th>
                                        <th class="dp-col-centro">Cantidad</th>
                                        <th class="dp-col-derecha">Subtotal</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${lineas}" var="linea">
                                        <tr>
                                            <%-- Producto --%>
                                            <td data-label="Producto">
                                                <div class="dp-producto">
                                                    <div class="dp-img-wrap">
                                                        <img src="${pageContext.request.contextPath}/imagenes/imagen/productos/${linea.producto.imagen}.jpg"
                                                             alt="${linea.producto.nombre}"
                                                             onerror="this.src='${pageContext.request.contextPath}/imagenes/imagen/productos/default.jpg'">
                                                    </div>
                                                    <div class="dp-prod-info">
                                                        <%-- ✅ Enlace al detalle del producto via POST --%>
                                                        <form method="post"
                                                              action="${pageContext.request.contextPath}/FrontController"
                                                              style="display:inline;">
                                                            <input type="hidden" name="accion"     value="verDetalle">
                                                            <input type="hidden" name="idproducto" value="${linea.idproducto}">
                                                            <button type="submit" class="dp-prod-nombre-btn">
                                                                ${linea.producto.nombre}
                                                            </button>
                                                        </form>
                                                        <c:if test="${not empty linea.producto.marca}">
                                                            <span class="dp-prod-marca">
                                                                ${linea.producto.marca}
                                                            </span>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </td>

                                            <%-- Precio unitario --%>
                                            <td data-label="Precio ud." class="dp-col-centro dp-precio-unit">
                                                <fmt:formatNumber value="${linea.producto.precio}"
                                                                  type="currency" currencySymbol="€"/>
                                            </td>

                                            <%-- Cantidad --%>
                                            <td data-label="Cantidad" class="dp-col-centro">
                                                <span class="dp-badge-cantidad">×${linea.cantidad}</span>
                                            </td>

                                            <%-- Subtotal línea --%>
                                            <td data-label="Subtotal" class="dp-col-derecha dp-subtotal-linea">
                                                <fmt:formatNumber
                                                    value="${linea.producto.precio * linea.cantidad}"
                                                    type="currency" currencySymbol="€"/>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <%-- COLUMNA DERECHA: resumen económico --%>
            <aside class="dp-resumen">
                <div class="resumen-card">
                    <h3>💳 Resumen del pedido</h3>

                    <div class="resumen-linea">
                        <span>Subtotal</span>
                        <span>
                            <fmt:formatNumber value="${subtotal}" type="currency" currencySymbol="€"/>
                        </span>
                    </div>
                    <div class="resumen-linea">
                        <span>IVA (21 %)</span>
                        <span>
                            <fmt:formatNumber value="${iva}" type="currency" currencySymbol="€"/>
                        </span>
                    </div>
                    <div class="resumen-linea resumen-total">
                        <span>Total</span>
                        <span>
                            <fmt:formatNumber value="${total}" type="currency" currencySymbol="€"/>
                        </span>
                    </div>

                    <%-- Info de envío --%>
                    <div class="dp-envio">
                        <div class="dp-envio-item">
                            <span>🚚</span><span>Envío rápido 24-48 h</span>
                        </div>
                        <div class="dp-envio-item">
                            <span>🔒</span><span>Pago 100 % seguro</span>
                        </div>
                        <div class="dp-envio-item">
                            <span>↩</span><span>Devolución gratuita 30 días</span>
                        </div>
                    </div>

                    <%-- ✅ Acciones sin parámetros sensibles en la URL --%>
                    <div class="dp-acciones">
                        <a href="${pageContext.request.contextPath}/FrontController?accion=verPedidos"
                           class="btn btn-secondary btn-block">
                            ← Volver a mis pedidos
                        </a>
                        <a href="${pageContext.request.contextPath}/FrontController?accion=inicio"
                           class="btn btn-primary btn-block">
                            🛒 Seguir comprando
                        </a>
                    </div>

                </div>
            </aside>

        </div><%-- /dp-layout --%>
    </div><%-- /container --%>

    <jsp:include page="../INC/footer.jsp" />

</body>
</html>
