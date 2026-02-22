<%-- 
    Document   : error500
    Created on : 19 feb. 2026, 23:43:44
    Author     : Alba
--%>

<jsp:directive.page contentType="text/html" pageEncoding="UTF-8"/>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:url var="estilo" value="/CSS/estilo.css" scope="application" />
<c:set var="contexto" value="${pageContext.request.contextPath}" scope="application"/>
<!DOCTYPE html>
<html lang="es">
<head>
        <jsp:include page="/INC/cabecera.jsp">
            <jsp:param name="titulo" value="Chollotek" />
            <jsp:param name="estilo" value="${estilo}" />
        </jsp:include>
</head>
<body>

  <!-- NAV -->
  <nav>
    <div class="logo">CHOLLOTEK</div>
    <div class="search-bar">
      <input type="text" placeholder="Buscar productos…" />
    </div>
    <div class="nav-actions">
      <button class="nav-btn"><span>👤</span> Mi cuenta</button>
      <button class="nav-btn"><span>🛒</span> Mi carrito</button>
    </div>
  </nav>

  <!-- ERROR 500 -->
  <div class="error-page">
    <div class="error-content">

      <div class="error-code">500</div>
      <div class="error-icon">⚠️</div>
      <div class="error-title">Error interno del servidor</div>
      <div class="error-desc">
        Algo ha ido mal en el servidor. Nuestro equipo ya ha sido notificado.
        Por favor, inténtalo de nuevo en unos momentos.
      </div>

      <div class="error-details-box">
        <span class="detail-label">Detalles del error</span>
        <%-- Con isErrorPage="true" podemos acceder al objeto exception --%>
        <code>
          Error 500 — Internal Server Error<br/>
          <c:choose>
            <c:when test="${not empty pageContext.exception}">
              ${pageContext.exception.class.name}: ${pageContext.exception.message}
            </c:when>
            <c:otherwise>
              javax.servlet.ServletException: Error al procesar la solicitud.
            </c:otherwise>
          </c:choose>
          <br/>Contacta con soporte si el problema persiste.
        </code>
      </div>

      <div class="error-actions">
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn-home">← Ir a la tienda</a>
        <a href="javascript:location.reload()" class="btn-retry">↺ Reintentar</a>
      </div>

    </div>
  </div>

  <!-- FOOTER -->
  <footer>
    <div class="footer-left">
      <strong>Autor: Alba Barroso</strong><br />
      Desarrollo de aplicaciones web<br />
      2025/2026
    </div>
    <div class="footer-right">
      <span style="color:var(--text-muted);">© 2026 Chollotek S.L</span><br />
      <a href="#">Aviso legal</a>
      <a href="#">Política de cookies</a>
    </div>
  </footer>

</body>
</html>