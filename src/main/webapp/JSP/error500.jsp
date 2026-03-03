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
        <div class="error-icon">
            <img src="${pageContext.request.contextPath}/IMG/error404.png" 
                 alt="Error 404" 
                 style="width: 200px; height: auto;">
        </div>

        <div class="error-actions">
            <a href="${pageContext.request.contextPath}/inicio.jsp" class="btn-home">← Ir a la tienda</a>
            <a href="javascript:location.reload()" class="btn-retry">↺ Reintentar</a>
        </div>

        <jsp:include page="../INC/footer.jsp" />

    </body>
</html>