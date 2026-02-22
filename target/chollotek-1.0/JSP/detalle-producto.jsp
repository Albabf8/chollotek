<%-- 
    Document   : detalle-producto
    Created on : 22 feb. 2026, 20:18:31
    Author     : Alba
--%>

<jsp:directive.page contentType="text/html" pageEncoding="UTF-8"/>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:url var="estilo" value="/CSS/estilo.css" scope="application" />
<c:set var="contexto" value="${pageContext.request.contextPath}" scope="application"/>
<!DOCTYPE html>
<html>
    <head>
        <jsp:include page="/INC/cabecera.jsp">
            <jsp:param name="titulo" value="Chollotek" />
            <jsp:param name="estilo" value="${estilo}" />
        </jsp:include>
    </head>
    <body>
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

  <div class="detail-wrapper">

    <a href="index.html" class="btn-escape">← Volver a la tienda</a>

    <div class="product-detail">

      <!-- IMAGEN -->
      <div class="product-image-box">
        <span>🎧</span>
      </div>

      <!-- INFO -->
      <div class="product-info">
        <div class="product-category">Periféricos / Audio</div>
        <div class="product-detail-name">Auriculares Gaming Pro X</div>
        <div class="product-brand">Marca: <strong style="color:var(--text)">HyperSound</strong></div>
        <div class="product-detail-price">89,99 €</div>

        <div class="product-description">
          Auriculares gaming de alta fidelidad con sonido envolvente 7.1 virtual. Micrófono
          con cancelación de ruido, diadema ajustable con almohadillas de espuma viscoelástica
          y compatibilidad con PC, PS5 y Xbox Series X. Cable desmontable de 1,5 m.
        </div>

        <!-- Cantidad + añadir al carrito -->
        <div class="qty-row">
          <span class="qty-label">Cantidad</span>
          <div class="qty-control">
            <button class="qty-btn" onclick="changeQty(-1)">−</button>
            <input class="qty-value" type="number" id="qty" value="1" min="1" max="99" />
            <button class="qty-btn" onclick="changeQty(1)">+</button>
          </div>
        </div>

        <div class="qty-row">
          <button class="btn-add-cart" onclick="addToCart()">🛒 Añadir al carrito</button>
        </div>

        <!-- Nota para anónimos -->
        <div class="login-note">
          ℹ️ Para finalizar la compra deberás <a href="login.html">iniciar sesión</a> o <a href="registro.html">registrarte</a>. El carrito se guarda durante 2 días.
        </div>
      </div>
    </div>

    <!-- PRODUCTOS RELACIONADOS -->
    <div class="related-section">
      <div class="glow-line" style="margin:0 0 1.5rem;"></div>
      <h2 class="section-title">Productos relacionados</h2>
      <div class="product-grid">

        <div class="product-card">
          <div class="product-img"><span>🖱️</span></div>
          <div class="product-name">Ratón Gaming RGB</div>
          <div class="product-price">49,99 €</div>
          <div class="card-actions">
            <button class="btn-open">Ver</button>
            <button class="btn-buy">Comprar</button>
          </div>
        </div>

        <div class="product-card">
          <div class="product-img"><span>⌨️</span></div>
          <div class="product-name">Teclado Mecánico</div>
          <div class="product-price">79,99 €</div>
          <div class="card-actions">
            <button class="btn-open">Ver</button>
            <button class="btn-buy">Comprar</button>
          </div>
        </div>

        <div class="product-card">
          <div class="product-img"><span>🎙️</span></div>
          <div class="product-name">Micrófono USB</div>
          <div class="product-price">59,99 €</div>
          <div class="card-actions">
            <button class="btn-open">Ver</button>
            <button class="btn-buy">Comprar</button>
          </div>
        </div>

        <div class="product-card">
          <div class="product-img"><span>🖥️</span></div>
          <div class="product-name">Monitor 27" 4K</div>
          <div class="product-price">349,00 €</div>
          <div class="card-actions">
            <button class="btn-open">Ver</button>
            <button class="btn-buy">Comprar</button>
          </div>
        </div>

      </div>
    </div>

  </div>

  <footer>
    <div class="footer-left">
      <strong>Autor: Alba Barroso</strong><br />
      Desarrollo de aplicaciones web<br />
      2025/2026
    </div>
    <div class="footer-right">
      <span>© 2026 Chollotek S.L</span><br />
      <a href="#">Aviso legal</a>
      <a href="#">Política de cookies</a>
    </div>
  </footer>

  <script>
    function changeQty(delta) {
      const inp = document.getElementById('qty');
      let v = parseInt(inp.value) + delta;
      if (v < 1) v = 1;
      if (v > 99) v = 99;
      inp.value = v;
    }

    function addToCart() {
      const qty = document.getElementById('qty').value;
      alert(`✅ Añadido al carrito: ${qty} × Auriculares Gaming Pro X`);
    }
  </script>
    </body>
</html>
