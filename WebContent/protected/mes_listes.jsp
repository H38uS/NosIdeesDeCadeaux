<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<t:normal>
		<jsp:body>
		<c:forEach var="user" items="${users}">
			<c:if test="${userid == user.id}">
				<!-- Début idée de la personne -->
				<h2>Mes idées de cadeaux</h2>
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
								<td>${idee.html}</td>
							</tr>
						</c:forEach>
					</table>
				</c:if>
				<c:if test="${fn:length(user.ideas) == 0}">
					<span>Vous n'avez pas encore d'idées. Cliquez <a href="protected/ma_liste">ici</a> pour en ajouter.</span>
				</c:if>
			</c:if>
			<!-- Fin idée de la personne -->

			<c:if test="${userid != user.id}">
				<h2>Liste de cadeaux de ${user.name}</h2>
				<c:if test="${fn:length(user.ideas) > 0}">
					<table>
						<thead>
							<tr>
								<th>Type</th>
								<th>Idée</th>
								<th>Réservation</th>
							</tr>
						</thead>
						<c:forEach var="idee" items="${user.ideas}">
							<tr>
								<td>
									<c:if test="${not empty idee.category}">
										<img src="public/image/type/${idee.category.image}" title="${idee.category.title}" alt="${idee.category.alt}" />
									</c:if>
								</td>
								<td>${idee.html}</td>
								<td>
									<c:if test="${not empty idee.bookingOwner}">
										Réservée par ${idee.bookingOwner.name}
									</c:if>
									<c:if test="${empty idee.bookingOwner}">
										L'idée n'a pas encore été réservée. <a href="protected/mes_listes?action=reserver&idee=${idee.id}">Je veux la réserver !</a>
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</table>
				</c:if>
				<c:if test="${fn:length(user.ideas) == 0}">
					<span>${user.name} n'a pas encore d'idées.</span>
				</c:if>
			</c:if>

		</c:forEach>
		<div>
			<a href="public/index.jsp">Retour à l'accueil</a> ou <a href="protected/index.jsp">Retour à votre espace</a>.
		</div>
	</jsp:body>
</t:normal>