<%-- 
    Document   : header
    Created on : 24 feb. 2026, 22:17:44
    Author     : Alba
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header class="header">
    <div class="header-container">
        <!-- LOGO -->
        <div class="logo">
            <a href="${pageContext.request.contextPath}/FrontController?accion=inicio">
                <img src="${pageContext.request.contextPath}/imagenes/logo.png" 
                     alt="Chollotek"
                     onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
                <span style="display:none;">CHOLLOTEK</span>
            </a>
        </div>

        <!-- BUSCADOR RÁPIDO -->
        <div class="header-search">
            <form action="FrontController" method="post">
                <input type="hidden" name="accion" value="buscar">
                <input type="text" name="textoBusqueda" 
                       placeholder="Buscar productos..." 
                       class="search-input">
                <button type="submit" class="btn-search">🔍</button>
            </form>
        </div>

        <!-- NAVEGACIÓN USUARIO -->
        <nav class="header-nav">
            <c:choose>
                <c:when test="${not empty sessionScope.usuario}">
                    <!-- Usuario LOGUEADO -->
                    <div class="user-menu" id="userMenu">
                        <span class="user-avatar">
                            <c:choose>
                                <c:when test="${not empty sessionScope.usuario.avatar}">
                                    <img src="${pageContext.request.contextPath}/avatares/${sessionScope.usuario.avatar}" 
                                         alt="Avatar"
                                         onerror="this.src='${pageContext.request.contextPath}/avatares/default-avatar.jpg'">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/avatares/default-avatar.jpg" alt="Avatar">
                                </c:otherwise>
                            </c:choose>
                        </span>
                        <span class="user-name">Hola, ${sessionScope.usuario.nombre}</span>

                        <div class="user-dropdown" id="userDropdown">
                            <a href="${pageContext.request.contextPath}/FrontController?accion=verPerfil">
                                👤 Mi perfil
                            </a>
                            <a href="${pageContext.request.contextPath}/FrontController?accion=verPedidos">
                                📦 Mis pedidos
                            </a>
                            <a href="${pageContext.request.contextPath}/FrontController?accion=logout" onclick="confirmarLogout(event)">
                                🚪 Cerrar sesión
                            </a>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Usuario NO logueado -->
                    <a href="${pageContext.request.contextPath}/FrontController?accion=verLogin" 
                       class="btn btn-login">
                        Iniciar sesión
                    </a>
                    <a href="${pageContext.request.contextPath}/FrontController?accion=verRegistro" 
                       class="btn btn-register">
                        Registrarse
                    </a>
                </c:otherwise>
            </c:choose>

            <!-- CARRITO -->
            <a href="${pageContext.request.contextPath}/FrontController?accion=verCarrito" 
               class="btn-cart">
                🛒 Carrito
                <c:if test="${not empty sessionScope.carrito}">
                    <span class="cart-count">${sessionScope.carrito.size()}</span>
                </c:if>
            </a>
        </nav>
    </div>

    <!-- MODAL CONFIRMACIÓN LOGOUT -->
    <div id="modalLogout" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%;
         background: rgba(7, 5, 15, 0.85); z-index:9999; align-items:center; justify-content:center;">
        <div style="background:#fff; color: #07050f; border-radius:10px; padding:30px; max-width:380px; width:90%;
             text-align:center; box-shadow:0 4px 20px rgba(0,0,0,0.3);">
            <p style="font-size:1.1rem; margin-bottom:20px;">¿Seguro que quieres cerrar sesión?</p>
            <div style="display:flex; gap:12px; justify-content:center;">
                <a href="${pageContext.request.contextPath}/FrontController?accion=logout"
                   class="btn btn-primary" style="text-decoration:none;">
                    Sí, cerrar sesión
                </a>
                <button onclick="cerrarModal()" class="btn btn-secondary">
                    Cancelar
                </button>
            </div>
        </div>
    </div>

    <script>
        // ── Desplegable: abierto mientras el ratón esté sobre el menú completo ──
        const userMenu = document.getElementById('userMenu');
        const userDropdown = document.getElementById('userDropdown');

        if (userMenu) {
            let closeTimer = null;

            userMenu.addEventListener('mouseenter', function () {
                clearTimeout(closeTimer);
                userDropdown.style.display = 'block';
            });

            userMenu.addEventListener('mouseleave', function () {
                // Pequeño retardo para que no se cierre al pasar entre hijo y padre
                closeTimer = setTimeout(function () {
                    userDropdown.style.display = 'none';
                }, 150);
            });
        }

        // ── Modal de confirmación de logout ──
        function confirmarLogout(e) {
            e.preventDefault();
            const modal = document.getElementById('modalLogout');
            modal.style.display = 'flex';
        }

        function cerrarModal() {
            document.getElementById('modalLogout').style.display = 'none';
        }

        // Cerrar modal al hacer clic fuera
        document.getElementById('modalLogout').addEventListener('click', function (e) {
            if (e.target === this)
                cerrarModal();
        });
    </script>

</header>
