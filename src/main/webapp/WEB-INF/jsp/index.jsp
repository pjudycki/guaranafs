<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>
        <title>Guarana Financial Services</title>
       <link href="<c:url value="/css/common.css"/>" rel="stylesheet" type="text/css">
<script
  src="https://code.jquery.com/jquery-3.7.1.min.js"
  integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo="
  crossorigin="anonymous"></script>

        <style>
             body {
                background-image: url("background.jpg");
                background-position: center;
                background-repeat: no-repeat;
             }
        </style>
        <script>
        $(document).ready(function() {
          $('#sidebar-btn').on('click', function() {
            $('#sidebar').toggleClass('visible');
          });
        });
        </script>
       </head>
    <body>
      <div id="sidebar">
        <ul>
          <li><a href="./viewSymbols">Symbols</a></li>
          <li><a href="./viewLatest">Latest</a></li>
        </ul>

        <div id="sidebar-btn">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
      <div class="title">
         <h1>Guarana Financial Services</h1>
      </div>
    </body>
</html>