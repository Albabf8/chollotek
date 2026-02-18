<%-- 
    Document   : index
    Created on : 6 feb. 2026, 17:47:23
    Author     : Alba
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Chollotek - Inicio</title>
        <link rel="stylesheet" href="CSS/estilos.css">
    </head>
    <body>
        <header>
            <h1>Bienvenido a Chollotek</h1>
        </header>

        <main style="display: flex;">
            <aside style="width: 20%; padding: 10px; border-right: 1px solid #ccc;">
                <h3>Categorías</h3>
                <ul>
                    <c:forEach var="categoria" items="${categorias}">
                        <li>
                            <a href="FrontController?action=verProductos&id=${categoria.idCategoria}">
                                ${categoria.nombre}
                            </a>
                        </li>
                    </c:forEach>
                </ul>
            </aside>

            <section style="width: 80%; padding: 10px;">
                <h2>Productos</h2>
                <div style="display: flex; flex-wrap: wrap;">
                    <c:forEach var="prod" items="${productosAMostrar}">
                        <div style="border: 1px solid #ccc; margin: 10px; padding: 10px; width: 200px;">
                            <%-- El $ debe ir antes de la llave para que Java lo reconozca --%>
                            <img src="${pageContext.request.contextPath}/IMAGE/productos/${prod.imagen}" 
                                 alt="${prod.nombre}" 
                                 style="width: 100%;">
                            <h4>${prod.nombre}</h4>
                            <p>${prod.precio} €</p>
                            <button>Añadir al carrito</button>
                        </div>
                    </c:forEach>
                </div>
            </section>
        </main>
    </body>
</html>