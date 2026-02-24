<%-- 
    Document   : registro
    Created on : 20 feb. 2026, 0:15:32
    Author     : Alba
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro - Chollotek</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilo.css">
    <script src="${pageContext.request.contextPath}/JS/validaciones.js" defer></script>
</head>
<body>
    
    <jsp:include page="../INC/header.jsp" />

    <div class="container-form">
        <div class="form-card form-card-wide">
            <h2>Crear cuenta</h2>

            <!-- MENSAJES -->
            <c:if test="${not empty mensajeError}">
                <div class="alert alert-error">${mensajeError}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/RegistroController" 
                  method="post" 
                  enctype="multipart/form-data"
                  id="formRegistro"
                  class="form-registro">
                
                <h3>Datos de acceso</h3>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="email">Email: *</label>
                        <input type="email" 
                               id="email" 
                               name="email" 
                               required>
                        <span id="emailError" class="error-message"></span>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="password">Contraseña: *</label>
                        <input type="password" 
                               id="password" 
                               name="password" 
                               required
                               minlength="6">
                    </div>

                    <div class="form-group">
                        <label for="password2">Repetir contraseña: *</label>
                        <input type="password" 
                               id="password2" 
                               name="password2" 
                               required>
                        <span id="passwordError" class="error-message"></span>
                    </div>
                </div>

                <h3>Datos personales</h3>

                <div class="form-row">
                    <div class="form-group">
                        <label for="nombre">Nombre: *</label>
                        <input type="text" 
                               id="nombre" 
                               name="nombre" 
                               required
                               maxlength="20">
                    </div>

                    <div class="form-group">
                        <label for="apellidos">Apellidos: *</label>
                        <input type="text" 
                               id="apellidos" 
                               name="apellidos" 
                               required
                               maxlength="30">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="nif">NIF (solo números): *</label>
                        <input type="text" 
                               id="nif" 
                               name="nif" 
                               required
                               maxlength="8"
                               pattern="[0-9]{8}"
                               placeholder="12345678">
                        <span id="nifLetra"></span>
                    </div>

                    <div class="form-group">
                        <label for="telefono">Teléfono:</label>
                        <input type="tel" 
                               id="telefono" 
                               name="telefono"
                               maxlength="9"
                               pattern="[0-9]{9}"
                               placeholder="612345678">
                    </div>
                </div>

                <h3>Dirección</h3>

                <div class="form-row">
                    <div class="form-group form-group-full">
                        <label for="direccion">Dirección: *</label>
                        <input type="text" 
                               id="direccion" 
                               name="direccion" 
                               required
                               maxlength="40">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="codigo_postal">Código postal: *</label>
                        <input type="text" 
                               id="codigo_postal" 
                               name="codigo_postal" 
                               required
                               pattern="[0-9]{5}"
                               placeholder="10600">
                    </div>

                    <div class="form-group">
                        <label for="localidad">Localidad: *</label>
                        <input type="text" 
                               id="localidad" 
                               name="localidad" 
                               required
                               maxlength="40">
                    </div>

                    <div class="form-group">
                        <label for="provincia">Provincia: *</label>
                        <input type="text" 
                               id="provincia" 
                               name="provincia" 
                               required
                               maxlength="30">
                    </div>
                </div>

                <h3>Avatar (opcional)</h3>

                <div class="form-group">
                    <label for="avatar">Subir foto de perfil:</label>
                    <input type="file" 
                           id="avatar" 
                           name="avatar"
                           accept="image/png, image/jpeg, image/jpg">
                    <small>Formatos: JPG, PNG. Tamaño máximo: 5MB</small>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary btn-block">
                        Crear cuenta
                    </button>
                </div>

                <p class="text-center">
                    ¿Ya tienes cuenta? 
                    <a href="${pageContext.request.contextPath}/FrontController?accion=verLogin">
                        Inicia sesión aquí
                    </a>
                </p>
            </form>
        </div>
    </div>

     <%--<jsp:include page="includes/footer.jsp" />--%>

</body>
</html>