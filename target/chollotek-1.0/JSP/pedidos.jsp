<%-- 
    Document   : pedidos
    Created on : 20 feb. 2026, 0:53:37
    Author     : Alba
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:if test="${empty sessionScope.usuario}">
    <c:redirect url="${pageContext.request.contextPath}/FrontController?accion=verLogin"/>
</c:if>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis pedidos - Chollotek</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilo.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Orbitron:wght@400;700;900&family=Exo+2:wght@300;400;600;700&display=swap" rel="stylesheet">
</head>
<body>

    <jsp:include page="../INC/header.jsp" />

    <div class="container" style="flex-direction: column;">
        <h2 style="font-family:'Orbitron',monospace; color:var(--neon-pink);
                   text-shadow:0 0 10px var(--neon-pink); margin-bottom:1.5rem;">
            📦 Mis pedidos
        </h2>

        <c:choose>
            <c:when test="${empty pedidos}">
                <div class="no-pedidos">
                    <p>Aún no has realizado ningún pedido.</p>
                    <a href="${pageContext.request.contextPath}/FrontController?accion=inicio"
                       class="btn btn-primary">Ir a comprar</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="pedidos-lista">
                    <c:forEach items="${pedidos}" var="pedido">
                        <div class="pedido-card">
                            <div class="pedido-header">
                                <div>
                                    <strong>Pedido #${pedido.idpedido}</strong>
                                    <span class="pedido-fecha">
                                        <fmt:formatDate value="${pedido.fecha}" pattern="dd/MM/yyyy"/>
                                    </span>
                                </div>
                                <div class="pedido-total">
                                    <fmt:formatNumber value="${pedido.importe + pedido.iva}"
                                                      type="currency" currencySymbol="€"/>
                                </div>
                            </div>

                            <div class="pedido-body">
                                <p><strong>Estado:</strong> Finalizado ✅</p>
                                <p><strong>Subtotal:</strong>
                                    <fmt:formatNumber value="${pedido.importe}"
                                                      type="currency" currencySymbol="€"/>
                                </p>
                                <p><strong>IVA:</strong>
                                    <fmt:formatNumber value="${pedido.iva}"
                                                      type="currency" currencySymbol="€"/>
                                </p>
                            </div>

                            <div class="pedido-footer">
                                <%-- ✅ FIX: POST en lugar de GET con idpedido en la URL --%>
                                <form method="post"
                                      action="${pageContext.request.contextPath}/PedidoController">
                                    <input type="hidden" name="accion"    value="verDetalle">
                                    <input type="hidden" name="idpedido"  value="${pedido.idpedido}">
                                    <button type="submit" class="btn btn-sm btn-secondary">
                                        Ver detalle →
                                    </button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <jsp:include page="../INC/footer.jsp" />

</body>
</html>

