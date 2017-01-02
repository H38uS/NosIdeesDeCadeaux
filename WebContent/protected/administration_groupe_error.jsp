<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:normal_proctected>
	<jsp:body>
		<h2>Une erreur est survenue...</h2>
		<div>${error_message}</div>
		<a href="public/index.jsp">Retour à l'accueil</a> ou accéder à la <a href="/protected/creation_groupe">page de création</a> pour créer votre groupe. 
	</jsp:body>
</t:normal_proctected>