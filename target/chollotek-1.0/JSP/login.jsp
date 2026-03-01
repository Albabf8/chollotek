<%-- 
    Document   : inicioSesion
    Created on : 20 feb. 2026, 0:32:48
    Author     : Alba
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar sesión - Chollotek</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilo.css">
</head>
<body>
    
    <jsp:include page="../INC/header.jsp" />

    <div class="container-form">
        <div class="form-card">
            <h2>Iniciar sesión</h2>

            <!-- MENSAJES -->
            <c:if test="${not empty mensajeError}">
                <div class="alert alert-error">${mensajeError}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/LoginController" 
                  method="post" 
                  class="form-login">
                
                <div class="form-group">
                    <label for="email">Email:</label>
                    <input type="email" 
                           id="email" 
                           name="email" 
                           value="${emailIntroducido}"
                           required 
                           autofocus>
                </div>

                <div class="form-group">
                    <label for="password">Contraseña:</label>
                    <input type="password" 
                           id="password" 
                           name="password" 
                           required>
                </div>

                <button type="submit" class="btn btn-primary btn-block">
                    Iniciar sesión
                </button>

                <p class="text-center">
                    ¿No tienes cuenta? 
                    <a href="${pageContext.request.contextPath}/FrontController?accion=verRegistro">
                        Regístrate aquí
                    </a>
                </p>

                <div style="margin-top: 1rem; text-align: center;">
                    <a href="${pageContext.request.contextPath}/FrontController?accion=inicio" class="btn btn-secondary btn-sm">
                        ↩ Volver a la tienda
                    </a>
                </div>
            </form>
        </div>
    </div>

    <jsp:include page="../INC/footer.jsp" />

</body>
</html>