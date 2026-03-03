<%-- 
    Document   : error404
    Created on : 19 feb. 2026, 23:45:11
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
    <title>Error 404</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilo.css">
</head>
<body>
    
    <jsp:include page="../INC/header.jsp" />

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
        <a href="${pageContext.request.contextPath}/inicio.jsp" class="btn-home">← Ir a la tienda</a>
        <a href="javascript:history.back()" class="btn-back">Volver atrás</a>
      </div>

    </div>
  </div>

 <jsp:include page="../INC/footer.jsp" />

</body>
</html>
