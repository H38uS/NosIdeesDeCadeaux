<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<t:template_head_includes>
	<link rel="stylesheet" type="text/css" href="resources/css/lib/thickbox.css" />
	<script src="resources/js/lib/thickbox.js" type="text/javascript"></script>
</t:template_head_includes>
<t:template_body_protected>
	<jsp:body>
		<h4>Réservations en cours</h4>
		<div id="reservation_res_area"></div>
		<script src="resources/js/reservations.js" type="text/javascript"></script>
	</jsp:body>
</t:template_body_protected>
</html>
