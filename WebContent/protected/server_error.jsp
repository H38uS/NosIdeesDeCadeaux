<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<t:normal_protected>
	<jsp:body>
		<h2>Une erreur serveur est survenue</h2>
		<p>Les développeurs, ce n'est plus ce que c'était.</p>
		<p>Veuillez réessayer dans quelques minutes.</p>
		<c:if test="${shouldLogStack}">
			<p>L'erreur : ${error}</p>
		</c:if>
	</jsp:body>
</t:normal_protected>