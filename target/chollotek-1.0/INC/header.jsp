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
                    <div class="user-menu">
                        <span class="user-avatar">
                            <c:choose>
                                <c:when test="${not empty sessionScope.usuario.avatar}">
                                    <img src="${pageContext.request.contextPath}/avatares/${sessionScope.usuario.avatar}" 
                                         alt="Avatar"
                                         onerror="this.src='${pageContext.request.contextPath}/imagenes/default-avatar.png'">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/imagenes/default-avatar.png" alt="Avatar">
                                </c:otherwise>
                            </c:choose>
                        </span>
                        <span class="user-name">Hola, ${sessionScope.usuario.nombre}</span>
                        
                        <div class="user-dropdown">
                            <a href="${pageContext.request.contextPath}/FrontController?accion=verPerfil">
                                👤 Mi perfil
                            </a>
                            <a href="${pageContext.request.contextPath}/FrontController?accion=verPedidos">
                                📦 Mis pedidos
                            </a>
                            <a href="${pageContext.request.contextPath}/FrontController?accion=logout">
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
</header>
