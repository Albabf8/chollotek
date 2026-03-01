<%-- Document : carrito Created on : 20 feb. 2026, 0:42:27 Author : Alba --%>
    <%@ page contentType="text/html" pageEncoding="UTF-8" %>
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

                    <jsp:include page="../INC/header.jsp" />

                    <div class="container">
                        <h2>🛒 Mi carrito</h2>

                        <!-- MENSAJES -->
                        <div id="mensajeCarrito"></div>
                        <c:if test="${not empty mensajeError}">
                            <div class="alert alert-error">${mensajeError}</div>
                        </c:if>
                        <c:if test="${not empty mensajeExito}">
                            <div class="alert alert-success">${mensajeExito}</div>
                        </c:if>

                        <c:choose>
                            <c:when test="${empty lineasCarrito}">
                                <div class="carrito-vacio" id="carritoVacio">
                                    <p>Tu carrito está vacío</p>
                                    <a href="${pageContext.request.contextPath}/FrontController?accion=inicio"
                                        class="btn btn-primary">
                                        Ir a comprar
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div id="carritoContenido">
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
                                        <tbody id="tablaCarritoBody">
                                            <c:set var="subtotal" value="0" />
                                            <c:forEach items="${lineasCarrito}" var="linea">
                                                <c:set var="subtotalLinea"
                                                    value="${linea.producto.precio * linea.cantidad}" />
                                                <c:set var="subtotal" value="${subtotal + subtotalLinea}" />

                                                <%-- data-precio para calcular subtotal de línea en JS --%>
                                                    <tr id="fila-${linea.idlinea}-${linea.idproducto}"
                                                        data-precio="${linea.producto.precio}">
                                                        <td>
                                                            <div class="producto-carrito">
                                                                <img src="${pageContext.request.contextPath}/imagenes/imagen/productos/${linea.producto.imagen}.jpg"
                                                                    alt="${linea.producto.nombre}"
                                                                    onerror="this.src='${pageContext.request.contextPath}/imagenes/imagen/productos/default.jpg'">
                                                                <div>
                                                                    <strong>${linea.producto.nombre}</strong>
                                                                    <small>${linea.producto.marca}</small>
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <fmt:formatNumber value="${linea.producto.precio}"
                                                                type="currency" currencySymbol="€" />
                                                        </td>
                                                        <td>
                                                            <div class="cantidad-controls"
                                                                data-linea="${linea.idlinea}">
                                                                <button type="button" class="btn-cantidad"
                                                                    onclick="restarCantidadAjax(${linea.idlinea}, ${linea.cantidad}, ${linea.idproducto})">
                                                                    -
                                                                </button>
                                                                <span class="cantidad"
                                                                    id="cantidad-${linea.idlinea}-${linea.idproducto}">
                                                                    ${linea.cantidad}
                                                                </span>
                                                                <button type="button" class="btn-cantidad"
                                                                    onclick="sumarCantidadAjax(${linea.idlinea}, ${linea.cantidad}, ${linea.idproducto})">
                                                                    +
                                                                </button>
                                                            </div>
                                                        </td>
                                                        <td>
                                                            <strong id="subtotal-${linea.idlinea}-${linea.idproducto}">
                                                                <fmt:formatNumber value="${subtotalLinea}"
                                                                    type="currency" currencySymbol="€" />
                                                            </strong>
                                                        </td>
                                                        <td>
                                                            <form action="CarritoController" method="post"
                                                                class="form-inline">
                                                                <input type="hidden" name="accion"
                                                                    value="eliminarProducto">
                                                                <input type="hidden" name="idlinea"
                                                                    value="${linea.idlinea}">
                                                                <input type="hidden" name="idproducto"
                                                                    value="${linea.idproducto}">
                                                                <button type="submit" class="btn btn-danger btn-sm">🗑️
                                                                    Eliminar</button>
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
                                                <span id="resumenSubtotal">
                                                    <fmt:formatNumber value="${subtotal}" type="currency"
                                                        currencySymbol="€" />
                                                </span>
                                            </div>

                                            <c:set var="iva" value="${subtotal * 0.21}" />
                                            <div class="resumen-linea">
                                                <span>IVA (21%):</span>
                                                <span id="resumenIva">
                                                    <fmt:formatNumber value="${iva}" type="currency"
                                                        currencySymbol="€" />
                                                </span>
                                            </div>

                                            <div class="resumen-linea resumen-total">
                                                <span>TOTAL:</span>
                                                <span id="resumenTotal">
                                                    <fmt:formatNumber value="${subtotal + iva}" type="currency"
                                                        currencySymbol="€" />
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
                                                            <a
                                                                href="${pageContext.request.contextPath}/FrontController?accion=verLogin">
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

                                                <a href="${pageContext.request.contextPath}/FrontController?accion=inicio"
                                                    class="btn btn-secondary btn-block" style="margin-top: 10px;">
                                                    ↩ Seguir comprando
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="carrito-vacio" id="carritoVacio" style="display:none;">
                                    <p>Tu carrito está vacío</p>
                                    <a href="${pageContext.request.contextPath}/FrontController?accion=inicio"
                                        class="btn btn-primary">
                                        Ir a comprar
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <jsp:include page="../INC/footer.jsp" />

                    <script src="${pageContext.request.contextPath}/JS/validaciones.js"></script>
                    <script>
                        var contextPath = '${pageContext.request.contextPath}';

                        function sumarCantidadAjax(idLinea, cantidadActual, idProducto) {
                            fetch(contextPath + '/AjaxController?accion=sumarCantidad'
                                + '&idlinea=' + idLinea
                                + '&cantidadActual=' + cantidadActual
                                + '&idproducto=' + idProducto)
                                .then(function (r) { return r.json(); })
                                .then(function (data) {
                                    if (data.exito) {
                                        var nuevaCantidad = data.nuevaCantidad;
                                        var key = idLinea + '-' + idProducto;
                                        document.getElementById('cantidad-' + key).textContent = nuevaCantidad;
                                        actualizarSubtotalLinea(key, nuevaCantidad);
                                        actualizarBotones(idLinea, nuevaCantidad, idProducto);
                                        mostrarMensaje('Cantidad actualizada', 'success');
                                    } else {
                                        mostrarMensaje('Error: ' + data.error, 'error');
                                    }
                                })
                                .catch(function () { mostrarMensaje('Error de conexión', 'error'); });
                        }

                        function restarCantidadAjax(idLinea, cantidadActual, idProducto) {
                            fetch(contextPath + '/AjaxController?accion=restarCantidad'
                                + '&idlinea=' + idLinea
                                + '&cantidadActual=' + cantidadActual
                                + '&idproducto=' + idProducto)
                                .then(function (r) { return r.json(); })
                                .then(function (data) {
                                    if (data.exito) {
                                        if (data.eliminado) {
                                            eliminarFila(idLinea, idProducto);
                                            mostrarMensaje('Producto eliminado del carrito', 'info');
                                        } else {
                                            var nuevaCantidad = data.nuevaCantidad;
                                            var key = idLinea + '-' + idProducto;
                                            document.getElementById('cantidad-' + key).textContent = nuevaCantidad;
                                            actualizarSubtotalLinea(key, nuevaCantidad);
                                            actualizarBotones(idLinea, nuevaCantidad, idProducto);
                                            mostrarMensaje('Cantidad actualizada', 'success');
                                        }
                                    } else {
                                        mostrarMensaje('Error: ' + data.error, 'error');
                                    }
                                })
                                .catch(function () { mostrarMensaje('Error de conexión', 'error'); });
                        }

                        function eliminarFila(idLinea, idProducto) {
                            var fila = document.getElementById('fila-' + idLinea + '-' + idProducto);
                            if (fila) fila.remove();

                            var tbody = document.getElementById('tablaCarritoBody');
                            if (tbody && tbody.rows.length === 0) {
                                document.getElementById('carritoContenido').style.display = 'none';
                                document.getElementById('carritoVacio').style.display = 'block';
                            }
                        }

                        function actualizarSubtotalLinea(key, nuevaCantidad) {
                            var fila = document.getElementById('fila-' + key);
                            if (!fila) return;
                            var precio = parseFloat(fila.getAttribute('data-precio'));
                            document.getElementById('subtotal-' + key).textContent =
                                formatearPrecio(precio * nuevaCantidad);
                            recalcularResumen();
                        }

                        function actualizarBotones(idLinea, nuevaCantidad, idProducto) {
                            var controls = document.querySelector('[data-linea="' + idLinea + '"]');
                            if (!controls) return;
                            var botones = controls.querySelectorAll('.btn-cantidad');
                            if (botones.length >= 2) {
                                botones[0].setAttribute('onclick',
                                    'restarCantidadAjax(' + idLinea + ',' + nuevaCantidad + ',' + idProducto + ')');
                                botones[1].setAttribute('onclick',
                                    'sumarCantidadAjax(' + idLinea + ',' + nuevaCantidad + ',' + idProducto + ')');
                            }
                        }

                        // Recalcula el resumen sumando los subtotales visibles en la tabla
                        function recalcularResumen() {
                            var subtotal = 0;
                            document.querySelectorAll('[id^="subtotal-"]').forEach(function (el) {
                                var texto = el.textContent.trim().replace(',', '.').replace('€', '').trim();
                                subtotal += parseFloat(texto) || 0;
                            });
                            var iva = subtotal * 0.21;
                            document.getElementById('resumenSubtotal').textContent = formatearPrecio(subtotal);
                            document.getElementById('resumenIva').textContent = formatearPrecio(iva);
                            document.getElementById('resumenTotal').textContent = formatearPrecio(subtotal + iva);
                        }

                        function formatearPrecio(valor) {
                            return parseFloat(valor).toFixed(2).replace('.', ',') + ' €';
                        }

                        function mostrarMensaje(mensaje, tipo) {
                            var zona = document.getElementById('mensajeCarrito');
                            zona.innerHTML = '<div class="alert alert-' + tipo + '">' + mensaje + '</div>';
                            setTimeout(function () { zona.innerHTML = ''; }, 3000);
                        }
                    </script>
                </body>

                </html>