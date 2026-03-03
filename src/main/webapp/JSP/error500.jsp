<%-- 
    Document   : error500
    Created on : 19 feb. 2026, 23:43:44
    Author     : Alba
--%>

<jsp:directive.page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true"/>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:url var="estilo" value="/CSS/estilo.css" scope="application" />
<c:set var="contexto" value="${pageContext.request.contextPath}" scope="application"/>
<!DOCTYPE html>
<html lang="es">
<head>
       <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error 500</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilo.css">
</head>
<body>

  <jsp:include page="../INC/header.jsp" />

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
        <a href="${pageContext.request.contextPath}/inicio.jsp" class="btn-home">← Ir a la tienda</a>
        <a href="javascript:location.reload()" class="btn-retry">↺ Reintentar</a>
      </div>

    </div>
  </div>

   <jsp:include page="../INC/footer.jsp" />

</body>
</html>