<%-- Document : detalle-producto Created on : 22 feb. 2026, 20:18:31 Author : Alba --%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
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
        <style>
            /* ── TOAST NOTIFICACIÓN ── */
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

        <!-- MENSAJES -->
        <c:if test="${not empty mensajeError}">
            <div class="container container-detalle">
                <div class="alert alert-error">${mensajeError}</div>
            </div>
        </c:if>
        <c:if test="${not empty mensajeExito}">
            <div class="container container-detalle">
                <div class="alert alert-success">${mensajeExito}</div>
            </div>
        </c:if>

        <!-- BREADCRUMB -->
        <div class="container container-detalle">
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
        <div class="container container-detalle">
            <div class="detalle-producto">

                <!-- IMAGEN DEL PRODUCTO -->
                <div class="detalle-imagen">
                    <div class="imagen-principal">
                        <img src="${pageContext.request.contextPath}/imagenes/imagen/productos/${producto.imagen}.jpg"
                             alt="${producto.nombre}"
                             onerror="this.src='${pageContext.request.contextPath}/imagenes/imagen/productos/default.jpg'">
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
                            <fmt:formatNumber value="${producto.precio}" type="currency" currencySymbol="€" />
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
                    <div class="form-anadir-carrito">
                        <div class="cantidad-selector">
                            <label for="cantidad">Cantidad:</label>
                            <div class="cantidad-input">
                                <button type="button" class="btn-cantidad" onclick="decrementarCantidad()">-</button>
                                <input type="number" id="cantidad" name="cantidad" value="1" min="1" max="99">
                                <button type="button" class="btn-cantidad" onclick="incrementarCantidad()">+</button>
                            </div>
                        </div>

                        <button type="button" 
                                class="btn btn-primary btn-block btn-comprar"
                                onclick="anadirAlCarritoConCantidad(${producto.idproducto}, '${producto.nombre}')">
                            🛒 Añadir al carrito
                        </button>

                        <a href="${pageContext.request.contextPath}/FrontController?accion=inicio"
                           class="btn btn-secondary btn-block" style="margin-top: 10px;">
                            ↩ Volver a la tienda
                        </a>
                    </div>

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
        </div>

        <!-- FOOTER -->
        <jsp:include page="../INC/footer.jsp" />

        <!-- Toast de notificación -->
        <div id="toastCarrito"></div>

        <script>
        var contextPath = '${pageContext.request.contextPath}';

        // ═════════════════════════════════════════════════
        // CONTROLES DE CANTIDAD
        // ═════════════════════════════════════════════════

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

        // ═════════════════════════════════════════════════
        // AÑADIR AL CARRITO CON CANTIDAD (desde detalle)
        // ═════════════════════════════════════════════════

        function anadirAlCarritoConCantidad(idProducto, nombreProducto) {
            var cantidad = parseInt(document.getElementById('cantidad').value) || 1;

            // ✅ FIX: Una sola petición enviando la cantidad como parámetro.
            // Antes se hacían N peticiones en paralelo (una por unidad),
            // lo que provocaba condiciones de carrera en sesión y perdía unidades.
            fetch(contextPath + '/AjaxController?accion=anadirAjax&idproducto=' + idProducto + '&cantidad=' + cantidad, {
                method: 'POST'
            })
                    .then(function (r) {
                        return r.json();
                    })
                    .then(function (data) {
                        if (data.exito) {
                            if (cantidad === 1) {
                                mostrarToast('✓ "' + nombreProducto + '" añadido al carrito', 'success');
                            } else {
                                mostrarToast('✓ ' + cantidad + ' unidades de "' + nombreProducto + '" añadidas', 'success');
                            }
                        } else {
                            mostrarToast('✗ ' + (data.error || 'Error al añadir'), 'error');
                        }
                    })
                    .catch(function () {
                        mostrarToast('✗ Error de conexión', 'error');
                    });
        }

        // ═════════════════════════════════════════════════
        // AÑADIR AL CARRITO SIMPLE (desde relacionados)
        // ═════════════════════════════════════════════════

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

        // ═════════════════════════════════════════════════
        // MOSTRAR TOAST (mensaje flotante)
        // ═════════════════════════════════════════════════

        function mostrarToast(mensaje, tipo) {
            var toast = document.getElementById('toastCarrito');
            toast.textContent = mensaje;
            toast.className = 'visible ' + tipo;

            // Limpiar el timer anterior si existe
            clearTimeout(toast._timer);

            // Ocultar después de 3 segundos
            toast._timer = setTimeout(function () {
                toast.className = '';
            }, 3000);
        }
        </script>

    </body>

</html>