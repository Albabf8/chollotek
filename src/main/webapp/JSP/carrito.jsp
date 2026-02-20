<%-- 
    Document   : carrito
    Created on : 20 feb. 2026, 0:42:27
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
  <title>Chollotek — Carrito</title>
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
      <button class="nav-btn">
        <span>🛒</span> Carrito
        <%-- Muestra el número de líneas del carrito desde sesión --%>
        <c:if test="${not empty carrito}"> (${carrito.size()})</c:if>
      </button>
    </div>
  </nav>

  <div class="cart-wrapper">

    <div class="page-header">
      <div class="page-title">Mi carrito</div>
      <a href="${pageContext.request.contextPath}/index.jsp" class="btn-escape">← Seguir comprando</a>
    </div>

    <c:choose>

      <%-- ── CARRITO VACÍO ── --%>
      <c:when test="${empty carrito}">
        <div class="empty-cart">
          <div class="icon">🛒</div>
          <p>Tu carrito está vacío.</p>
          <p><a href="${pageContext.request.contextPath}/index.jsp">← Volver a la tienda</a></p>
        </div>
      </c:when>

      <%-- ── CARRITO CON PRODUCTOS ── --%>
      <c:otherwise>
        <div class="cart-layout">

          <!-- LISTA DE ITEMS -->
          <div>
            <div class="cart-items" id="cartItems">

              <%-- Iteramos sobre la lista de LineaCarritoDTO que viene en el modelo --%>
              <c:forEach var="linea" items="${carrito}" varStatus="st">
                <div class="cart-item" id="item-${linea.idProducto}">

                  <div class="item-thumb">${linea.icono}</div>

                  <div class="item-info">
                    <div class="item-name">${linea.nombreProducto}</div>
                    <div class="item-brand">${linea.marca}</div>
                    <div class="item-unit-price">
                      <fmt:formatNumber value="${linea.precioUnitario}" type="currency"
                                        currencySymbol="€" maxFractionDigits="2" /> / ud.
                    </div>
                  </div>

                  <div class="item-controls">
                    <div class="item-subtotal" id="sub-${linea.idProducto}">
                      <fmt:formatNumber value="${linea.subtotal}" type="currency"
                                        currencySymbol="€" maxFractionDigits="2" />
                    </div>

                    <div class="qty-control">
                      <button class="qty-btn"
                              onclick="updateQty(${linea.idProducto}, -1, ${linea.precioUnitario})">−</button>
                      <span class="qty-display" id="qty-${linea.idProducto}">${linea.cantidad}</span>
                      <button class="qty-btn"
                              onclick="updateQty(${linea.idProducto}, +1, ${linea.precioUnitario})">+</button>
                    </div>

                    <button class="btn-remove"
                            onclick="removeItem(${linea.idProducto})">🗑 Eliminar</button>
                  </div>
                </div>
              </c:forEach>

            </div>

            <div class="cart-actions-bar" style="margin-top:1rem;">
              <button class="btn-clear-cart" onclick="clearCart()">🗑 Vaciar carrito</button>
            </div>
          </div>

          <!-- RESUMEN -->
          <div class="cart-summary">
            <div class="summary-title">Resumen del pedido</div>

            <div class="summary-row">
              <span>Subtotal</span>
              <span class="amount" id="subtotalDisplay">
                <fmt:formatNumber value="${resumen.subtotal}" type="currency"
                                  currencySymbol="€" maxFractionDigits="2" />
              </span>
            </div>
            <div class="summary-row">
              <span>IVA (21%)</span>
              <span class="amount" id="ivaDisplay">
                <fmt:formatNumber value="${resumen.iva}" type="currency"
                                  currencySymbol="€" maxFractionDigits="2" />
              </span>
            </div>
            <div class="summary-row total">
              <span>Total</span>
              <span class="amount" id="totalDisplay">
                <fmt:formatNumber value="${resumen.total}" type="currency"
                                  currencySymbol="€" maxFractionDigits="2" />
              </span>
            </div>

            <button class="btn-checkout"
                    onclick="window.location='${pageContext.request.contextPath}/CheckoutServlet'">
              Finalizar compra →
            </button>

            <div class="checkout-note">
              <c:choose>
                <c:when test="${empty sessionScope.usuarioDTO}">
                  Necesitas <a href="${pageContext.request.contextPath}/login.jsp">iniciar sesión</a>
                  para completar tu pedido.
                </c:when>
                <c:otherwise>
                  Comprando como <strong>${sessionScope.usuarioDTO.email}</strong>
                </c:otherwise>
              </c:choose>
            </div>
          </div>

        </div>
      </c:otherwise>
    </c:choose>

  </div><!-- /cart-wrapper -->

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
    // Mapa de precios unitarios para recalcular en cliente
    // Los precios reales vienen del servidor; el JS solo actualiza la vista
    const prices = {};

    // Inicializar precios desde data attributes generados por JSTL
    document.querySelectorAll('.cart-item').forEach(el => {
      const id = el.id.replace('item-', '');
      const qtyEl = document.getElementById('qty-' + id);
      const subEl = document.getElementById('sub-' + id);
      if (qtyEl && subEl) {
        // Extraer precio unitario del texto "X,XX €" del subtotal dividido entre qty
        const qty = parseInt(qtyEl.textContent);
        const sub = parseFloat(subEl.textContent.replace(',', '.').replace(' €', '').replace(/\./g, ''));
        prices[id] = sub / qty;
      }
    });

    function fmt(n) {
      return n.toFixed(2).replace('.', ',') + ' €';
    }

    function updateQty(id, delta, unitPrice) {
      const qtyEl = document.getElementById('qty-' + id);
      let qty = Math.max(1, parseInt(qtyEl.textContent) + delta);
      qtyEl.textContent = qty;
      prices[id] = unitPrice;
      document.getElementById('sub-' + id).textContent = fmt(unitPrice * qty);
      recalculate();

      // Notificar al servidor vía fetch para actualizar la sesión
      fetch('${pageContext.request.contextPath}/CarritoServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'accion=actualizar&idProducto=' + id + '&cantidad=' + qty
      });
    }

    function removeItem(id) {
      document.getElementById('item-' + id).remove();
      delete prices[id];
      recalculate();
      if (document.querySelectorAll('.cart-item').length === 0) showEmpty();

      fetch('${pageContext.request.contextPath}/CarritoServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'accion=eliminar&idProducto=' + id
      });
    }

    function clearCart() {
      document.getElementById('cartItems').innerHTML = '';
      for (const k in prices) delete prices[k];
      showEmpty();

      fetch('${pageContext.request.contextPath}/CarritoServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'accion=vaciar'
      });
    }

    function showEmpty() {
      document.getElementById('cartItems').innerHTML =
        '<div class="empty-cart">' +
          '<div class="icon">🛒</div>' +
          '<p>Tu carrito está vacío.</p>' +
          '<p><a href="${pageContext.request.contextPath}/index.jsp">← Volver a la tienda</a></p>' +
        '</div>';
      document.getElementById('subtotalDisplay').textContent = '0,00 €';
      document.getElementById('ivaDisplay').textContent      = '0,00 €';
      document.getElementById('totalDisplay').textContent    = '0,00 €';
    }

    function recalculate() {
      const sub = Object.entries(prices).reduce((acc, [id, price]) => {
        const qty = parseInt(document.getElementById('qty-' + id)?.textContent || 0);
        return acc + price * qty;
      }, 0);
      const iva = sub * 0.21;
      document.getElementById('subtotalDisplay').textContent = fmt(sub);
      document.getElementById('ivaDisplay').textContent      = fmt(iva);
      document.getElementById('totalDisplay').textContent    = fmt(sub + iva);
    }
  </script>

</body>
</html>

