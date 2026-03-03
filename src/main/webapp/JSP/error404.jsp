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
        <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 60vh; text-align: center;">
            <img src="${pageContext.request.contextPath}/IMAGE/error404.png" 
                 alt="Error 404" 
                 style="width: 800px; max-width: 90%; height: auto; margin-bottom: 2rem; margin-top: 2rem">
        </div>

        <div class="error-actions" style="margin-bottom: 3rem;">
            <a href="${pageContext.request.contextPath}/inicio.jsp" class="btn-home">← Ir a la tienda</a>
            <a href="javascript:history.back()" class="btn-back">Volver atrás</a>
        </div>

        <jsp:include page="../INC/footer.jsp" />

    </body>
</html>
