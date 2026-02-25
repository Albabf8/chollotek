<%-- 
    Document   : perfil
    Created on : 20 feb. 2026, 0:36:04
    Author     : Alba
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Verificar que el usuario está logueado -->
<c:if test="${empty sessionScope.usuario}">
    <c:redirect url="${pageContext.request.contextPath}/FrontController?accion=verLogin"/>
</c:if>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi perfil - Chollotek</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilo.css">
</head>
<body>
    
    <jsp:include page="../INC/header.jsp" />

    <div class="container">
        <h2>👤 Mi perfil</h2>

        <!-- MENSAJES -->
        <c:if test="${not empty mensajeError}">
            <div class="alert alert-error">${mensajeError}</div>
        </c:if>
        <c:if test="${not empty mensajeExito}">
            <div class="alert alert-success">${mensajeExito}</div>
        </c:if>

        <div class="perfil-container">
            <!-- FORMULARIO EDITAR PERFIL -->
            <div class="perfil-card">
                <h3>Datos personales</h3>
                
                <form action="${pageContext.request.contextPath}/PerfilController" 
                      method="post"
                      enctype="multipart/form-data">
                    
                    <input type="hidden" name="accion" value="actualizarPerfil">
                    
                    <div class="form-group">
                        <label>Email:</label>
                        <input type="email" value="${sessionScope.usuario.email}" disabled>
                        <small>El email no se puede modificar</small>
                    </div>

                    <div class="form-group">
                        <label>NIF:</label>
                        <input type="text" value="${sessionScope.usuario.nif}" disabled>
                        <small>El NIF no se puede modificar</small>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="nombre">Nombre: *</label>
                            <input type="text" 
                                   id="nombre" 
                                   name="nombre" 
                                   value="${sessionScope.usuario.nombre}"
                                   required>
                        </div>

                        <div class="form-group">
                            <label for="apellidos">Apellidos: *</label>
                            <input type="text" 
                                   id="apellidos" 
                                   name="apellidos" 
                                   value="${sessionScope.usuario.apellidos}"
                                   required>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="telefono">Teléfono:</label>
                        <input type="tel" 
                               id="telefono" 
                               name="telefono" 
                               value="${sessionScope.usuario.telefono}">
                    </div>

                    <div class="form-group">
                        <label for="direccion">Dirección: *</label>
                        <input type="text" 
                               id="direccion" 
                               name="direccion" 
                               value="${sessionScope.usuario.direccion}"
                               required>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="codigo_postal">Código postal: *</label>
                            <input type="text" 
                                   id="codigo_postal" 
                                   name="codigoPostal" 
                                   value="${sessionScope.usuario.codigoPostal}"
                                   required>
                        </div>

                        <div class="form-group">
                            <label for="localidad">Localidad: *</label>
                            <input type="text" 
                                   id="localidad" 
                                   name="localidad" 
                                   value="${sessionScope.usuario.localidad}"
                                   required>
                        </div>

                        <div class="form-group">
                            <label for="provincia">Provincia: *</label>
                            <input type="text" 
                                   id="provincia" 
                                   name="provincia" 
                                   value="${sessionScope.usuario.provincia}"
                                   required>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary">
                        Guardar cambios
                    </button>
                </form>
            </div>

            <!-- FORMULARIO CAMBIAR CONTRASEÑA -->
            <div class="perfil-card">
                <h3>Cambiar contraseña</h3>
                
                <form action="${pageContext.request.contextPath}/PerfilController" 
                      method="post">
                    
                    <input type="hidden" name="accion" value="cambiarPassword">
                    
                    <div class="form-group">
                        <label for="passwordActual">Contraseña actual: *</label>
                        <input type="password" 
                               id="passwordActual" 
                               name="passwordActual" 
                               required>
                    </div>

                    <div class="form-group">
                        <label for="passwordNueva">Nueva contraseña: *</label>
                        <input type="password" 
                               id="passwordNueva" 
                               name="passwordNueva" 
                               required
                               minlength="6">
                    </div>

                    <div class="form-group">
                        <label for="passwordNueva2">Repetir nueva contraseña: *</label>
                        <input type="password" 
                               id="passwordNueva2" 
                               name="passwordNueva2" 
                               required>
                    </div>

                    <button type="submit" class="btn btn-secondary">
                        Cambiar contraseña
                    </button>
                </form>
            </div>
        </div>
    </div>

    <jsp:include page="../INC/footer.jsp" />

</body>
</html>
