<%-- 
    Document   : pedido
    Created on : 20 feb. 2026, 0:53:37
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
      <button class="nav-btn"
              onclick="location.href='${pageContext.request.contextPath}/carrito.jsp'">
        <span>🛒</span> Mi carrito
      </button>
      <a href="${pageContext.request.contextPath}/LogoutServlet">
        <button class="nav-btn"><span>🚪</span> Cerrar sesión</button>
      </a>
    </div>
  </nav>

  <div class="orders-wrapper">

    <div class="page-header">
      <div class="page-title">Mis pedidos</div>
      <a href="${pageContext.request.contextPath}/index.jsp" class="btn-escape">← Volver a la tienda</a>
    </div>

    <%-- Estado vacío: el usuario no tiene pedidos todavía --%>
    <c:if test="${empty pedidos}">
      <div class="empty-state">
        <div class="icon">📦</div>
        <p>Todavía no has realizado ningún pedido.</p>
        <p><a href="${pageContext.request.contextPath}/index.jsp">← Ir a la tienda</a></p>
      </div>
    </c:if>

    <%-- Lista de pedidos: itera sobre List<PedidoDTO> pasada por el Servlet --%>
    <c:forEach var="pedido" items="${pedidos}">
      <div class="order-card" id="order-${pedido.id}">

        <%-- CABECERA DEL PEDIDO --%>
        <div class="order-header" onclick="toggleOrder('order-${pedido.id}')">
          <div>
            <div class="order-id">#PED-${pedido.referencia}</div>
            <div class="order-date">
              <fmt:formatDate value="${pedido.fecha}" pattern="dd 'de' MMMM 'de' yyyy"
                              type="date" />
            </div>
          </div>
          <div class="order-meta">
            <%-- Badge de estado dinámico --%>
            <span class="order-status status-${pedido.estadoCss}">${pedido.estadoLabel}</span>
            <span class="order-total">
              <fmt:formatNumber value="${pedido.total}" type="currency"
                                currencySymbol="€" maxFractionDigits="2" />
            </span>
            <span class="order-chevron">▼</span>
          </div>
        </div>

        <%-- CUERPO DEL PEDIDO: líneas de detalle --%>
        <div class="order-body">
          <table class="order-lines">
            <thead>
              <tr>
                <th>Producto</th>
                <th class="td-right">Precio</th>
                <th class="td-right">Cant.</th>
                <th class="td-right">Subtotal</th>
              </tr>
            </thead>
            <tbody>
              <%-- Itera sobre List<LineaPedidoDTO> anidada en cada pedido --%>
              <c:forEach var="linea" items="${pedido.lineas}">
                <tr>
                  <td>${linea.nombreProducto}</td>
                  <td class="td-right">
                    <fmt:formatNumber value="${linea.precioUnitario}" type="currency"
                                      currencySymbol="€" maxFractionDigits="2" />
                  </td>
                  <td class="td-right">${linea.cantidad}</td>
                  <td class="td-right line-total">
                    <fmt:formatNumber value="${linea.subtotal}" type="currency"
                                      currencySymbol="€" maxFractionDigits="2" />
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>

          <div class="order-footer-row">
            <div class="order-iva-note">
              IVA incluido (21%):
              <fmt:formatNumber value="${pedido.iva}" type="currency"
                                currencySymbol="€" maxFractionDigits="2" />
            </div>
            <div class="order-grand-total">
              Total:
              <span>
                <fmt:formatNumber value="${pedido.total}" type="currency"
                                  currencySymbol="€" maxFractionDigits="2" />
              </span>
            </div>
          </div>
        </div>

      </div>
    </c:forEach>

  </div><!-- /orders-wrapper -->

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
    function toggleOrder(id) {
      document.getElementById(id).classList.toggle('open');
    }
  </script>

</body>
</html>

