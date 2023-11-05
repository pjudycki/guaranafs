<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>
        <title>Guarana Financial Services</title>
        <link href="<c:url value="/css/common.css"/>" rel="stylesheet" type="text/css">
        <style>
             body {
                background-image: url("background.jpg");
                background-position: center;
                background-repeat: no-repeat;
             }
        </style>
    </head>
    <body>
       <div align = "center" style="font-size:40px;"><b> Welcome to Guarana Financial Services </b></div>
                <table>
                   <thead>
                       <tr>
                           <th>Service Name</th>
                           <th>Link</th>
                       </tr>
                   </thead>
                   <tbody>
                       <tr>
                           <td>List of Available Symbols</td>
                           <td>
                           <br>
                                <form action="./viewSymbols"><input type="submit" value="Symbols"/></form>
                           </td>
                       </tr>
                       <tr>
                           <td>Latest Rates of Exchange</td>
                           <td>
                           <br>
                               <form action="./viewLatest"><input type="submit" value="Latest"/></form>
                           </td>
                       </tr>
                      <tr>
                          <td>Historical Rates of Exchange</td>
                          <td>
                          <br>
                              <form action="./viewHistorical"><input type="submit" value="Historical"/></form>
                          </td>
                      </tr>
                   </tbody>
               </table>
    </body>
</html>