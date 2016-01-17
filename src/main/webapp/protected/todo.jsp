<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:normallight>
	<jsp:body>
		<h2>Page en cours de construction...</h2>
		<div>Circulez, travail en cours...</div>
		<div>
			Bonjour <c:out value="${username}" /> - 
			<a href="${pageContext.request.contextPath}/logout">me deconnecter.</a>
		</div>
	</jsp:body>
</t:normallight>
<a href="public/index.jsp">Retour à l'accueil.</a>