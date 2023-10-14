<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>
        <title>List of Currencies</title>
    </head>
    <body>
        <table>
            <thead>
                <tr>
                    <th>Currency Code</th>
                    <th>Currency Name</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${symbolList}" var="symbolItem">
                    <tr>
                        <td>${symbolItem.currencyCode}</td>
                        <td>${symbolItem.currencyName}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </body>
</html>