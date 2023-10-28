<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>
        <title>List of Currencies</title>
        <link href="<c:url value="/css/common.css"/>" rel="stylesheet" type="text/css">
    </head>
    <body>
        <table>
            <thead>
                <tr>
                    <th>Currency From</th>
                    <th>Currency To</th>
                    <th>Date</th>
                    <th>Rate Of Exchange</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${latestList}" var="latestItem">
                    <tr>
                        <td>${latestItem.fromCurrency}</td>
                        <td>${latestItem.toCurrency}</td>
                        <td>${latestItem.date}</td>
                        <td>${latestItem.rate}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </body>
</html>