<%-- 
    Document   : registro
    Created on : 20 feb. 2026, 0:15:32
    Author     : Alba
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Chollotek — Registro</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilo.css" />
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

  <!-- REGISTRO -->
  <div class="register-wrapper">
    <div class="register-card">

      <a href="${pageContext.request.contextPath}/index.jsp" class="btn-escape">← Volver a la tienda</a>

      <div class="auth-title">Crear cuenta</div>
      <div class="auth-subtitle">Regístrate para poder finalizar tus compras</div>

      <%-- Mensaje de error desde el Servlet (si lo hay) --%>
      <c:if test="${not empty error}">
        <div class="field-hint err" style="margin-bottom:1rem; text-align:center;">${error}</div>
      </c:if>

      <form action="${pageContext.request.contextPath}/RegistroServlet" method="post" enctype="multipart/form-data">

        <!-- DATOS PERSONALES -->
        <div class="form-section-title">Datos personales</div>
        <div class="form-row">

          <div class="form-group">
            <label>Nombre <span class="required">*</span></label>
            <%-- BeanUtils recupera el valor del DTO para repoblar el campo tras error --%>
            <input type="text" name="nombre" placeholder="Ana"
                   value="${not empty usuarioDTO.nombre ? usuarioDTO.nombre : ''}" required />
          </div>

          <div class="form-group">
            <label>Apellidos <span class="required">*</span></label>
            <input type="text" name="apellidos" placeholder="García López"
                   value="${not empty usuarioDTO.apellidos ? usuarioDTO.apellidos : ''}" required />
          </div>

          <div class="form-group">
            <label>NIF — números <span class="required">*</span></label>
            <div class="input-with-action">
              <input type="text" name="nif_numeros" placeholder="12345678"
                     maxlength="8" id="nifNum" />
              <button type="button" class="btn-ajax" onclick="calcularLetraNIF()">Calcular letra</button>
            </div>
          </div>

          <div class="form-group">
            <label>NIF completo</label>
            <input type="text" name="nif" id="nifCompleto" placeholder="12345678Z"
                   value="${not empty usuarioDTO.nif ? usuarioDTO.nif : ''}" readonly />
            <span class="field-hint" id="nifHint">La letra se asigna automáticamente</span>
          </div>

          <div class="form-group">
            <label>Teléfono</label>
            <input type="tel" name="telefono" placeholder="600 000 000" maxlength="9"
                   value="${not empty usuarioDTO.telefono ? usuarioDTO.telefono : ''}" />
          </div>

        </div>

        <!-- CUENTA -->
        <div class="form-section-title">Cuenta</div>
        <div class="form-row">

          <div class="form-group full">
            <label>Correo electrónico <span class="required">*</span></label>
            <input type="email" name="email" placeholder="tu@email.com" id="emailInput"
                   value="${not empty usuarioDTO.email ? usuarioDTO.email : ''}" required />
            <span class="field-hint" id="emailHint">Se comprobará disponibilidad</span>
          </div>

          <div class="form-group">
            <label>Contraseña <span class="required">*</span></label>
            <input type="password" name="password" id="pass1" placeholder="••••••••" required />
          </div>

          <div class="form-group">
            <label>Repetir contraseña <span class="required">*</span></label>
            <input type="password" name="password2" id="pass2" placeholder="••••••••" required />
            <span class="field-hint" id="passHint"></span>
          </div>

        </div>

        <!-- DIRECCIÓN -->
        <div class="form-section-title">Dirección de envío</div>
        <div class="form-row">

          <div class="form-group full">
            <label>Dirección <span class="required">*</span></label>
            <input type="text" name="direccion" placeholder="Calle Mayor, 10, 2ºA"
                   value="${not empty usuarioDTO.direccion ? usuarioDTO.direccion : ''}" required />
          </div>

          <div class="form-group">
            <label>Código postal <span class="required">*</span></label>
            <input type="text" name="codigo_postal" placeholder="28001" maxlength="5" id="cp"
                   value="${not empty usuarioDTO.codigoPostal ? usuarioDTO.codigoPostal : ''}" required />
            <span class="field-hint" id="cpHint"></span>
          </div>

          <div class="form-group">
            <label>Localidad <span class="required">*</span></label>
            <input type="text" name="localidad" placeholder="Madrid"
                   value="${not empty usuarioDTO.localidad ? usuarioDTO.localidad : ''}" required />
          </div>

          <div class="form-group full">
            <label>Provincia <span class="required">*</span></label>
            <input type="text" name="provincia" placeholder="Madrid"
                   value="${not empty usuarioDTO.provincia ? usuarioDTO.provincia : ''}" required />
          </div>

        </div>

        <!-- AVATAR -->
        <div class="form-section-title">Avatar (opcional)</div>
        <div class="form-group">
          <div class="avatar-upload">
            <div class="avatar-preview" id="avatarPreview">👤</div>
            <input type="file" name="avatar" accept="image/*" onchange="previewAvatar(event)" />
          </div>
        </div>

        <button type="submit" class="btn-full">Crear cuenta →</button>
      </form>

      <div class="auth-link">
        ¿Ya tienes cuenta? <a href="${pageContext.request.contextPath}/login.jsp">Inicia sesión</a>
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

  <script>
    // Validación contraseñas en cliente
    document.getElementById('pass2').addEventListener('input', () => {
      const p1 = document.getElementById('pass1').value;
      const p2 = document.getElementById('pass2').value;
      const hint = document.getElementById('passHint');
      if (p2.length === 0) { hint.textContent = ''; return; }
      if (p1 === p2) { hint.textContent = '✔ Las contraseñas coinciden'; hint.className = 'field-hint ok'; }
      else           { hint.textContent = '✖ Las contraseñas no coinciden'; hint.className = 'field-hint err'; }
    });

    // Validación código postal
    document.getElementById('cp').addEventListener('input', () => {
      const v = document.getElementById('cp').value;
      const hint = document.getElementById('cpHint');
      if (/^\d{5}$/.test(v))  { hint.textContent = '✔ Formato correcto'; hint.className = 'field-hint ok'; }
      else if (v.length > 0)  { hint.textContent = '✖ Debe tener 5 dígitos'; hint.className = 'field-hint err'; }
      else hint.textContent = '';
    });

    // Cálculo letra NIF en cliente (sin llamada al servidor)
    function calcularLetraNIF() {
      const num = document.getElementById('nifNum').value;
      const hint = document.getElementById('nifHint');
      if (!/^\d{8}$/.test(num)) {
        hint.textContent = '✖ Introduce 8 dígitos';
        hint.className = 'field-hint err';
        return;
      }
      const letras = 'TRWAGMYFPDXBNJZSQVHLCKE';
      const letra = letras[parseInt(num) % 23];
      document.getElementById('nifCompleto').value = num + letra;
      hint.textContent = '✔ Letra asignada: ' + letra;
      hint.className = 'field-hint ok';
    }

    // Preview avatar antes de subir
    function previewAvatar(e) {
      const file = e.target.files[0];
      if (!file) return;
      const reader = new FileReader();
      reader.onload = ev => {
        const prev = document.getElementById('avatarPreview');
        prev.innerHTML = '<img src="' + ev.target.result + '" style="width:100%;height:100%;object-fit:cover;border-radius:50%;" />';
      };
      reader.readAsDataURL(file);
    }
  </script>

</body>
</html>

