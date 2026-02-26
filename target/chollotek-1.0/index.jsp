<%-- 
    Document   : index
    Created on : 6 feb. 2026, 17:47:23
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
    <title>Chollotek - Tu tienda online</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilo.css">
</head>
<body>
    
    <!-- HEADER -->
    <jsp:include page="INC/header.jsp" />

    <!-- MENSAJES -->
    <c:if test="${not empty mensajeError}">
        <div class="alert alert-error">${mensajeError}</div>
    </c:if>
    <c:if test="${not empty mensajeExito}">
        <div class="alert alert-success">${mensajeExito}</div>
    </c:if>
    <c:if test="${not empty mensajeInfo}">
        <div class="alert alert-info">${mensajeInfo}</div>
    </c:if>

    <!-- CONTENEDOR PRINCIPAL -->
    <div class="container">
        
        <!-- SIDEBAR: Categorías y Filtros -->
        <aside class="sidebar">
            <h3>Categorías</h3>
            <ul class="categorias">
                <li>
                    <a href="FrontController?accion=inicio">Todas</a>
                </li>
                <c:forEach items="${categorias}" var="cat">
                    <li>
                        <a href="FrontController?accion=filtrar&idCategoria=${cat.idcategoria}">
                            ${cat.nombre}
                        </a>
                    </li>
                </c:forEach>
            </ul>

            <!-- FORMULARIO DE BÚSQUEDA -->
            <form action="FrontController" method="post" class="filtros">
                <input type="hidden" name="accion" value="buscar">
                
                <div class="form-group">
                    <label>Buscar producto:</label>
                    <input type="text" name="textoBusqueda" 
                           placeholder="Nombre del producto..."
                           value="${textoBuscado}">
                </div>
                
                <button type="submit" class="btn btn-primary">Buscar</button>
            </form>

            <!-- FILTROS AVANZADOS -->
<!--            <form action="FrontController" method="post" class="filtros">
                <input type="hidden" name="accion" value="filtrar">
                
                <h4>Filtrar por:</h4>
                
                <div class="form-group">
                    <label>Marca:</label>
                    <input type="text" name="marca" 
                           placeholder="Ej: Samsung"
                           value="${marcaSeleccionada}">
                </div>
                
                <div class="form-group">
                    <label>Precio mínimo:</label>
                    <input type="number" name="precioMin" 
                           step="0.01" min="0"
                           value="${precioMinSeleccionado}">
                </div>
                
                <div class="form-group">
                    <label>Precio máximo:</label>
                    <input type="number" name="precioMax" 
                           step="0.01" min="0"
                           value="${precioMaxSeleccionado}">
                </div>
                
                <button type="submit" class="btn btn-secondary">Aplicar filtros</button>
            </form>
        </aside>-->

        <!-- GRID DE PRODUCTOS -->
        <main class="productos-grid">
            <h2>
                <c:choose>
                    <c:when test="${not empty textoBuscado}">
                        Resultados para: "${textoBuscado}"
                    </c:when>
                    <c:when test="${not empty catSeleccionada}">
                        Productos filtrados
                    </c:when>
                    <c:otherwise>
                        Productos destacados
                    </c:otherwise>
                </c:choose>
            </h2>

            <c:choose>
                <c:when test="${empty productos}">
                    <p class="no-productos">No hay productos disponibles.</p>
                </c:when>
                <c:otherwise>
<div class="productos-container">
    <c:forEach items="${productos}" var="prod">
        <article class="producto-card">
            <!-- ENLACE A DETALLE EN LA IMAGEN -->
            <a href="${pageContext.request.contextPath}/FrontController?accion=verDetalle&idproducto=${prod.idproducto}">
                <img src="${pageContext.request.contextPath}/imagenes/imagen/productos/${prod.imagen}" 
                     alt="${prod.nombre}"
                     onerror="this.src='${pageContext.request.contextPath}/imagenes/productos/default.jpg'">
            </a>
            
            <div class="producto-info">
                <!-- ENLACE A DETALLE EN EL TÍTULO -->
                <h3>
                    <a href="${pageContext.request.contextPath}/FrontController?accion=verDetalle&idproducto=${prod.idproducto}">
                        ${prod.nombre}
                    </a>
                </h3>
                <p class="marca">${prod.marca}</p>
                <p class="precio">
                    <fmt:formatNumber value="${prod.precio}" type="currency" currencySymbol="€"/>
                </p>
                
                <form action="CarritoController" method="post" class="form-inline">
                    <input type="hidden" name="accion" value="anadir">
                    <input type="hidden" name="idproducto" value="${prod.idproducto}">
                    <button type="submit" class="btn btn-add-cart">
                        🛒 Añadir al carrito
                    </button>
                </form>
            </div>
        </article>
    </c:forEach>
</div>
                </c:otherwise>
            </c:choose>
        </main>
    </div>

    <!-- FOOTER -->
    <jsp:include page="INC/footer.jsp" />

</body>
</html>
