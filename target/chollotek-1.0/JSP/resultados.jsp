<%-- 
    Document   : resultados
    Created on : 1 mar. 2026, 20:25:01
    Author     : Alba
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>
            <c:choose>
                <c:when test="${not empty textoBuscado}">Resultados: "${textoBuscado}" — Chollotek</c:when>
                <c:otherwise>Catálogo — Chollotek</c:otherwise>
            </c:choose>
        </title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilo.css">
        <style>
            /* ── TOAST ── */
            #toastCarrito {
                position: fixed;
                bottom: 1.5rem;
                right: 1.5rem;
                padding: .75rem 1.25rem;
                border-radius: 6px;
                font-size: .9rem;
                color: #fff;
                opacity: 0;
                pointer-events: none;
                transition: opacity .3s ease;
                z-index: 9999;
            }
            #toastCarrito.visible  {
                opacity: 1;
            }
            #toastCarrito.success  {
                background: #2e7d32;
            }
            #toastCarrito.error    {
                background: #c62828;
            }
        </style>
    </head>
    <body>

        <!-- HEADER -->
        <jsp:include page="../INC/header.jsp" />

        <!-- MENSAJES FULL-WIDTH -->
        <c:if test="${not empty mensajeError}">
            <div class="alert alert-error">${mensajeError}</div>
        </c:if>
        <c:if test="${not empty mensajeExito}">
            <div class="alert alert-success">${mensajeExito}</div>
        </c:if>
        <c:if test="${not empty mensajeInfo}">
            <div class="alert alert-info">${mensajeInfo}</div>
        </c:if>

        <!-- ══════════════════════════════
             LAYOUT PRINCIPAL: sidebar + grid
             ══════════════════════════════ -->
        <div class="container">

            <!-- SIDEBAR: Categorías y Filtros -->
            <aside class="sidebar">

                <h3>Categorías</h3>
                <ul class="categorias">
                    <li>
                        <a href="${pageContext.request.contextPath}/FrontController?accion=filtrar"
                           class="${empty catSeleccionada ? 'active' : ''}">
                            Todas
                        </a>
                    </li>
                    <c:forEach items="${applicationScope.categorias}" var="cat">
                        <li>
                            <a href="${pageContext.request.contextPath}/FrontController?accion=filtrar&idCategoria=${cat.idcategoria}"
                               class="${catSeleccionada == cat.idcategoria ? 'active' : ''}">
                                ${cat.nombre}
                            </a>
                        </li>
                    </c:forEach>
                </ul>

                <!-- Filtros avanzados -->
                <fmt:setLocale value="en_US"/>
                <fmt:formatNumber var="pMin" value="${applicationScope.precioMinGlobal}" pattern="0.00"/>
                <fmt:formatNumber var="pMax" value="${applicationScope.precioMaxGlobal}" pattern="0.00"/>

                <form action="${pageContext.request.contextPath}/FrontController" method="post" class="filtros">
                    <input type="hidden" name="accion" value="filtrar">

                    <h4>Filtrar por:</h4>

                    <div class="form-group">
                        <label>Marca:</label>
                        <select name="marca">
                            <option value="">Todas las marcas</option>
                            <c:forEach items="${applicationScope.marcas}" var="m">
                                <option value="${m}" ${marcaSeleccionada == m ? 'selected' : ''}>
                                    ${m}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Precio mínimo (€):</label>
                        <input type="number" name="precioMin" step="0.01"
                               min="${pMin}" max="${pMax}"
                               value="${not empty precioMinSeleccionado ? precioMinSeleccionado : pMin}">
                    </div>

                    <div class="form-group">
                        <label>Precio máximo (€):</label>
                        <input type="number" name="precioMax" step="0.01"
                               min="${pMin}" max="${pMax}"
                               value="${not empty precioMaxSeleccionado ? precioMaxSeleccionado : pMax}">
                    </div>

                    <button type="submit" class="btn btn-secondary">Aplicar filtros</button>
                </form>

            </aside>

            <!-- ÁREA DE RESULTADOS -->
            <main class="productos-grid">

                <!-- Cabecera con título y contador -->
                <div style="display:flex; align-items:baseline; justify-content:space-between;
                     gap:1rem; margin-bottom:1.25rem;">
                    <h2 class="section-title" style="margin-bottom:0; border-left:4px solid var(--neon-pink);
                        padding-left:.75rem;">
                        <c:choose>
                            <c:when test="${not empty textoBuscado}">
                                Resultados para: &ldquo;${textoBuscado}&rdquo;
                            </c:when>
                            <c:otherwise>
                                Productos filtrados
                            </c:otherwise>
                        </c:choose>
                    </h2>
                    <c:if test="${not empty productos}">
                        <span style="font-size:.82rem; color:var(--text-muted); white-space:nowrap;">
                            ${fn:length(productos)} resultado<c:if test="${fn:length(productos) != 1}">s</c:if>
                            </span>
                    </c:if>
                </div>

                <c:choose>
                    <c:when test="${empty productos}">
                        <p class="no-productos">No hay productos disponibles.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="productos-container">
                            <c:forEach items="${productos}" var="prod">
                                <article class="producto-card">
                                    <a href="${pageContext.request.contextPath}/FrontController?accion=verDetalle&idproducto=${prod.idproducto}">
                                        <img src="${pageContext.request.contextPath}/imagenes/imagen/productos/${prod.imagen}.jpg"
                                             alt="${prod.nombre}"
                                             onerror="this.src='${pageContext.request.contextPath}/imagenes/imagen/productos/default.jpg'">
                                    </a>
                                    <div class="producto-info">
                                        <h3>
                                            <a href="${pageContext.request.contextPath}/FrontController?accion=verDetalle&idproducto=${prod.idproducto}">
                                                ${prod.nombre}
                                            </a>
                                        </h3>
                                        <p class="marca">${prod.marca}</p>
                                        <p class="precio">
                                            <fmt:formatNumber value="${prod.precio}" type="currency" currencySymbol="€"/>
                                        </p>
                                        <button type="button"
                                                class="btn btn-add-cart"
                                                onclick="anadirAlCarritoAjax(${prod.idproducto}, '${prod.nombre}')">
                                            🛒 Añadir al carrito
                                        </button>
                                    </div>
                                </article>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>

            </main>
        </div>

        <!-- FOOTER -->
        <jsp:include page="../INC/footer.jsp" />

        <div id="toastCarrito"></div>

        <script>
            var contextPath = '${pageContext.request.contextPath}';

            /**
             * Realiza una petición asíncrona (AJAX) para añadir un producto al carrito de compras.
             * * @param {number|string} idProducto - El identificador único del producto.
             * @param {string} nombreProducto - El nombre del producto para mostrar en la notificación.
             */
            function anadirAlCarritoAjax(idProducto, nombreProducto) {
                fetch(contextPath + '/AjaxController?accion=anadirAjax&idproducto=' + idProducto, {
                    method: 'POST'
                })
                        .then(function (r) {
                            return r.json();
                        })
                        .then(function (data) {
                            if (data.exito) {
                                mostrarToast('✓ "' + nombreProducto + '" añadido al carrito', 'success');
                            } else {
                                mostrarToast('✗ ' + (data.error || 'Error al añadir'), 'error');
                            }
                        })
                        .catch(function () {
                            mostrarToast('✗ Error de conexión', 'error');
                        });
            }

            /**
             * Muestra una notificación temporal (Toast) en la interfaz de usuario.
             * * @param {string} mensaje - El texto que se mostrará en la notificación.
             * @param {string} tipo - La clase CSS para el estilo ('success' o 'error').
             */
            function mostrarToast(mensaje, tipo) {
                var toast = document.getElementById('toastCarrito');
                toast.textContent = mensaje;
                toast.className = 'visible ' + tipo;
                clearTimeout(toast._timer);
                toast._timer = setTimeout(function () {
                    toast.className = '';
                }, 3000);
            }
        </script>

    </body>
</html>

