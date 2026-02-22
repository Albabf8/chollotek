<%-- 
    Document   : error404
    Created on : 19 feb. 2026, 23:45:11
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

  <!-- ERROR 404 -->
  <div class="error-page">
    <div class="error-content">

      <div class="error-code">404</div>
      <div class="error-icon">🔍</div>
      <div class="error-title">Página no encontrada</div>
      <div class="error-desc">
        La página que estás buscando no existe, ha sido movida o la URL no es correcta.
        No te preocupes, puedes volver a la tienda y seguir buscando chollos.
      </div>

      <div class="error-actions">
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn-home">← Ir a la tienda</a>
        <a href="javascript:history.back()" class="btn-back">Volver atrás</a>
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
