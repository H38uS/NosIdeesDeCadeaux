<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:normal>
	<jsp:body>
		<h2>Succès !</h2>
		<div>Le groupe <c:out value="${name}" /> a bien été créé.</div>
		<a href="public/index.jsp">Retour à l'accueil.</a>
	</jsp:body>
</t:normal>