<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:normal_protected>
	<jsp:body>
		<h2>Page en cours de construction...</h2>
		<div>Circulez, travail en cours...</div>
		<div>
			Bonjour ${username} - 
			<a href="<c:url value="/logout" />">me deconnecter.</a>
		</div>
		<a href="public/index.jsp">Retour à l'accueil.</a>
		<div>
			<a href="protected/index.jsp">Retour à mon espace.</a>
		</div>
	</jsp:body>
</t:normal_protected>