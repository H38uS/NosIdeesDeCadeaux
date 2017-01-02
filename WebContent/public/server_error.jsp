<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:normal_public>
	<jsp:body>
		<h2>Une erreur serveur est survenue</h2>
		<p>Les développeurs, ce n'est plus ce que c'était.</p>
		<p>Veuillez réessayer dans quelques minutes.</p>
		<p>L'erreur : ${error}</p>
	</jsp:body>
</t:normal_public>