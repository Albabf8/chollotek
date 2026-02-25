<%-- 
    Document   : footer
    Created on : 25 feb. 2026, 18:17:19
    Author     : Alba
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<footer class="footer">
    <div class="footer-container">
        <div class="footer-section">
            <h4>Sobre Chollotek</h4>
            <p>Tu tienda online de confianza para las mejores ofertas en tecnología.</p>
        </div>
        
        <div class="footer-section">
            <h4>Enlaces rápidos</h4>
            <ul>
                <li><a href="${pageContext.request.contextPath}/FrontController?accion=inicio">Inicio</a></li>
                <li><a href="${pageContext.request.contextPath}/JSP/contacto.jsp">Contacto</a></li>
                <li><a href="${pageContext.request.contextPath}/JSP/terminos.jsp">Términos y condiciones</a></li>
            </ul>
        </div>
        
        <div class="footer-section">
            <h4>Contacto</h4>
            <p>📧 info@chollotek.com</p>
            <p>📞 +34 927 123 456</p>
        </div>
    </div>
    
    <div class="footer-bottom">
        <p>&copy; 2026 Chollotek. Todos los derechos reservados.</p>
    </div>
</footer>
