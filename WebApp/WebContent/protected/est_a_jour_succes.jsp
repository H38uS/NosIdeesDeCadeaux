<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<t:normal_protected>
	<jsp:body>
		<h2>Succès !</h2>
		<div>Une demande à ${user} a bien été envoyé concernant son idée "${text}".</div>
		<a href="${link}">Retourner</a> à la page précédente.
	</jsp:body>
</t:normal_protected>