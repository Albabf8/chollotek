<%-- Document : index Created on : 6 feb. 2026, 17:47:23 Author : Alba --%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
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
                    <c:forEach items="${applicationScope.categorias}" var="cat">
                        <li>
                            <a href="FrontController?accion=filtrar&idCategoria=${cat.idcategoria}">
                                ${cat.nombre}
                            </a>
                        </li>
                    </c:forEach>
                </ul>

                <!-- FILTROS AVANZADOS -->
                <form action="FrontController" method="post" class="filtros">
                    <input type="hidden" name="accion" value="filtrar">

                    <h4>Filtrar por:</h4>

                    <div class="form-group">
                        <label>Marca:</label>
                        <select name="marca">
                            <option value="">Todas las marcas</option>
                            <c:forEach items="${applicationScope.marcas}" var="m">
                                <option value="${m}" ${marcaSeleccionada==m ? 'selected' : '' }>
                                    ${m}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <!-- PRECIO MÍNIMO -->
                    <div class="form-group">
                        <label>Precio mínimo (€):</label>
                        <input type="number" name="precioMin" step="0.01" min="${applicationScope.precioMinGlobal}"
                               max="${applicationScope.precioMaxGlobal}"
                               value="${not empty precioMinSeleccionado ? precioMinSeleccionado : applicationScope.precioMinGlobal}">
                    </div>

                    <!-- PRECIO MÁXIMO -->
                    <div class="form-group">
                        <label>Precio máximo (€):</label>
                        <input type="number" name="precioMax" step="0.01" min="${applicationScope.precioMinGlobal}"
                               max="${applicationScope.precioMaxGlobal}"
                               value="${not empty precioMaxSeleccionado ? precioMaxSeleccionado : applicationScope.precioMaxGlobal}">
                    </div>

                    <button type="submit" class="btn btn-secondary">Aplicar filtros</button>
                </form>
            </aside>

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
                            <c:forEach items="${productos}" var="prod" end="7">
                                <article class="producto-card">
                                    <!-- ENLACE A DETALLE EN LA IMAGEN -->
                                    <a
                                        href="${pageContext.request.contextPath}/FrontController?accion=verDetalle&idproducto=${prod.idproducto}">
                                        <img src="${pageContext.request.contextPath}/imagenes/imagen/productos/${prod.imagen}.jpg"
                                             alt="${prod.nombre}"
                                             onerror="this.src='${pageContext.request.contextPath}/imagenes/imagen/productos/default.jpg'">
                                    </a>

                                    <div class="producto-info">
                                        <!-- ENLACE A DETALLE EN EL TÍTULO -->
                                        <h3>
                                            <a
                                                href="${pageContext.request.contextPath}/FrontController?accion=verDetalle&idproducto=${prod.idproducto}">
                                                ${prod.nombre}
                                            </a>
                                        </h3>
                                        <p class="marca">${prod.marca}</p>
                                        <p class="precio">
                                            <fmt:formatNumber value="${prod.precio}" type="currency"
                                                              currencySymbol="€" />
                                        </p>

                                        <form onsubmit="anadirCarritoAjax(event, '${prod.idproducto}')"
                                              class="form-inline">
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

        <!-- Toast de Notificación -->
        <div id="toast-container" style="position: fixed; bottom: 20px; right: 20px; z-index: 1000;"></div>

        <script>
            function anadirCarritoAjax(event, idProducto) {
                event.preventDefault(); // Evita redirección

                var formBody = new URLSearchParams();
                formBody.append("accion", "anadir");
                formBody.append("idproducto", idProducto);
                formBody.append("ajax", "true"); // Importante para que el controller lo detecte

                fetch("${pageContext.request.contextPath}/CarritoController", {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: formBody.toString()
                })
                        .then(function (response) {
                            return response.json();
                        })
                        .then(function (data) {
                            if (data.exito) {
                                mostrarToast("🛒 Producto añadido al carrito correctamente", "success");
                            } else {
                                mostrarToast("❌ " + data.error, "error");
                            }
                        })
                        .catch(function (error) {
                            mostrarToast("❌ Error al añadir producto", "error");
                        });
            }

            function mostrarToast(mensaje, tipo) {
                var toast = document.createElement("div");
                toast.className = "alert alert-" + tipo;
                toast.style.marginBottom = "10px";
                toast.style.boxShadow = "0 4px 6px rgba(0,0,0,0.1)";
                toast.innerText = mensaje;

                var container = document.getElementById("toast-container");
                container.appendChild(toast);

                setTimeout(function () {
                    toast.style.opacity = '0';
                    toast.style.transition = 'opacity 0.5s ease';
                    setTimeout(function () {
                        toast.remove();
                    }, 500);
                }, 3000);
            }
        </script>
    </body>

</html>