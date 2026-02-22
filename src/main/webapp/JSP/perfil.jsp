<%-- 
    Document   : perfil
    Created on : 20 feb. 2026, 0:36:04
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
      <button class="nav-btn"><span>🛒</span> Mi carrito</button>
      <a href="${pageContext.request.contextPath}/LogoutServlet">
        <button class="nav-btn"><span>🚪</span> Cerrar sesión</button>
      </a>
    </div>
  </nav>

  <!-- PERFIL -->
  <div class="profile-wrapper">

    <div class="page-header">
      <div class="page-title">Mi perfil</div>
      <a href="${pageContext.request.contextPath}/index.jsp" class="btn-escape">← Volver a la tienda</a>
    </div>

    <!-- Cabecera del perfil — datos del DTO en sesión -->
    <div class="profile-header">
      <div class="profile-avatar">
        <c:choose>
          <c:when test="${not empty usuarioDTO.avatar}">
            <img src="${pageContext.request.contextPath}/uploads/avatars/${usuarioDTO.avatar}"
                 style="width:100%;height:100%;object-fit:cover;" alt="Avatar" />
          </c:when>
          <c:otherwise>👤</c:otherwise>
        </c:choose>
        <div class="avatar-overlay">Cambiar<br/>foto</div>
      </div>
      <div class="profile-info">
        <h2>${usuarioDTO.nombre} ${usuarioDTO.apellidos}</h2>
        <p>${usuarioDTO.email}</p>
        <p>Último acceso: <fmt:formatDate value="${usuarioDTO.ultimoAcceso}" pattern="dd/MM/yyyy HH:mm" /></p>
        <span class="profile-badge">Usuario registrado</span>
      </div>
    </div>

    <%-- Mensajes de éxito o error desde el Servlet --%>
    <c:if test="${not empty exito}">
      <div style="background:rgba(0,230,118,.1);border:1px solid rgba(0,230,118,.3);border-radius:8px;
                  padding:.6rem .9rem;font-size:.8rem;color:#00e676;margin-bottom:1rem;">
        ✔ ${exito}
      </div>
    </c:if>
    <c:if test="${not empty error}">
      <div style="background:rgba(255,60,60,.1);border:1px solid rgba(255,60,60,.3);border-radius:8px;
                  padding:.6rem .9rem;font-size:.8rem;color:#ff6b6b;margin-bottom:1rem;">
        ❌ ${error}
      </div>
    </c:if>

    <!-- Tabs -->
    <div class="tabs">
      <div class="tab active"  onclick="switchTab('datos')">Mis datos</div>
      <div class="tab"         onclick="switchTab('password')">Contraseña</div>
      <div class="tab"         onclick="switchTab('direccion')">Dirección</div>
    </div>

    <!-- TAB: Datos personales -->
    <div class="tab-content active" id="tab-datos">
      <div class="edit-card">
        <form action="${pageContext.request.contextPath}/PerfilServlet" method="post"
              enctype="multipart/form-data">
          <input type="hidden" name="accion" value="datos" />

          <div class="form-section-title">Datos personales</div>
          <div class="form-row">

            <div class="form-group">
              <label>Nombre</label>
              <input type="text" name="nombre" value="${usuarioDTO.nombre}" />
            </div>

            <div class="form-group">
              <label>Apellidos</label>
              <input type="text" name="apellidos" value="${usuarioDTO.apellidos}" />
            </div>

            <div class="form-group">
              <label>Email <span class="readonly-note">(no editable)</span></label>
              <input type="email" value="${usuarioDTO.email}" readonly />
            </div>

            <div class="form-group">
              <label>NIF <span class="readonly-note">(no editable)</span></label>
              <input type="text" value="${usuarioDTO.nif}" readonly />
            </div>

            <div class="form-group">
              <label>Teléfono</label>
              <input type="tel" name="telefono" value="${usuarioDTO.telefono}" />
            </div>

          </div>

          <div class="form-section-title">Avatar</div>
          <div class="form-group">
            <input type="file" name="avatar" accept="image/*" />
          </div>

          <button type="submit" class="btn-save">Guardar cambios</button>
        </form>
      </div>
    </div>

    <!-- TAB: Contraseña -->
    <div class="tab-content" id="tab-password">
      <div class="edit-card">
        <form action="${pageContext.request.contextPath}/CambiarPasswordServlet" method="post">

          <div class="pass-section">
            <div class="form-section-title" style="margin-top:0">Cambiar contraseña</div>

            <div class="form-group">
              <label>Contraseña actual</label>
              <input type="password" name="password_actual" placeholder="••••••••" />
            </div>

            <div class="form-group">
              <label>Nueva contraseña</label>
              <input type="password" name="password_nueva" id="pnueva" placeholder="••••••••" />
            </div>

            <div class="form-group">
              <label>Repetir nueva contraseña</label>
              <input type="password" name="password_nueva2" id="pnueva2" placeholder="••••••••" />
              <span class="field-hint" id="newPassHint"></span>
            </div>
          </div>

          <button type="submit" class="btn-save">Cambiar contraseña</button>
        </form>
      </div>
    </div>

    <!-- TAB: Dirección -->
    <div class="tab-content" id="tab-direccion">
      <div class="edit-card">
        <form action="${pageContext.request.contextPath}/PerfilServlet" method="post">
          <input type="hidden" name="accion" value="direccion" />

          <div class="form-section-title" style="margin-top:0">Dirección de envío</div>
          <div class="form-row">

            <div class="form-group full">
              <label>Dirección</label>
              <input type="text" name="direccion" value="${usuarioDTO.direccion}" />
            </div>

            <div class="form-group">
              <label>Código postal</label>
              <input type="text" name="codigoPostal" value="${usuarioDTO.codigoPostal}" maxlength="5" />
            </div>

            <div class="form-group">
              <label>Localidad</label>
              <input type="text" name="localidad" value="${usuarioDTO.localidad}" />
            </div>

            <div class="form-group full">
              <label>Provincia</label>
              <input type="text" name="provincia" value="${usuarioDTO.provincia}" />
            </div>

          </div>
          <button type="submit" class="btn-save">Guardar dirección</button>
        </form>
      </div>
    </div>

  </div><!-- /profile-wrapper -->

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
    function switchTab(name) {
      const ids = ['datos', 'password', 'direccion'];
      document.querySelectorAll('.tab').forEach((t, i) => {
        t.classList.toggle('active', ids[i] === name);
      });
      document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
      document.getElementById('tab-' + name).classList.add('active');
    }

    document.getElementById('pnueva2').addEventListener('input', () => {
      const p1   = document.getElementById('pnueva').value;
      const p2   = document.getElementById('pnueva2').value;
      const hint = document.getElementById('newPassHint');
      if (!p2) { hint.textContent = ''; return; }
      if (p1 === p2) { hint.textContent = '✔ Las contraseñas coinciden'; hint.className = 'field-hint ok'; }
      else           { hint.textContent = '✖ No coinciden';              hint.className = 'field-hint err'; }
    });
  </script>

</body>
</html>

