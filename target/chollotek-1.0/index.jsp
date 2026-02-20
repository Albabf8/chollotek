<%-- 
    Document   : index
    Created on : 6 feb. 2026, 17:47:23
    Author     : Alba
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Chollotek — Tu PC Despega</title>
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

  <!-- HERO -->
  <div style="position:relative; background: radial-gradient(ellipse 80% 100% at 80% 50%, rgba(124,58,237,.2) 0%, transparent 70%);">
    <div class="hero-bg"></div>
    <div class="hero">
      <div class="hero-content">
        <h1>¡Tu PC Despega<br/>con <span>Chollotek!</span></h1>
        <p>te ayudamos a elegir los mejores componentes para tu PC con comparativas reales, asesoramiento y precios que marcan la diferencia.</p>
        <a href="#ofertas" class="btn-primary">Ver ofertas 🔥 Aquí</a>
      </div>
    </div>
    <div class="hero-illustration">🖥️</div>
  </div>

  <div class="glow-line"></div>

  <!-- OFERTAS -->
  <section class="section" id="ofertas">
    <h2 class="section-title">OFERTAS</h2>
    <div class="product-grid">

      <%--
      ══════════════════════════════════════════════════════
      VERSIÓN DINÁMICA con JSTL (activar cuando esté el DAO)
      El Servlet pone en request: List<ProductoDTO> productos
      Cada ProductoDTO tiene: id, icono, nombre, precio, marca, descripcion
      ══════════════════════════════════════════════════════

      <c:forEach var="p" items="${productos}">
        <div class="product-card">
          <div class="product-img"><span>${p.icono}</span></div>
          <div class="product-name">${p.nombre}</div>
          <div class="product-price">
            <fmt:formatNumber value="${p.precio}" type="currency"
                              currencySymbol="€" maxFractionDigits="2" />
          </div>
          <div class="card-actions">
            <button class="btn-open"
                    onclick="abrirModal('${p.icono}','${p.nombre}','${p.marca}','${p.descripcion}',${p.precio},${p.id})">
              Abrir
            </button>
            <button class="btn-buy"
                    onclick="location.href='${pageContext.request.contextPath}/CarritoServlet?accion=agregar&idProducto=${p.id}'">
              Comprar
            </button>
          </div>
        </div>
      </c:forEach>
      --%>

      <%-- DATOS ESTÁTICOS de ejemplo (reemplazar por el c:forEach anterior) --%>

      <div class="product-card">
        <div class="product-img"><span>🎧</span></div>
        <div class="product-name">Auriculares Gaming Pro X</div>
        <div class="product-price">29,99 €</div>
        <div class="card-actions">
          <button class="btn-open" onclick="abrirModal('🎧','Auriculares Gaming Pro X','HyperSound','Auriculares gaming con sonido envolvente 7.1, micrófono retráctil con cancelación de ruido y diadema acolchada para largas sesiones.',29.99,1)">Abrir</button>
          <button class="btn-buy">Comprar</button>
        </div>
      </div>

      <div class="product-card">
        <div class="product-img"><span>💻</span></div>
        <div class="product-name">Portátil UltraBook 15</div>
        <div class="product-price">549,00 €</div>
        <div class="card-actions">
          <button class="btn-open" onclick="abrirModal('💻','Portátil UltraBook 15','TechBook','Portátil ultradelgado con pantalla IPS Full HD de 15.6 pulgadas, procesador Intel Core i5 de 12a gen, 16 GB RAM y SSD 512 GB.',549.00,2)">Abrir</button>
          <button class="btn-buy">Comprar</button>
        </div>
      </div>

      <div class="product-card">
        <div class="product-img"><span>🖥️</span></div>
        <div class="product-name">PC Gaming Ryzen 7 RTX 4070</div>
        <div class="product-price">899,00 €</div>
        <div class="card-actions">
          <button class="btn-open" onclick="abrirModal('🖥️','PC Gaming Ryzen 7 RTX 4070','CholloPC','Torre gaming con AMD Ryzen 7 5800X, NVIDIA RTX 4070 12 GB, 32 GB DDR4 y SSD NVMe 1 TB. Lista para jugar al máximo.',899.00,3)">Abrir</button>
          <button class="btn-buy">Comprar</button>
        </div>
      </div>

      <div class="product-card">
        <div class="product-img"><span>🎮</span></div>
        <div class="product-name">Mando Pro Wireless</div>
        <div class="product-price">349,99 €</div>
        <div class="card-actions">
          <button class="btn-open" onclick="abrirModal('🎮','Mando Pro Wireless','GameForce','Mando inalámbrico con vibración háptica, gatillos adaptativos, batería de 40 h y compatibilidad con PC, PS y Xbox.',349.99,4)">Abrir</button>
          <button class="btn-buy">Comprar</button>
        </div>
      </div>

      <div class="product-card">
        <div class="product-img"><span>⚡</span></div>
        <div class="product-name">Fuente 850W 80+ Gold</div>
        <div class="product-price">199,99 €</div>
        <div class="card-actions">
          <button class="btn-open" onclick="abrirModal('⚡','Fuente 850W 80+ Gold','VoltCore','Fuente modular 850 W con certificación 80+ Gold, protección contra sobretensión y ventilador silencioso de 135 mm.',199.99,5)">Abrir</button>
          <button class="btn-buy">Comprar</button>
        </div>
      </div>

      <div class="product-card">
        <div class="product-img"><span>⌨️</span></div>
        <div class="product-name">Teclado Mecánico RGB TKL</div>
        <div class="product-price">79,99 €</div>
        <div class="card-actions">
          <button class="btn-open" onclick="abrirModal('⌨️','Teclado Mecánico RGB TKL','KeyForce','Teclado tenkeyless con switches Cherry MX Red, iluminación RGB por tecla, marco de aluminio y cable USB-C desmontable.',79.99,6)">Abrir</button>
          <button class="btn-buy">Comprar</button>
        </div>
      </div>

      <div class="product-card">
        <div class="product-img"><span>🖱️</span></div>
        <div class="product-name">Ratón Gaming 16000 DPI</div>
        <div class="product-price">49,99 €</div>
        <div class="card-actions">
          <button class="btn-open" onclick="abrirModal('🖱️','Ratón Gaming 16000 DPI','SwiftClick','Ratón gaming con sensor óptico de 16000 DPI, 7 botones programables, RGB de 16 M de colores y diseño ergonómico ambidiestro.',49.99,7)">Abrir</button>
          <button class="btn-buy">Comprar</button>
        </div>
      </div>

      <div class="product-card">
        <div class="product-img"><span>🎙️</span></div>
        <div class="product-name">Micro Condensador USB</div>
        <div class="product-price">89,99 €</div>
        <div class="card-actions">
          <button class="btn-open" onclick="abrirModal('🎙️','Micro Condensador USB','ClearVoice','Micrófono de condensador con patrón cardioide, conexión USB plug and play, brazo articulado incluido y filtro antipop.',89.99,8)">Abrir</button>
          <button class="btn-buy">Comprar</button>
        </div>
      </div>

    </div>
  </section>

  <!-- ══════════════════════════════════════════
       MODAL DE PRODUCTO (único, reutilizable)
  ══════════════════════════════════════════════ -->
  <div class="modal-overlay" id="modalOverlay" onclick="cerrarModalOverlay(event)">
    <div class="modal" id="modalProducto">

      <button class="modal-close" onclick="cerrarModal()" title="Cerrar">✕</button>

      <div class="modal-img">
        <span id="modalIcono">🎧</span>
      </div>

      <div class="modal-body">
        <div class="modal-marca"  id="modalMarca">Marca</div>
        <div class="modal-nombre" id="modalNombre">Nombre del producto</div>
        <div class="modal-desc"   id="modalDesc">Descripción del producto.</div>
        <div class="modal-precio" id="modalPrecio">0,00 €</div>

        <div class="modal-actions">
          <button class="modal-btn-buy" onclick="comprarDesdeModal()">
            🛒 Añadir al carrito
          </button>
          <button class="modal-btn-close" onclick="cerrarModal()">Cerrar</button>
        </div>
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
    let _idProductoActual = null;

    function abrirModal(icono, nombre, marca, descripcion, precio, idProducto) {
      _idProductoActual = idProducto;
      document.getElementById('modalIcono').textContent  = icono;
      document.getElementById('modalNombre').textContent = nombre;
      document.getElementById('modalMarca').textContent  = marca;
      document.getElementById('modalDesc').textContent   = descripcion;
      document.getElementById('modalPrecio').textContent =
        precio.toFixed(2).replace('.', ',') + ' €';
      document.getElementById('modalOverlay').classList.add('active');
      document.body.style.overflow = 'hidden';
    }

    function cerrarModal() {
      document.getElementById('modalOverlay').classList.remove('active');
      document.body.style.overflow = '';
      _idProductoActual = null;
    }

    function cerrarModalOverlay(e) {
      if (e.target === document.getElementById('modalOverlay')) cerrarModal();
    }

    function comprarDesdeModal() {
      if (_idProductoActual === null) return;
      window.location = '${pageContext.request.contextPath}/CarritoServlet?accion=agregar&idProducto=' + _idProductoActual;
    }

    document.addEventListener('keydown', e => {
      if (e.key === 'Escape') cerrarModal();
    });
  </script>

</body>
</html>
