<%-- 
    Document   : inicioSesion
    Created on : 20 feb. 2026, 0:32:48
    Author     : Alba
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Chollotek — Iniciar sesión</title>
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

  <!-- LOGIN -->
  <div class="auth-wrapper">
    <div class="auth-card">

      <div class="auth-title">Iniciar sesión</div>
      <div class="auth-subtitle">Accede a tu cuenta para gestionar tus pedidos</div>

      <%-- El Servlet hace forward con el atributo "error" si las credenciales fallan --%>
      <c:if test="${not empty error}">
        <div class="error-msg">❌ ${error}</div>
      </c:if>

      <form action="${pageContext.request.contextPath}/LoginServlet" method="post">

        <div class="form-group">
          <label>Correo electrónico</label>
          <%-- BeanUtils repobla el email si hay error para no perder el valor --%>
          <input type="email" name="email" placeholder="tu@email.com"
                 value="${not empty loginDTO.email ? loginDTO.email : ''}" required />
        </div>

        <div class="form-group">
          <label>Contraseña</label>
          <input type="password" name="password" placeholder="••••••••" required />
        </div>

        <button type="submit" class="btn-full">Entrar →</button>
      </form>

      <div class="auth-divider">o</div>

      <a href="${pageContext.request.contextPath}/index.jsp">
        <button class="btn-ghost">← Continuar sin registrarse</button>
      </a>

      <div class="auth-divider"></div>

      <div class="auth-link">
        ¿No tienes cuenta? <a href="${pageContext.request.contextPath}/registro.jsp">Regístrate aquí</a>
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

</body>
</html>

