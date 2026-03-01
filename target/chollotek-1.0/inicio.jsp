<%-- Document : index Created on : 6 feb. 2026, 17:47:23 Author : Alba --%>

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
    <style>
        /* ── HERO ── */
        .hero {
            position: relative;
            overflow: hidden;
            background: linear-gradient(135deg, #0d0620 0%, #1a0535 40%, #0a1a3a 100%);
            padding: 5rem 2rem 4rem;
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 2rem;
            min-height: 360px;
        }

        .hero::before {
            content: '';
            position: absolute;
            inset: 0;
            background:
                radial-gradient(ellipse 60% 80% at 20% 50%, rgba(224,64,251,.18), transparent),
                radial-gradient(ellipse 40% 60% at 80% 30%, rgba(0,229,255,.12), transparent);
            pointer-events: none;
        }

        .hero-content {
            position: relative;
            z-index: 1;
            max-width: 520px;
        }

        .hero-eyebrow {
            display: inline-block;
            font-family: 'Orbitron', monospace;
            font-size: .7rem;
            font-weight: 700;
            letter-spacing: .2em;
            text-transform: uppercase;
            color: var(--neon-pink);
            background: rgba(224,64,251,.1);
            border: 1px solid rgba(224,64,251,.3);
            border-radius: 999px;
            padding: .3rem 1rem;
            margin-bottom: 1.25rem;
            text-shadow: 0 0 8px var(--neon-pink);
        }

        .hero-title {
            font-family: 'Orbitron', monospace;
            font-size: clamp(2rem, 4vw, 3.2rem);
            font-weight: 900;
            line-height: 1.15;
            color: #fff;
            margin-bottom: 1rem;
            text-shadow: 0 0 40px rgba(224,64,251,.3);
        }

        .hero-title span {
            color: var(--neon-pink);
            text-shadow: 0 0 24px var(--neon-pink);
        }

        .hero-subtitle {
            font-size: .95rem;
            color: var(--text-muted);
            line-height: 1.7;
            margin-bottom: 2rem;
            max-width: 420px;
        }

        .hero-actions {
            display: flex;
            gap: 1rem;
            flex-wrap: wrap;
        }

        .btn-hero-primary {
            display: inline-flex;
            align-items: center;
            gap: .5rem;
            padding: .75rem 1.75rem;
            border-radius: 999px;
            background: linear-gradient(135deg, var(--neon-pink), var(--neon-violet));
            color: #fff;
            font-family: 'Orbitron', monospace;
            font-size: .75rem;
            font-weight: 700;
            letter-spacing: .08em;
            text-transform: uppercase;
            text-decoration: none;
            border: none;
            cursor: pointer;
            box-shadow: 0 0 24px rgba(224,64,251,.5);
            transition: transform .2s, box-shadow .2s;
        }

        .btn-hero-primary:hover {
            transform: translateY(-3px);
            box-shadow: 0 0 40px rgba(224,64,251,.8);
        }

        .btn-hero-secondary {
            display: inline-flex;
            align-items: center;
            gap: .5rem;
            padding: .75rem 1.75rem;
            border-radius: 999px;
            background: transparent;
            color: var(--text);
            font-family: 'Orbitron', monospace;
            font-size: .75rem;
            font-weight: 700;
            letter-spacing: .08em;
            text-transform: uppercase;
            text-decoration: none;
            border: 1px solid rgba(255,255,255,.2);
            cursor: pointer;
            transition: background .2s, border-color .2s;
        }

        .btn-hero-secondary:hover {
            background: rgba(255,255,255,.06);
            border-color: rgba(255,255,255,.5);
        }

        /* Ilustración decorativa del hero */
        .hero-visual {
            position: relative;
            z-index: 1;
            flex-shrink: 0;
            width: 260px;
            height: 220px;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .hero-visual .monitor {
            font-size: 9rem;
            filter: drop-shadow(0 0 32px rgba(0,229,255,.6));
            animation: heroFloat 4s ease-in-out infinite;
        }

        .hero-visual .glow-ring {
            position: absolute;
            width: 200px;
            height: 200px;
            border-radius: 50%;
            border: 1px solid rgba(0,229,255,.2);
            animation: ringPulse 3s ease-in-out infinite;
        }

        .hero-visual .glow-ring:nth-child(2) {
            width: 160px;
            height: 160px;
            border-color: rgba(224,64,251,.2);
            animation-delay: .75s;
        }

        @keyframes heroFloat {
            0%, 100% { transform: translateY(0); }
            50%       { transform: translateY(-14px); }
        }

        @keyframes ringPulse {
            0%, 100% { opacity: .4; transform: scale(1); }
            50%       { opacity: .15; transform: scale(1.08); }
        }

        /* ── BADGES STATS ── */
        .hero-stats {
            display: flex;
            gap: 1.5rem;
            margin-top: 2rem;
            padding-top: 1.75rem;
            border-top: 1px solid rgba(255,255,255,.07);
            flex-wrap: wrap;
        }

        .stat-item {
            display: flex;
            flex-direction: column;
            gap: .15rem;
        }

        .stat-value {
            font-family: 'Orbitron', monospace;
            font-size: 1.3rem;
            font-weight: 900;
            color: var(--neon-blue);
            text-shadow: 0 0 12px rgba(0,229,255,.5);
        }

        .stat-label {
            font-size: .72rem;
            color: var(--text-muted);
            text-transform: uppercase;
            letter-spacing: .08em;
        }

        /* ── SECCIÓN DESTACADOS ── */
        .section-destacados {
            padding: 3rem 2rem 4rem;
            max-width: 1400px;
            margin: 0 auto;
            width: 100%;
        }

        .section-header {
            display: flex;
            align-items: baseline;
            justify-content: space-between;
            gap: 1rem;
            margin-bottom: 1.75rem;
        }

        .section-title-main {
            font-family: 'Orbitron', monospace;
            font-size: 1.3rem;
            font-weight: 700;
            color: #fff;
            letter-spacing: .1em;
            border-left: 4px solid var(--neon-pink);
            padding-left: .75rem;
            text-shadow: 0 0 12px rgba(224,64,251,.4);
        }

        .section-link {
            font-size: .8rem;
            color: var(--neon-pink);
            text-decoration: none;
            transition: opacity .2s;
            white-space: nowrap;
        }

        .section-link:hover { opacity: .7; }

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
        #toastCarrito.visible  { opacity: 1; }
        #toastCarrito.success  { background: #2e7d32; }
        #toastCarrito.error    { background: #c62828; }

        /* Responsive hero */
        @media (max-width: 768px) {
            .hero { flex-direction: column; padding: 3rem 1.5rem; min-height: auto; }
            .hero-visual { display: none; }
            .hero-stats { gap: 1rem; }
            .section-destacados { padding: 2rem 1rem 3rem; }
        }
    </style>
</head>
<body>

    <!-- HEADER -->
    <jsp:include page="/INC/header.jsp" />

    <!-- MENSAJES -->
    <c:if test="${not empty mensajeError}">
        <div class="alert alert-error">${mensajeError}</div>
    </c:if>
    <c:if test="${not empty mensajeExito}">
        <div class="alert alert-success">${mensajeExito}</div>
    </c:if>

    <!-- ══════════════════════════════
         HERO
         ══════════════════════════════ -->
    <section class="hero">
        <div class="hero-content">
            <span class="hero-eyebrow">✦ Novedades y chollos</span>
            <h1 class="hero-title">
                ¡Tu PC Despega<br>con <span>Chollotek!</span>
            </h1>
            <p class="hero-subtitle">
                Encuentra los mejores componentes y periféricos al mejor precio.
                Calidad garantizada, envío rápido.
            </p>
            <div class="hero-actions">
                <a href="${pageContext.request.contextPath}/FrontController?accion=filtrar"
                   class="btn-hero-primary">
                    🔥 Ver ofertas
                </a>
                <a href="${pageContext.request.contextPath}/FrontController?accion=verRegistro"
                   class="btn-hero-secondary">
                    Crear cuenta
                </a>
            </div>
            <div class="hero-stats">
                <div class="stat-item">
                    <span class="stat-value">+500</span>
                    <span class="stat-label">Productos</span>
                </div>
                <div class="stat-item">
                    <span class="stat-value">24h</span>
                    <span class="stat-label">Envío</span>
                </div>
                <div class="stat-item">
                    <span class="stat-value">100%</span>
                    <span class="stat-label">Garantía</span>
                </div>
            </div>
        </div>

        <div class="hero-visual">
            <div class="glow-ring"></div>
            <div class="glow-ring"></div>
            <span class="monitor">🖥️</span>
        </div>
    </section>

    <!-- ══════════════════════════════
         PRODUCTOS DESTACADOS
         ══════════════════════════════ -->
    <section class="section-destacados">
        <div class="section-header">
            <h2 class="section-title-main">⚡ Productos destacados</h2>
            <a href="${pageContext.request.contextPath}/FrontController?accion=filtrar"
               class="section-link">Ver todo el catálogo →</a>
        </div>

        <c:choose>
            <c:when test="${empty productos}">
                <p class="no-productos">No hay productos disponibles en este momento.</p>
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
    </section>

    <!-- FOOTER -->
    <jsp:include page="/INC/footer.jsp" />

    <div id="toastCarrito"></div>

    <script>
        var contextPath = '${pageContext.request.contextPath}';

        function anadirAlCarritoAjax(idProducto, nombreProducto) {
            fetch(contextPath + '/AjaxController?accion=anadirAjax&idproducto=' + idProducto, {
                method: 'POST'
            })
            .then(function(r) { return r.json(); })
            .then(function(data) {
                if (data.exito) {
                    mostrarToast('✓ "' + nombreProducto + '" añadido al carrito', 'success');
                } else {
                    mostrarToast('✗ ' + (data.error || 'Error al añadir'), 'error');
                }
            })
            .catch(function() { mostrarToast('✗ Error de conexión', 'error'); });
        }

        function mostrarToast(mensaje, tipo) {
            var toast = document.getElementById('toastCarrito');
            toast.textContent = mensaje;
            toast.className = 'visible ' + tipo;
            clearTimeout(toast._timer);
            toast._timer = setTimeout(function() { toast.className = ''; }, 3000);
        }
    </script>

</body>
</html>