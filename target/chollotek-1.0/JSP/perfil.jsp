<%-- Document : perfil Created on : 20 feb. 2026, 0:36:04 Author : Alba --%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Verificar que el usuario está logueado -->
<c:if test="${empty sessionScope.usuario}">
    <c:redirect url="${pageContext.request.contextPath}/FrontController?accion=verLogin" />
</c:if>

<!DOCTYPE html>
<html lang="es">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Mi perfil - Chollotek</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilo.css">
        <style>
            .avatar-preview {
                width: 120px;
                height: 120px;
                border-radius: 50%;
                object-fit: cover;
                border: 3px solid #ddd;
                display: block;
                margin-bottom: 10px;
            }
            .avatar-wrapper {
                display: flex;
                flex-direction: column;
                align-items: flex-start;
                gap: 8px;
            }
        </style>
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

                    <form action="${pageContext.request.contextPath}/PerfilController" method="post"
                          enctype="multipart/form-data"  onsubmit="return validarFormularioPerfil()">

                        <input type="hidden" name="accion" value="actualizarPerfil">

                        <!-- AVATAR -->
                        <div class="form-group">
                            <label>Avatar:</label>
                            <div class="avatar-wrapper">
                                <img id="avatarPreview"
                                     src="${pageContext.request.contextPath}/avatares/${sessionScope.usuario.avatar}"
                                     alt="Avatar"
                                     class="avatar-preview"
                                     onerror="this.src='${pageContext.request.contextPath}/avatares/default-avatar.jpg'"/>
                                <input type="file" id="avatar" name="avatar" accept="image/*"
                                       onchange="previewAvatar(event)">
                                <small>Deja vacío para mantener el avatar actual</small>
                            </div>
                        </div>

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
                                <input type="text" id="nombre" name="nombre"
                                       value="${sessionScope.usuario.nombre}" required>
                            </div>

                            <div class="form-group">
                                <label for="apellidos">Apellidos: *</label>
                                <input type="text" id="apellidos" name="apellidos"
                                       value="${sessionScope.usuario.apellidos}" required>
                            </div>
                        </div>

                        <div class="form-group">
                            <label for="telefono">Teléfono:</label>
                            <input type="tel" id="telefono" name="telefono"
                                   value="${sessionScope.usuario.telefono}">
                            <span id="telefonoError" class="error-message"></span>
                        </div>

                        <div class="form-group">
                            <label for="direccion">Dirección: *</label>
                            <input type="text" id="direccion" name="direccion"
                                   value="${sessionScope.usuario.direccion}" required>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="codigo_postal">Código postal: *</label>
                                <input type="text" id="codigo_postal" name="codigoPostal"
                                       value="${sessionScope.usuario.codigoPostal}" required>
                                <span id="cpError" class="error-message"></span>
                            </div>

                            <div class="form-group">
                                <label for="localidad">Localidad: *</label>
                                <input type="text" id="localidad" name="localidad"
                                       value="${sessionScope.usuario.localidad}" required>
                            </div>

                            <div class="form-group">
                                <label for="provincia">Provincia: *</label>
                                <input type="text" id="provincia" name="provincia"
                                       value="${sessionScope.usuario.provincia}" required>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary">
                            Guardar cambios
                        </button>

                        <a href="${pageContext.request.contextPath}/FrontController?accion=inicio"
                           class="btn btn-secondary" style="margin-left: 10px;">
                            ↩ Volver a la tienda
                        </a>
                    </form>
                </div>

                <!-- FORMULARIO CAMBIAR CONTRASEÑA -->
                <div class="perfil-card">
                    <h3>Cambiar contraseña</h3>

                    <form action="${pageContext.request.contextPath}/PerfilController" method="post" onsubmit="return validarCambioPassword()">

                        <input type="hidden" name="accion" value="cambiarPassword">

                        <div class="form-group">
                            <label for="passwordActual">Contraseña actual: *</label>
                            <input type="password" id="passwordActual" name="passwordActual" required>
                        </div>

                        <div class="form-group">
                            <label for="passwordNueva">Nueva contraseña: *</label>
                            <input type="password" id="passwordNueva" name="passwordNueva" required
                                   minlength="6">
                            <span id="passwordNuevaError" class="error-message"></span>
                        </div>

                        <div class="form-group">
                            <label for="passwordNueva2">Repetir nueva contraseña: *</label>
                            <input type="password" id="passwordNueva2" name="passwordNueva2" required>
                            <span id="passwordNueva2Error" class="error-message"></span>
                        </div>

                        <button type="submit" class="btn btn-secondary">
                            Cambiar contraseña
                        </button>

                        <a href="${pageContext.request.contextPath}/FrontController?accion=inicio"
                           class="btn btn-secondary" style="margin-left: 10px;">
                            ↩ Volver a la tienda
                        </a>
                    </form>
                </div>
            </div>
        </div>

        <jsp:include page="../INC/footer.jsp" />

        <script>
            /**
             * Procesa y muestra una vista previa de la imagen seleccionada por el usuario.
             * @param {Event} event - El evento de cambio (change) disparado por el input de tipo file.
             */
            function previewAvatar(event) {
                const file = event.target.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        document.getElementById('avatarPreview').src = e.target.result;
                    };
                    reader.readAsDataURL(file);
                }
            }

            /**
             * Valida el formulario de datos personales del perfil antes de enviarlo.
             * Comprueba que el teléfono tenga 9 dígitos (si se ha rellenado)
             * y que el código postal tenga exactamente 5 dígitos.
             * 
             * @returns {boolean} true si todos los datos son válidos, false si hay algún error.
             */
            function validarFormularioPerfil() {
                let valido = true;

                var telefono = document.getElementById('telefono').value;
                var telefonoError = document.getElementById('telefonoError');
                if (telefono && !/^[0-9]{9}$/.test(telefono)) {
                    telefonoError.textContent = '✗ El teléfono debe tener 9 dígitos';
                    telefonoError.style.color = 'red';
                    valido = false;
                } else {
                    telefonoError.textContent = '';
                }

                var cp = document.getElementById('codigo_postal').value;
                var cpError = document.getElementById('cpError');
                if (!/^[0-9]{5}$/.test(cp)) {
                    cpError.textContent = '✗ El código postal debe tener 5 dígitos';
                    cpError.style.color = 'red';
                    valido = false;
                } else {
                    cpError.textContent = '';
                }

                return valido;
            }

            /**
             * Valida el formulario de cambio de contraseña antes de enviarlo.
             * Comprueba que la nueva contraseña tenga al menos 6 caracteres
             * y que ambas contraseñas nuevas introducidas coincidan.
             * 
             * @returns {boolean} true si la contraseña es válida y coincide, false si hay algún error.
             */
            function validarCambioPassword() {
                let valido = true;

                var nueva = document.getElementById('passwordNueva').value;
                var passwordNuevaError = document.getElementById('passwordNuevaError');
                if (nueva.length < 6) {
                    passwordNuevaError.textContent = '✗ La contraseña debe tener al menos 6 caracteres';
                    passwordNuevaError.style.color = 'red';
                    valido = false;
                } else {
                    passwordNuevaError.textContent = '';
                }

                var nueva2 = document.getElementById('passwordNueva2').value;
                var passwordNueva2Error = document.getElementById('passwordNueva2Error');
                if (nueva !== nueva2) {
                    passwordNueva2Error.textContent = '✗ Las contraseñas nuevas no coinciden';
                    passwordNueva2Error.style.color = 'red';
                    valido = false;
                } else {
                    passwordNueva2Error.textContent = '';
                }

                return valido;
            }
        </script>

    </body>

</html>