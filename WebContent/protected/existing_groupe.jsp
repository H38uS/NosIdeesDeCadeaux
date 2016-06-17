<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:normal>
	<jsp:body>
		<h2>Erreur : vous avez déjà un groupe</h2>
		<div>Vous avez déjà créé un groupe, vous ne pouvez donc pas en créer un deuxième...</div>
		<a href="public/index.jsp">Retour à l'accueil</a> ou accéder à la <a href="protected/administration_groupe">page d'administration</a> de votre groupe. 
	</jsp:body>
</t:normal>