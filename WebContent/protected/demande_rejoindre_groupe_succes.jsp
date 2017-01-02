<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<t:normal_proctected>
	<jsp:body>
		<h2>Succès !</h2>
		<div>Une demande pour rejoindre le groupe ${name} vient d'être envoyée.</div>
		<a href="public/index.jsp">Retour à l'accueil.</a>
	</jsp:body>
</t:normal_proctected>