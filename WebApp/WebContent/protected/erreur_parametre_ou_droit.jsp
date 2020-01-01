<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:normal_protected>
	<jsp:body>
		<h2>Erreur lors de l'affichage de la page</h2>
		<div>Raison : ${error_message}</div>
	</jsp:body>
</t:normal_protected>