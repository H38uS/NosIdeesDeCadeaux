<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:normal_protected>
	<jsp:body>
		<h2>Impossible d'envoyer une demande à ${name}...</h2>
		<div>${error_message}</div>
		<a href="${link}">Retourner</a> voir les listes partagées.
	</jsp:body>
</t:normal_protected>