<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>JQuery Datatable Example</title>
<script src="https://code.jquery.com/jquery-3.7.0.js"
	integrity="sha256-JlqSTELeR4TLqP0OG9dxM7yDPqX1ox/HfgiSLBj8+kM="
	crossorigin="anonymous"></script>
<link
	href="https://cdn.datatables.net/v/dt/jq-3.7.0/dt-1.13.7/datatables.min.css"
	rel="stylesheet">
<script
	src="https://cdn.datatables.net/v/dt/jq-3.7.0/dt-1.13.7/datatables.min.js"></script>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css"
	integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"
	crossorigin="anonymous">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js"
	integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
	crossorigin="anonymous"></script>
<script type="text/javascript">
		$(document).ready(function() {
		    $('#symbols_table').DataTable();
		} );
	</script>
</head>
<body>
	<table id="symbols_table">
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