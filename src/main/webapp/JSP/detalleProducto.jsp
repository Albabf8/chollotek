<%-- 
    Document   : detalle-producto
    Created on : 22 feb. 2026, 20:18:31
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
    <title>${producto.nombre} - Chollotek</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilo.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@400;700;900&family=Exo+2:wght@300;400;600;700&display=swap" rel="stylesheet">
</head>
<body>
    
    <!-- HEADER -->
    <jsp:include page="../INC/header.jsp" />

    <!-- MENSAJES -->
    <c:if test="${not empty mensajeError}">
        <div class="container">
            <div class="alert alert-error">${mensajeError}</div>
        </div>
    </c:if>
    <c:if test="${not empty mensajeExito}">
        <div class="container">
            <div class="alert alert-success">${mensajeExito}</div>
        </div>
    </c:if>

    <!-- BREADCRUMB -->
    <div class="container">
        <nav class="breadcrumb">
            <a href="${pageContext.request.contextPath}/FrontController?accion=inicio">Inicio</a>
            <span class="separator">›</span>
            <c:if test="${not empty categoria}">
                <a href="${pageContext.request.contextPath}/FrontController?accion=filtrar&idCategoria=${categoria.idcategoria}">
                    ${categoria.nombre}
                </a>
                <span class="separator">›</span>
            </c:if>
            <span class="current">${producto.nombre}</span>
        </nav>
    </div>

    <!-- DETALLE DEL PRODUCTO -->
    <div class="container">
        <div class="detalle-producto">
            
            <!-- IMAGEN DEL PRODUCTO -->
            <div class="detalle-imagen">
                <div class="imagen-principal">
                    <img src="${pageContext.request.contextPath}/imagenes/productos/${producto.imagen}" 
                         alt="${producto.nombre}"
                         onerror="this.src='${pageContext.request.contextPath}/imagenes/productos/default.jpg'">
                </div>
            </div>

            <!-- INFORMACIÓN DEL PRODUCTO -->
            <div class="detalle-info">
                <h1 class="producto-titulo">${producto.nombre}</h1>
                
                <c:if test="${not empty producto.marca}">
                    <p class="producto-marca">
                        <span class="label">Marca:</span> ${producto.marca}
                    </p>
                </c:if>

                <div class="producto-precio-box">
                    <span class="precio-grande">
                        <fmt:formatNumber value="${producto.precio}" type="currency" currencySymbol="€"/>
                    </span>
                    <span class="precio-iva">IVA incluido</span>
                </div>

                <c:if test="${not empty producto.descripcion}">
                    <div class="producto-descripcion">
                        <h3>Descripción</h3>
                        <p>${producto.descripcion}</p>
                    </div>
                </c:if>

                <!-- AÑADIR AL CARRITO -->
                <form action="${pageContext.request.contextPath}/CarritoController" 
                      method="post" 
                      class="form-anadir-carrito">
                    <input type="hidden" name="accion" value="anadir">
                    <input type="hidden" name="idproducto" value="${producto.idproducto}">
                    
                    <div class="cantidad-selector">
                        <label for="cantidad">Cantidad:</label>
                        <div class="cantidad-input">
                            <button type="button" class="btn-cantidad" onclick="decrementarCantidad()">-</button>
                            <input type="number" 
                                   id="cantidad" 
                                   name="cantidad" 
                                   value="1" 
                                   min="1" 
                                   max="99">
                            <button type="button" class="btn-cantidad" onclick="incrementarCantidad()">+</button>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary btn-block btn-comprar">
                        🛒 Añadir al carrito
                    </button>
                </form>

                <!-- INFORMACIÓN ADICIONAL -->
                <div class="producto-extras">
                    <div class="extra-item">
                        <span class="icon">✓</span>
                        <span>Envío rápido en 24-48h</span>
                    </div>
                    <div class="extra-item">
                        <span class="icon">↩</span>
                        <span>Devolución gratuita en 30 días</span>
                    </div>
                    <div class="extra-item">
                        <span class="icon">🔒</span>
                        <span>Pago 100% seguro</span>
                    </div>
                </div>
            </div>
        </div>

        <!-- PRODUCTOS RELACIONADOS -->
        <c:if test="${not empty relacionados}">
            <div class="glow-line"></div>
            
            <section class="section">
                <h2 class="section-title">Productos relacionados</h2>
                
                <div class="product-grid">
                    <c:forEach items="${relacionados}" var="prod">
                        <article class="product-card">
                            <a href="${pageContext.request.contextPath}/FrontController?accion=verDetalle&idproducto=${prod.idproducto}">
                                <div class="product-img">
                                    <img src="${pageContext.request.contextPath}/imagenes/productos/${prod.imagen}" 
                                         alt="${prod.nombre}"
                                         onerror="this.src='${pageContext.request.contextPath}/imagenes/productos/default.jpg'">
                                </div>
                            </a>
                            
                            <div class="producto-info">
                                <h3>
                                    <a href="${pageContext.request.contextPath}/FrontController?accion=verDetalle&idproducto=${prod.idproducto}">
                                        ${prod.nombre}
                                    </a>
                                </h3>
                                <c:if test="${not empty prod.marca}">
                                    <p class="marca">${prod.marca}</p>
                                </c:if>
                                <p class="precio">
                                    <fmt:formatNumber value="${prod.precio}" type="currency" currencySymbol="€"/>
                                </p>
                                
                                <form action="${pageContext.request.contextPath}/CarritoController" method="post">
                                    <input type="hidden" name="accion" value="anadir">
                                    <input type="hidden" name="idproducto" value="${prod.idproducto}">
                                    <button type="submit" class="btn-buy">
                                        🛒 Añadir
                                    </button>
                                </form>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </section>
        </c:if>
    </div>

    <!-- FOOTER -->
    <jsp:include page="../INC/footer.jsp" />

    <script>
        // Controles de cantidad
        function incrementarCantidad() {
            const input = document.getElementById('cantidad');
            const valor = parseInt(input.value);
            if (valor < 99) {
                input.value = valor + 1;
            }
        }

        function decrementarCantidad() {
            const input = document.getElementById('cantidad');
            const valor = parseInt(input.value);
            if (valor > 1) {
                input.value = valor - 1;
            }
        }
    </script>

</body>
</html>
