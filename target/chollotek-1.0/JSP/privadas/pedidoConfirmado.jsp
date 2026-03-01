<%-- Document : pedidoConfirmado Created on : 1 mar. 2026 Author : Alba --%>

    <%@ page contentType="text/html" pageEncoding="UTF-8" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

            <!-- Verificar que el usuario está logueado -->
            <c:if test="${empty sessionScope.usuario}">
                <c:redirect url="${pageContext.request.contextPath}/FrontController?accion=verLogin" />
            </c:if>

            <!DOCTYPE html>
            <html lang="es">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Pedido Confirmado - Chollotek</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estilo.css">
            </head>

            <body>

                <jsp:include page="../INC/header.jsp" />

                <div class="container" style="text-align: center; padding: 50px 0;">
                    <h2 style="color: #4CAF50; font-size: 2em; margin-bottom: 20px;">🎉 ¡Gracias por tu compra! 🎉</h2>

                    <c:if test="${not empty requestScope.mensajeExito}">
                        <div class="alert alert-success" style="display: inline-block; text-align: left;">
                            ${requestScope.mensajeExito}
                        </div>
                    </c:if>

                    <p style="margin-top: 20px; font-size: 1.2em;">
                        Tu pedido se ha tramitado correctamente. Puedes consultar los detalles en tu historial de
                        pedidos.
                    </p>

                    <div style="margin-top: 40px; display: flex; justify-content: center; gap: 20px;">
                        <a href="${pageContext.request.contextPath}/FrontController?accion=inicio"
                            class="btn btn-primary">
                            Seguir comprando
                        </a>
                        <a href="${pageContext.request.contextPath}/PedidoController?accion=verPedidos"
                            class="btn btn-secondary">
                            Ver mis pedidos
                        </a>
                    </div>
                </div>

                <jsp:include page="../INC/footer.jsp" />

            </body>

            </html>