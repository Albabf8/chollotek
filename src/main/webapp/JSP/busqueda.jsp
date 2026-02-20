<%-- 
    Document   : busqueda
    Created on : 20 feb. 2026, 0:49:41
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
  <title>Chollotek — Búsqueda</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/busqueda.css" />
</head>
<body>

  <!-- NAV -->
  <nav>
    <div class="logo">CHOLLOTEK</div>
    <div class="search-bar">
      <%-- El valor del campo refleja la query actual recibida por el Servlet --%>
      <input type="text" placeholder="Buscar productos…"
             value="${not empty filtroDTO.query ? filtroDTO.query : ''}"
             onkeydown="if(event.key==='Enter') buscar(this.value)" />
    </div>
    <div class="nav-actions">
      <button class="nav-btn"><span>👤</span> Mi cuenta</button>
      <button class="nav-btn"><span>🛒</span> Mi carrito</button>
    </div>
  </nav>

  <div class="search-wrapper">

    <!-- Cabecera de resultados -->
    <div class="search-header">
      <div class="search-query-info">
        Resultados para: <strong>"${filtroDTO.query}"</strong>
      </div>
      <div class="result-count">
        <c:choose>
          <c:when test="${totalResultados == 0}">Sin resultados</c:when>
          <c:when test="${totalResultados == 1}">1 producto encontrado</c:when>
          <c:otherwise>${totalResultados} productos encontrados</c:otherwise>
        </c:choose>
      </div>
    </div>

    <div class="search-layout">

      <!-- FILTROS -->
      <form action="${pageContext.request.contextPath}/BusquedaServlet" method="get"
            id="formFiltros">
        <%-- Mantener la query en el formulario de filtros --%>
        <input type="hidden" name="q" value="${filtroDTO.query}" />

        <div class="filters-sidebar">
          <div class="filters-title">Filtros</div>

          <%-- CATEGORÍAS --%>
          <div class="filter-group">
            <div class="filter-group-title">Categoría</div>
            <c:forEach var="cat" items="${categorias}">
              <div class="filter-option">
                <input type="checkbox" name="categoria" id="cat-${cat.id}"
                       value="${cat.id}"
                       ${filtroDTO.categorias.contains(cat.id) ? 'checked' : ''} />
                <label for="cat-${cat.id}">${cat.nombre} (${cat.total})</label>
              </div>
            </c:forEach>
          </div>

          <%-- PRECIO --%>
          <div class="filter-group">
            <div class="filter-group-title">Precio</div>
            <div class="price-range-row">
              <input type="number" name="precioMin" placeholder="0" min="0"
                     value="${not empty filtroDTO.precioMin ? filtroDTO.precioMin : 0}" />
              <span class="price-range-sep">—</span>
              <input type="number" name="precioMax" placeholder="500" min="0"
                     value="${not empty filtroDTO.precioMax ? filtroDTO.precioMax : 500}" />
              <span class="price-range-sep">€</span>
            </div>
          </div>

          <%-- MARCAS --%>
          <div class="filter-group">
            <div class="filter-group-title">Marca</div>
            <c:forEach var="marca" items="${marcas}">
              <div class="filter-option">
                <input type="checkbox" name="marca" id="marca-${marca.id}"
                       value="${marca.id}"
                       ${filtroDTO.marcas.contains(marca.id) ? 'checked' : ''} />
                <label for="marca-${marca.id}">${marca.nombre} (${marca.total})</label>
              </div>
            </c:forEach>
          </div>

          <%-- ORDENAR --%>
          <div class="filter-group">
            <div class="filter-group-title">Ordenar por</div>
            <div class="filter-option">
              <input type="radio" name="orden" id="s1" value="relevancia"
                     ${filtroDTO.orden == 'relevancia' || empty filtroDTO.orden ? 'checked' : ''} />
              <label for="s1">Relevancia</label>
            </div>
            <div class="filter-option">
              <input type="radio" name="orden" id="s2" value="precio_asc"
                     ${filtroDTO.orden == 'precio_asc' ? 'checked' : ''} />
              <label for="s2">Precio ↑</label>
            </div>
            <div class="filter-option">
              <input type="radio" name="orden" id="s3" value="precio_desc"
                     ${filtroDTO.orden == 'precio_desc' ? 'checked' : ''} />
              <label for="s3">Precio ↓</label>
            </div>
            <div class="filter-option">
              <input type="radio" name="orden" id="s4" value="nombre_az"
                     ${filtroDTO.orden == 'nombre_az' ? 'checked' : ''} />
              <label for="s4">Nombre A-Z</label>
            </div>
          </div>

          <button type="submit" class="btn-apply-filters">Aplicar filtros</button>
          <button type="button" class="btn-reset-filters"
                  onclick="window.location='${pageContext.request.contextPath}/BusquedaServlet?q=${filtroDTO.query}'">
            Limpiar filtros
          </button>
        </div>
      </form>

      <!-- RESULTADOS -->
      <div class="results-area">

        <div class="results-toolbar">
          <%-- Tags de filtros activos --%>
          <div class="active-filters">
            <c:forEach var="cat" items="${filtroDTO.categorias}">
              <span class="filter-tag">${cat} <span class="remove">✕</span></span>
            </c:forEach>
            <c:forEach var="marca" items="${filtroDTO.marcas}">
              <span class="filter-tag">${marca} <span class="remove">✕</span></span>
            </c:forEach>
          </div>
          <div style="display:flex;gap:.75rem;align-items:center;">
            <div class="view-toggle">
              <button class="view-btn active" title="Cuadrícula">⊞</button>
              <button class="view-btn" title="Lista">☰</button>
            </div>
          </div>
        </div>

        <%-- Sin resultados --%>
        <c:if test="${empty productos}">
          <div class="no-results">
            <div class="icon">🔍</div>
            <p>No se encontraron productos para <strong>"${filtroDTO.query}"</strong>.</p>
            <p>Prueba con otros términos o elimina algunos filtros.</p>
          </div>
        </c:if>

        <%-- Grid de productos --%>
        <c:if test="${not empty productos}">
          <div class="product-grid">
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
                          onclick="window.location='${pageContext.request.contextPath}/ProductoServlet?id=${p.id}'">
                    Ver
                  </button>
                  <button class="btn-buy"
                          onclick="window.location='${pageContext.request.contextPath}/CarritoServlet?accion=agregar&idProducto=${p.id}'">
                    Comprar
                  </button>
                </div>
              </div>
            </c:forEach>
          </div>
        </c:if>

      </div><!-- /results-area -->
    </div><!-- /search-layout -->
  </div><!-- /search-wrapper -->

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
    function buscar(query) {
      if (query.trim()) {
        window.location = '${pageContext.request.contextPath}/BusquedaServlet?q=' +
                          encodeURIComponent(query.trim());
      }
    }
  </script>

</body>
</html>

