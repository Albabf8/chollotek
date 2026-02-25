<%-- 
    Document   : pedido
    Created on : 20 feb. 2026, 0:53:37
    Author     : Alba
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!-- Verificar sesión -->
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
</head>
<body>
    
    <jsp:include page="../INC/header.jsp" />

    <div class="container">
        <h2>📦 Mis pedidos</h2>

        <c:choose>
            <c:when test="${empty pedidos}">
                <div class="no-pedidos">
                    <p>Aún no has realizado ningún pedido.</p>
                    <a href="${pageContext.request.contextPath}/FrontController?accion=inicio" 
                       class="btn btn-primary">
                        Ir a comprar
                    </a>
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
                                                     type="currency" 
                                                     currencySymbol="€"/>
                                </div>
                            </div>

                            <div class="pedido-body">
                                <p><strong>Estado:</strong> Finalizado ✅</p>
                                <p><strong>Subtotal:</strong> 
                                    <fmt:formatNumber value="${pedido.importe}" type="currency" currencySymbol="€"/>
                                </p>
                                <p><strong>IVA:</strong> 
                                    <fmt:formatNumber value="${pedido.iva}" type="currency" currencySymbol="€"/>
                                </p>
                            </div>

                            <div class="pedido-footer">
                                <a href="${pageContext.request.contextPath}/PedidoController?accion=verDetalle&idpedido=${pedido.idpedido}" 
                                   class="btn btn-sm btn-secondary">
                                    Ver detalle
                                </a>
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

