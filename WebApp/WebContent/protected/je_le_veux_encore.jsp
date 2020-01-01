<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<t:normal_protected>
		<jsp:body>
		<h2>Succès !</h2>
		<p>
			Toutes les réservations (s'il y en avait) ont bien été supprimées sur cette idée.
			Une notification a aussi été envoyé pour bien les prévenir !
		</p>
		<p>
			Retourner à la <a href="${from}">page précédente</a>.
		</p>
		</jsp:body>
</t:normal_protected>