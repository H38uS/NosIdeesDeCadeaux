<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal>
		<jsp:body>
		<c:forEach var="user" items="${users}">
			<c:if test="${userid == user.id}">
				<h2>Mes idées de cadeaux</h2>
			</c:if>
			<c:if test="${userid != user.id}">
				<h2>Liste de cadeaux de ${user.name}</h2>
			</c:if>
			<c:if test="${fn:length(user.ideas) > 0}">
				<table>
					<thead>
						<tr>
							<th>Type</th>
							<th>Idée</th>
						</tr>
					</thead>
					<c:forEach var="idee" items="${user.ideas}">
						<tr>
							<td>
								<c:if test="${not empty idee.category}">
									<img src="public/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}" />
								</c:if>
							</td>
							<td>${idee.text}</td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
			<c:if test="${fn:length(user.ideas) == 0}">
				<span>${user.name} n'a pas encore d'idées.</span>
			</c:if>
		</c:forEach>
		<div>
			<a href="public/index.jsp">Retour à l'accueil</a> ou <a href="protected/index.jsp">Retour à votre espace</a>.
		</div>
	</jsp:body>
</t:normal>